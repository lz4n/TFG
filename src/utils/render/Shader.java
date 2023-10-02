package utils.render;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public enum Shader {
    TEXTURE("assets/shaders/texture"),
    HUD("assets/shaders/hud");

    private String vertexShader = null, fragmentShader = null;
    private int vertexID, fragmentID, shaderProgramID;
    Shader(String source) {
        try {
            this.vertexShader = Files.readString(Paths.get(source + "/vertex.glsl"));
        } catch (IOException exception) {
            System.err.printf("Error cargando el shader: '%s.vertex'. El archivo '%s' no existe o no ha sido posible su lectura.\n", this, source + "/vertex.glsl");
        }
        try {
            this.fragmentShader = Files.readString(Paths.get(source + "/fragment.glsl"));
        } catch (IOException exception) {
            System.err.printf("Error cargando el shader: '%s.fragment'. El archivo '%s' no existe o no ha sido posible su lectura.\n", this, source + "/fragment.glsl");
        }
    }

    public void compile() {
        //Cargamos y compilamos el vertex shader
        if (this.vertexShader != null) {
            this.vertexID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
            GL20.glShaderSource(this.vertexID, this.vertexShader);
            GL20.glCompileShader(this.vertexID);
        }

        if (GL20.glGetShaderi(this.vertexID, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
            System.err.printf("Error compilando el shader: '%s.vertex'. %s\n", this, GL20.glGetShaderInfoLog(this.vertexID));
        }

        //Cargamos y compilamos el fragment shader
        if (this.fragmentShader != null) {
            this.fragmentID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
            GL20.glShaderSource(this.fragmentID, this.fragmentShader);
            GL20.glCompileShader(this.fragmentID);
        }

        if (GL20.glGetShaderi(this.fragmentID, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
            System.err.printf("Error compilando el shader: '%s.fargment'. %s\n", this, GL20.glGetShaderInfoLog(this.fragmentID));
        }

        this.shaderProgramID = GL20.glCreateProgram();
        GL20.glAttachShader(this.shaderProgramID, this.vertexID);
        GL20.glAttachShader(this.shaderProgramID, this.fragmentID);
        GL20.glLinkProgram(this.shaderProgramID);
    }

    public void use() {
        GL20.glUseProgram(this.shaderProgramID);
    }

    public static void detach() {
        GL20.glUseProgram(0);
    }

    /**
     * Envía una matriz 4x4 de flotantes al shader.
     * @param variableName Nombre de la variable en el shader (declarada con <code>uniform</code> en el archivo <code>.glsl</code>).
     * @param matrix4f Matriz 4x4 de flotantes a enviar.
     */
    public void uploadMatrix4f(String variableName, Matrix4f matrix4f) {
        int variableLocation = GL20.glGetUniformLocation(this.shaderProgramID, variableName);
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        matrix4f.get(matrixBuffer);
        GL20.glUniformMatrix4fv(variableLocation, false, matrixBuffer);
    }

    public void upload3f(String variableName, float floatValue1, float floatValue2, float floatValue3) {
        GL20.glUniform3f(GL20.glGetUniformLocation(this.shaderProgramID, variableName), floatValue1, floatValue2, floatValue3);
    }

    public void uploadFloat(String variableName, float floatValue) {
        GL20.glUniform1f(GL20.glGetUniformLocation(this.shaderProgramID, variableName), floatValue);
    }

    public void uploadInt(String variableName, int intValue) {
        GL20.glUniform1i(GL20.glGetUniformLocation(this.shaderProgramID, variableName), intValue);
    }

    public void uploadTexture(String variableName, int textureSlot) {
        GL20.glUniform1f(GL20.glGetUniformLocation(this.shaderProgramID, variableName), textureSlot);
    }
}
