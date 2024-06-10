package utils.render.mesh;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.opengl.GL20;

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
     * Array que almacena el tamaño de cada atributo de los vértices.
     */
    protected int[] vertexAttributesSize;
    private int vaoId;

    /**
     * Tamaño del vértice en bytes y en unidades.
     */
    protected int vertexSizeBytes = 0, vertexSize = 0;

    public Mesh(int... attributesSize) {
        this.vertexAttributesSize = attributesSize;
        for (int attributeSize: this.vertexAttributesSize) {
            this.vertexSizeBytes += attributeSize;
            this.vertexSize += attributeSize;
        }
        this.vertexSizeBytes *= Float.BYTES;
    }

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
        vertexBuffer = BufferUtils.createFloatBuffer(this.vertexArray.length);
        vertexBuffer.put(this.vertexArray).flip();

        //Creamos la VBO y la subimos al buffer
        vboId = GL20.glGenBuffers();
        GL15C.glBindBuffer(GL20.GL_ARRAY_BUFFER, vboId);
        GL15C.glBufferData(GL20.GL_ARRAY_BUFFER, vertexBuffer, GL20.GL_STATIC_DRAW);

        //Creamos los índices
        elementBuffer = BufferUtils.createIntBuffer(this.elementArray.length);
        elementBuffer.put(this.elementArray).flip();

        eboId = GL20.glGenBuffers();
        GL15C.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, eboId);
        GL15C.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL20.GL_STATIC_DRAW);

        //Añadimos los atributos a los vertices
        for (int attributeIndex = 0, attributePosition = 0, attributeSize; attributeIndex < this.vertexAttributesSize.length; attributeIndex++) {
            attributeSize = this.vertexAttributesSize[attributeIndex];
            GL20.glVertexAttribPointer(attributeIndex, attributeSize, GL20.GL_FLOAT, false, this.vertexSizeBytes, (long) attributePosition * Float.BYTES);
            GL20.glEnableVertexAttribArray(attributeIndex);
            attributePosition += attributeSize;
        }
    }

    /**
     * Dibuja el mesh en la pantalla utilizando OpenGL. Primero vincula el objeto de arreglo de vértices (VAO),
     * habilita los atributos de vértice necesarios, dibuja los elementos del mesh y luego deshabilita los atributos
     * de vértice antes de desvincular el VAO.
     */
    public void draw() {
        ARBVertexArrayObject.glBindVertexArray(this.vaoId);
        for (int attributeIndex = 0; attributeIndex < this.vertexAttributesSize.length; attributeIndex++) {
            GL20.glEnableVertexAttribArray(attributeIndex);
        }
        GL20.glDrawElements(GL20.GL_TRIANGLES, this.elementArray.length, GL11.GL_UNSIGNED_INT, 0);
        for (int attributeIndex = 0; attributeIndex < this.vertexAttributesSize.length; attributeIndex++) {
            GL20.glDisableVertexAttribArray(attributeIndex);
        }
        ARBVertexArrayObject.glBindVertexArray(0);
    }

    /**
     * Obtiene el número de elementos en el arreglo de elementos del mesh.
     *
     * @return El número de elementos en el arreglo de elementos del mesh.
     */
    public int getElementCount() {
        return this.elementArray.length;
    }

    /**
     * Obtiene el identificador del VAO (objeto de arreglo de vértices) en OpenGL asociado con este mesh.
     *
     * @return El identificador del VAO en OpenGL.
     */
    public int getVaoId() {
        return this.vaoId;
    }
}
