package game.utils.render.mesh;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Un <code>mesh</code> es un conjunto de vértices que permiten renderizar figuras/texturas dentro de la pantalla.
 * <br>El <code>vertexArray</code> almacena toda la posición relacionada con los vértices (generalmente la posición dentro de
 * la pantalla y las coordenadas UV de la textura que le correspondan, aunque podrían ponerse más si se requiere).
 *
 * @author Izan
 */
public abstract class Mesh {
    /**
     * Array que almacena información de los vértices.
     */
    protected float[] vertexArray;


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
        int vboId;
        FloatBuffer vertexBuffer;

        // Generamos los VAO y VBO, y los mandamos a la GPU
        this.vaoId = ARBVertexArrayObject.glGenVertexArrays();
        ARBVertexArrayObject.glBindVertexArray(this.vaoId);

        // Creamos el buffer de vértices
        vertexBuffer = BufferUtils.createFloatBuffer(this.vertexArray.length);
        vertexBuffer.put(this.vertexArray).flip();

        // Creamos la VBO y la subimos al buffer
        vboId = GL20.glGenBuffers();
        GL15C.glBindBuffer(GL20.GL_ARRAY_BUFFER, vboId);
        GL15C.glBufferData(GL20.GL_ARRAY_BUFFER, vertexBuffer, GL20.GL_STATIC_DRAW);

        // Añadimos los atributos a los vértices
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
        GL20.glDrawArrays(this.getRenderMode(), 0, this.vertexArray.length / this.vertexSize);
        for (int attributeIndex = 0; attributeIndex < this.vertexAttributesSize.length; attributeIndex++) {
            GL20.glDisableVertexAttribArray(attributeIndex);
        }
        ARBVertexArrayObject.glBindVertexArray(0);

        GL20.glActiveTexture(GL13.GL_TEXTURE0);
    }

    /**
     * Metodo sobreescribible que devuelve el modo en el que se va a renderizar el mesh. <code>GL20.GL_TRIANGLE_STRIP</code> para meshses con un único
     * cuadrado, y <code>GL20.GL_TRIANGLES</code> para los que se compongan de varios triangulos.
     * @return Modo de renderizado de OpenGL.
     *
     * @see GL20#GL_TRIANGLE_STRIP
     * @see GL20#GL_TRIANGLES
     */
    protected int getRenderMode() {
        return GL20.GL_TRIANGLE_STRIP;
    }
}
