package utils.render.scene;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import utils.Time;
import utils.render.Camera;
import utils.render.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class SquareScene extends Scene {
    private float[] vertexArray = {
            // position               // color
            100.5f, 0.5f, 0.0f,       1.0f, 0.0f, 0.0f, 1.0f, // Bottom right 0
            0.5f,  100.5f, 0.0f,       0.0f, 1.0f, 0.0f, 1.0f, // Top left     1
            100.5f,  100.5f, 0.0f ,      1.0f, 0.0f, 1.0f, 1.0f, // Top right    2
            0.5f, 0.5f, 0.0f,       1.0f, 1.0f, 0.0f, 1.0f, // Bottom left  3
    };

    private int[] elementArray = {
            2, 1, 0,
            0, 1, 3
    };

    private int vaoID, vboID, eboID;
    private Camera camera = new Camera(new Vector2f()); //Iniciamos la cámara en 0,0.

    public void init() {
        //Cargamos el shader
        Shader.DEFAULT.compile();

        //Generamos los VAO, VBO y EBO, y los mandamos a la GPU
        vaoID = ARBVertexArrayObject.glGenVertexArrays();
        ARBVertexArrayObject.glBindVertexArray(vaoID);

        //Creamos el buffer de vértices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //Creamos la VBO y la subimos al buffer
        vboID = GL20.glGenBuffers();
        GL15C.glBindBuffer(GL20.GL_ARRAY_BUFFER, vboID);
        GL15C.glBufferData(GL20.GL_ARRAY_BUFFER, vertexBuffer, GL20.GL_STATIC_DRAW);

        //Creamos los índices
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = GL20.glGenBuffers();
        GL15C.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, eboID);
        GL15C.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL20.GL_STATIC_DRAW);

        //Añadimos los atributos a los vertices
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
        GL20.glVertexAttribPointer(0, positionsSize, GL20.GL_FLOAT, false, vertexSizeBytes, 0);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(1, colorSize, GL20.GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        GL20.glEnableVertexAttribArray(1);
    }


    public void update(long dTime) {
        //Movemos la cámara
        /*camera.cameraPosition.x -= dTime * 3E-8f;
        camera.cameraPosition.y -= dTime * 3E-8f;*/

        Shader.DEFAULT.use();
        Shader.DEFAULT.uploadMatrix4f("uProjection", camera.getProjectionMatrix());
        Shader.DEFAULT.uploadMatrix4f("uView", camera.getViewMatrix());
        Shader.DEFAULT.uploadFloat("uTime", Time.getTime());

        ARBVertexArrayObject.glBindVertexArray(vaoID);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        GL20.glClearColor(0.1f, 0.1f, 0.1f, 0.1f);
        GL20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        GL20.glDrawElements(GL20.GL_TRIANGLES, elementArray.length, GL11.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);

        ARBVertexArrayObject.glBindVertexArray(0);
        Shader.detach();
    }
}
