package utils.render.mesh;

import utils.render.scene.WorldScene;

import java.util.Arrays;

public class TooltipMesh extends Mesh {

    /**
     * Número de objetos que se han colocado en el mesh hasta el momento.
     */
    private int elementsCount = 0;


    /**
     * Últimos índices utilizados de los arrays de vérices y elementos.
     */
    private int previousVertexArrayPos = 0, previousElementArrayPos = 0;
    
    public TooltipMesh() {
        super(2);
        this.vertexArray = new float[this.vertexSize *1 *4];
        this.elementArray = new int[1 *2 *3];
    }

    public void addVertex(float x, float y, float sizeX, float sizeY, float pixelSize) {
        
        if (this.previousVertexArrayPos >= this.vertexArray.length) {
            this.vertexArray = Arrays.copyOf(this.vertexArray, this.previousVertexArrayPos +this.vertexSize *4);
        }
        if (this.previousElementArrayPos >= this.elementArray.length) {
            this.elementArray = Arrays.copyOf(this.elementArray, this.previousElementArrayPos +6);
        }

        //Primer vértice: abajo derecha
        this.vertexArray[this.previousVertexArrayPos++] = (x *pixelSize) + (sizeX *pixelSize);
        this.vertexArray[this.previousVertexArrayPos++] = y *pixelSize;

        //Segundo vértice: arriba izquierda
        this.vertexArray[this.previousVertexArrayPos++] = x *pixelSize;
        this.vertexArray[this.previousVertexArrayPos++] = (y *pixelSize) + (sizeY *pixelSize);

        //Tercer vértice: arriba derecha
        this.vertexArray[this.previousVertexArrayPos++] = (x *pixelSize) + (sizeX *pixelSize);
        this.vertexArray[this.previousVertexArrayPos++] = (y *pixelSize) + (sizeY *pixelSize);

        //cuarto vértice: abajo izquierda
        this.vertexArray[this.previousVertexArrayPos++] = x *pixelSize;
        this.vertexArray[this.previousVertexArrayPos++] = y *pixelSize;

        //Añadir elementos
        //Primer triangulo
        this.elementArray[this.previousElementArrayPos++] = this.elementsCount +2;
        this.elementArray[this.previousElementArrayPos++] = this.elementsCount +1;
        this.elementArray[this.previousElementArrayPos++] = this.elementsCount;

        //Segundo triangulo
        this.elementArray[this.previousElementArrayPos++] = this.elementsCount;
        this.elementArray[this.previousElementArrayPos++] = this.elementsCount +1;
        this.elementArray[this.previousElementArrayPos++] = this.elementsCount +3;

        this.elementsCount += 4;
    }
}
