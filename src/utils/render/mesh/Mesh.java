package utils.render.mesh;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.opengl.GL20;
import utils.render.texture.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * Un <code>mesh</code> es un conjunto de vértices que permiten renderizar figuras/texturas dentro de la pantalla.
 * <br>El <code>vertexArray</code> almacena toda la posición relacionada con los vértices (generalmente la posición dentro de
 * la pantalla y las coordenadas UV de la textura que le correspondan, aunque podrían ponerse más si se requiere), mientras
 * que el <code>elementArray</code> almacena los índices de los vértices dentro del <code>vertexArray</code> que van a
 * formar un triángulo.
 *
 * @author Izan
 */
public abstract class Mesh {
    /**
     * Array que almacena información de los vértices.
     */
    protected float[] vertexArray;

    /**
     * Array que almacena los triángulos que se van a formar, según los índices dentro del <code>vertexArray</code>.
     */
    protected int[] elementArray;

    /**
     * Identificador del <code>mesh</code> dentro de <code>OpenGL</code>.
     */
    private int vaoId;

    /**
     * Cantidad de vértices que forman parte de un triángulo, se usa para crear el <code>elementArray</code>.
     */
    protected int elementsCount = 0;

    /**
     * Textura del <code>Mesh</code>
     */
    private final Texture TEXTURE;

    /**
     * @param texture Textura que va a tener el mesh.
     */
    public Mesh(Texture texture) {
        this.TEXTURE = texture;
    }

    /**
     * Crea un vértice y lo almacena en los respectivos arrays. No todos los <code>mesh</code> permiten añadir vértices.
     * @param x posición X in-game del vértice.
     * @param y posición Y in-game del vértice.
     * @param sizeX tamaño del objeto a renderizar (en medidas in-game) en el eje X.
     * @param sizeY tamaño del objeto a renderizar (en medidas in-game) en el eje Y.
     */
    public void addVertex(float x, float y, int sizeX, int sizeY) {}

    /**
     * Carga el <code>mesh</code> en la <code>GPU</code>.<br>
     * <b>IMPORTANTE:</b> liberar la memoría de los vértices de atributos y texturas despues del ciclo de renderizado.
     */
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

    /**
     * @return Una copia del array de vértices.
     */
    public float[] getVertexArray() {
        return Arrays.copyOf(this.vertexArray, this.vertexArray.length);
    }

    /**
     * @return Una copia del array de elementos.
     */
    public int[] getElementArray() {
        return Arrays.copyOf(this.elementArray, this.elementArray.length);
    }

    /**
     * @return Identificador del <code>mesh</code> dentro de <code>OpenGL</code>.
     */
    public int getVaoId() {
        return this.vaoId;
    }

    /**
     * @return Textura correspondiente al <code>mesh</code>.
     */
    public final Texture getTexture() {
        return this.TEXTURE;
    }
}
