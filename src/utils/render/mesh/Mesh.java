package utils.render.mesh;

import main.Main;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.opengl.GL20;
import utils.render.scene.WorldScene;
import utils.render.texture.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public abstract class Mesh {
    protected float[] vertexArray;
    protected int[] elementArray;
    private int vaoId;
    protected int elementsCount = 0;
    private final Texture TEXTURE;

    public Mesh(Texture texture) {
        this.TEXTURE = texture;
    }


    public void addVertex(float x, float y, int sizeX, int sizeY) {}

    public void load() {
        int vboId, eboId;
        FloatBuffer vertexBuffer;
        IntBuffer elementBuffer;

        //Generamos los VAO, VBO y EBO, y los mandamos a la GPU
        this.vaoId = ARBVertexArrayObject.glGenVertexArrays();
        ARBVertexArrayObject.glBindVertexArray(this.vaoId);


        //Creamos el buffer de vértices
        vertexBuffer = BufferUtils.createFloatBuffer(this.getVertexArray().length);
        vertexBuffer.put(this.getVertexArray()).flip();

        //Creamos la VBO y la subimos al buffer
        vboId = GL20.glGenBuffers();
        GL15C.glBindBuffer(GL20.GL_ARRAY_BUFFER, vboId);
        GL15C.glBufferData(GL20.GL_ARRAY_BUFFER, vertexBuffer, GL20.GL_STATIC_DRAW);

        //Creamos los índices
        elementBuffer = BufferUtils.createIntBuffer(this.getElementArray().length);
        elementBuffer.put(this.getElementArray()).flip();

        eboId = GL20.glGenBuffers();
        GL15C.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, eboId);
        GL15C.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL20.GL_STATIC_DRAW);
    }

    public float[] getVertexArray() {
        return Arrays.copyOf(this.vertexArray, this.vertexArray.length);
    }

    public int[] getElementArray() {
        return Arrays.copyOf(this.elementArray, this.elementArray.length);
    }

    public int getVaoId() {
        return this.vaoId;
    }

    public final Texture getTexture() {
        return this.TEXTURE;
    }
}
