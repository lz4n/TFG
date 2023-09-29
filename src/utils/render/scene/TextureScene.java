package utils.render.scene;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.opengl.GL20;
import utils.render.Camera;
import utils.render.Shader;
import utils.render.texture.StaticTexture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class TextureScene extends Scene {
    public static final int SPRITE_SIZE = 20;

    private float[] vertexArray = {
            // position               // color                  //Coordenadas UV
            20f, 0f, 0.0f,       1.0f, 0.0f, 0.0f, 1.0f,   1, 1, //Abajo derecha
            0f,  20f, 0.0f,       0.0f, 1.0f, 0.0f, 1.0f,   0, 0, //Arriba izquierda
            20f,  20f, 0.0f ,      1.0f, 0.0f, 1.0f, 1.0f,   1, 0, //Arriba derecha
            0f, 0f, 0.0f,       1.0f, 1.0f, 0.0f, 1.0f,   0, 1, //Abajo izquierda

            60f, 20f, 0.0f,       1.0f, 0.0f, 0.0f, 1.0f,   1, 1, //Abajo derecha
            40f,  40f, 0.0f,       0.0f, 1.0f, 0.0f, 1.0f,   0, 0, //Arriba izquierda
            60f,  40f, 0.0f ,      1.0f, 0.0f, 1.0f, 1.0f,   1, 0, //Arriba derecha
            40f, 20f, 0.0f,       1.0f, 1.0f, 0.0f, 1.0f,   0, 1 //Abajo izquierda
    };

    private int[] elementArray = {
            // Primer cuadrado (abajo)
            2, 1, 0,
            0, 1, 3,

            6, 5, 4,
            4, 5, 7

    };

    private int vaoID, vboID, eboID;
    private Camera camera = new Camera(new Vector2f()); //Iniciamos la cámara en 0,0.
    private StaticTexture texture;

    public void init() {
        //Cargamos el shader
        Shader.DEFAULT_TEXTURE.compile();

        this.texture = new StaticTexture("assets/textures/terrain/bush.png");

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
        int uvSize = 2;
        int vertexSizeBytes = (positionsSize + colorSize + uvSize) * Float.BYTES;

        GL20.glVertexAttribPointer(0, positionsSize, GL20.GL_FLOAT, false, vertexSizeBytes, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, colorSize, GL20.GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL20.glVertexAttribPointer(2, uvSize, GL11.GL_FLOAT, false, vertexSizeBytes, (positionsSize + colorSize) * Float.BYTES);
        GL20.glEnableVertexAttribArray(2);
    }

    public void drawTerrain() {

    }

    public void update(long dTime) {
        Shader.DEFAULT_TEXTURE.use();

        //Subimos variables uniform al shader .glsl
        Shader.DEFAULT_TEXTURE.uploadMatrix4f("uProjection", camera.getProjectionMatrix());
        Shader.DEFAULT_TEXTURE.uploadMatrix4f("uView", camera.getViewMatrix());

        //Cargamos la textura
        Shader.DEFAULT_TEXTURE.uploadTexture("texture_sampler", 0);
        GL20.glActiveTexture(GL20.GL_TEXTURE0);
        texture.bind();

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
