package utils.render;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import utils.Logger;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Un shader es el programa utilizado para controlar la apariencia visual de los objetos en la escena 2D.
 * Existen 2 tipos de shaders:<br>
 * <b>1. Vertex Shader</b><br>
 * Este shader opera en cada vértice de un objeto y se utiliza para transformar y operar con la posición de los vértices.<br>
 * <b>2. Fragment Shader</b><br>
 * Este shader opera en cada pixel del objeto que se está renderizado y se utiliza par calcular el color de cada pixel (iluminación, texturización, reflejos...).
 *
 * @author Izan
 */
public enum Shader {
    /**
     * Shader utilizado para renderizar objetos dentro del mundo, cuya posición en pantalla depende de la posición de la cámara.
     */
    WORLD("assets/shaders/world/vertex.glsl", "assets/shaders/fog.glsl", false),

    /**
     * Shader utilizado para renderizar entidades instanciables dentro del mundo, cuya posición en pantalla depende de la posición de la cámara.
     */
    ENTITY("assets/shaders/entity/vertex.glsl", "assets/shaders/fog.glsl", true),

    /**
     * Shader utilizado para renderizar objetos sobre la pantalla. La posición de estos objetos siempre será la misma, sin importar la posición de la cámara.
     */
    HUD("assets/shaders/hud", true);

    private final boolean SUPPORTS_INSTANTATION;

    /**
     * Contenido del archivo .glsl del shader de vértices.
     */
    private String vertexShader = null;

    /**
     * Contenido del archivo .glsl del shader de fragmentos.
     */
    private String fragmentShader = null;

    /**
     * Identificador número del shader de vértices.
     */
    private int vertexID;

    /**
     * Identificador número del shader de fragmentos.
     */
    private int fragmentID;

    /**
     * Identificador número del programa de shader, con los shaders de vértices y fragmentos ya cargados.
     */
    private int shaderProgramID;

    /**
     * Lee los shaders de vértices y fragmentos, pero ni los compila ni crea el programa de shaders. OpenGL no puede acceder
     * a la memoria antes de que se haya inicializado GLSL, por lo que hay que compilar los shaders después de la inicialización.
     * @param sourceVertex dirección al archivo <code>.glsl</code> que contiene el código del shader de vértices.
     * @param sourceFragment dirección al archivo <code>.glsl</code> que contiene el código del shader de fragmentos.
     * @see Window
     */
    Shader(String sourceVertex, String sourceFragment, boolean supportsInstantiation) {
        try {
            this.vertexShader = Files.readString(Paths.get(sourceVertex));
        } catch (IOException exception) {
            Logger.sendMessage("Error cargando el shader: '%s'. El archivo '%s' no existe o no ha sido posible su lectura.", Logger.LogMessageType.WARNING, this, sourceVertex);
        }
        try {
            this.fragmentShader = Files.readString(Paths.get(sourceFragment));
        } catch (IOException exception) {
            Logger.sendMessage("Error cargando el shader: '%s'. El archivo '%s' no existe o no ha sido posible su lectura.", Logger.LogMessageType.WARNING, this, sourceFragment);
        }

        this.SUPPORTS_INSTANTATION = supportsInstantiation;
    }

    /**
     * Lee los shaders de vértices y fragmentos, pero ni los compila ni crea el programa de shaders. OpenGL no puede acceder
     * a la memoria antes de que se haya inicializado GLSL, por lo que hay que compilar los shaders después de la inicialización.
     * @param source Dirección a un directorio que contiene los archivos <code>vertex.glsl</code> y <code>fragment.glsl</code>,
     *               que contienen el código de los shaders de vértices y fragmentos, respectivamente.
     *
     * @see Window
     */
    Shader(String source, boolean supportsInstantiation) {
        this(source  + "/vertex.glsl", source + "/fragment.glsl", supportsInstantiation);
    }

    /**
     * Compila el shader y crea el programa de shaders, que se subirá a la GPU.
     */
    public void compile() {
        //Cargamos y compilamos el vertex shader
        if (this.vertexShader != null) {
            this.vertexID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
            GL20.glShaderSource(this.vertexID, this.vertexShader);
            GL20.glCompileShader(this.vertexID);
        }

        if (GL20.glGetShaderi(this.vertexID, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
            Logger.sendMessage("Error compilando el shader: '%s.vertex'. %s", Logger.LogMessageType.FATAL,this, GL20.glGetShaderInfoLog(this.vertexID));
        }

        //Cargamos y compilamos el fragment shader
        if (this.fragmentShader != null) {
            this.fragmentID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
            GL20.glShaderSource(this.fragmentID, this.fragmentShader);
            GL20.glCompileShader(this.fragmentID);
        }

        if (GL20.glGetShaderi(this.fragmentID, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
            Logger.sendMessage("Error compilando el shader: '%s.fragment'. %s", Logger.LogMessageType.FATAL, this, GL20.glGetShaderInfoLog(this.fragmentID));
        }

        this.shaderProgramID = GL20.glCreateProgram();
        GL20.glAttachShader(this.shaderProgramID, this.vertexID);
        GL20.glAttachShader(this.shaderProgramID, this.fragmentID);
        GL20.glLinkProgram(this.shaderProgramID);
    }

    public boolean supportsInstantiation() {
        return this.SUPPORTS_INSTANTATION;
    }

    /**
     * Establece este shader como el que se va a utilizar. Si hubiese uno anteriormente, se desecharía. Es necesario que
     * se haya compilado antes.
     */
    public void use() {
        GL20.glUseProgram(this.shaderProgramID);
    }

    /**
     * Desactiva cualquier shader que se estuviera usando en la GPU.
     */
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

    /**
     * Envía un vector tridimensional de flotantes al shader.
     * @param variableName Nombre de la variable en el shader (declarada con <code>uniform</code> en el archivo <code>.glsl</code>).
     * @param floatValue1 Primer componente del vector.
     * @param floatValue2 Segundo componente del vector.
     * @param floatValue3 Tercer componente del vector.
     */
    public void upload3f(String variableName, float floatValue1, float floatValue2, float floatValue3) {
        GL20.glUniform3f(GL20.glGetUniformLocation(this.shaderProgramID, variableName), floatValue1, floatValue2, floatValue3);
    }

    /**
     * Envía un vector bidimensional de flotantes al shader.
     * @param variableName Nombre de la variable en el shader (declarada con <code>uniform</code> en el archivo <code>.glsl</code>).
     * @param floatValue1 Primer componente del vector.
     * @param floatValue2 Segundo componente del vector.
     */
    public void upload2f(String variableName, float floatValue1, float floatValue2) {
        GL20.glUniform2f(GL20.glGetUniformLocation(this.shaderProgramID, variableName), floatValue1, floatValue2);
    }

    /**
     * Envía un valor flotante al shader.
     * @param variableName Nombre de la variable en el shader (declarada con <code>uniform</code> en el archivo <code>.glsl</code>).
     * @param floatValue Valor flotante a enviar.
     */
    public void uploadFloat(String variableName, float floatValue) {
        GL20.glUniform1f(GL20.glGetUniformLocation(this.shaderProgramID, variableName), floatValue);
    }

    /**
     * Envía un entero al shader.
     * @param variableName Nombre de la variable en el shader (declarada con <code>uniform</code> en el archivo <code>.glsl</code>).
     * @param intValue Valor entero a enviar.
     */
    public void uploadInt(String variableName, int intValue) {
        GL20.glUniform1i(GL20.glGetUniformLocation(this.shaderProgramID, variableName), intValue);
    }
}
