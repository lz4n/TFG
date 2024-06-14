package game.utils.render.mesh;

import java.util.Arrays;

public class ShapeMesh extends Mesh {
    private int elementsCount = 0;
    private int previousVertexArrayPos = 0;

    /**
     * Constructor de la clase ShapeMesh. Inicializa el arreglo de vértices y el arreglo de elementos del mesh.
     */
    public ShapeMesh() {
        super(2);
        this.vertexArray = new float[this.vertexSize *1 *4];
    }

    /**
     * Agrega un vértice al mesh con las coordenadas especificadas y calcula los elementos para formar dos triángulos.
     *
     * @param x         Coordenada X del vértice inferior izquierdo.
     * @param y         Coordenada Y del vértice inferior izquierdo.
     * @param sizeX     Tamaño en X del vértice.
     * @param sizeY     Tamaño en Y del vértice.
     * @param pixelSize Tamaño en píxeles para el escalado.
     */
    public void addVertex(float x, float y, float sizeX, float sizeY, float pixelSize) {
        
        if (this.previousVertexArrayPos >= this.vertexArray.length) {
            this.vertexArray = Arrays.copyOf(this.vertexArray, this.previousVertexArrayPos +this.vertexSize *4);
        }

        this.vertexArray[this.previousVertexArrayPos++] = x *pixelSize;
        this.vertexArray[this.previousVertexArrayPos++] = y *pixelSize;

        this.vertexArray[this.previousVertexArrayPos++] = (x *pixelSize) + (sizeX *pixelSize);
        this.vertexArray[this.previousVertexArrayPos++] = y *pixelSize;

        this.vertexArray[this.previousVertexArrayPos++] = x *pixelSize;
        this.vertexArray[this.previousVertexArrayPos++] = (y *pixelSize) + (sizeY *pixelSize);

        this.vertexArray[this.previousVertexArrayPos++] = (x *pixelSize) + (sizeX *pixelSize);
        this.vertexArray[this.previousVertexArrayPos++] = (y *pixelSize) + (sizeY *pixelSize);


        this.elementsCount += 4;
    }
}
