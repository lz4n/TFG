package utils.render.mesh;

import utils.render.scene.WorldScene;

import java.util.Arrays;

public class WorldMesh extends Mesh {
    private int elementsCount = 0;
    private int previousVertexArrayPos = 0, previousElementArrayPos = 0;
    private int vertexArraySize;

    public WorldMesh(int initialCapacity, int[] attributesSize) {
        super(attributesSize);
        this.vertexArray = new float[this.vertexSize *initialCapacity *4];
        this.vertexArraySize = this.vertexSize *initialCapacity *4;
        this.elementArray = new int[initialCapacity *2 *3];
    }

    public WorldMesh(int... attributesSize) {
        this(0, attributesSize);
    }

    public void addVertex(float posX, float posY, float sizeX, float sizeY, float... attributes) {
        float screenPosX = WorldScene.SPRITE_SIZE * posX, screenPosY = WorldScene.SPRITE_SIZE * posY;

        //Aumentar el tamaño del mesh si hace falta
        /*if (this.vertexArraySize < this.vertexArray.length + this.vertexSize *4) {

            System.err.println("SE HA AUMENTADO EL TAMAÑO");
            int previousVertexArrayLength = this.vertexArray.length;
            this.vertexArraySize = previousVertexArrayLength + this.vertexSize *4;
            this.vertexArray = Arrays.copyOf(this.vertexArray, previousVertexArrayLength + this.vertexSize *4);

            int previousElementArrayLength = this.elementArray.length;
            this.elementArray = Arrays.copyOf(this.elementArray, previousElementArrayLength + 2 *3);
        }*/


        //Primer vértice: abajo derecha
        //Posición
        this.vertexArray[this.previousVertexArrayPos++] = screenPosX + WorldScene.SPRITE_SIZE * sizeX;
        this.vertexArray[this.previousVertexArrayPos++] = screenPosY;

        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayPos++] = 1f;
        this.vertexArray[this.previousVertexArrayPos++] = 1f;

        for (float attribute: attributes) {
            this.vertexArray[this.previousVertexArrayPos++] = attribute;
        }

        //Segundo vértice: arriba izquierda
        //Posición
        this.vertexArray[this.previousVertexArrayPos++] = screenPosX;
        this.vertexArray[this.previousVertexArrayPos++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;

        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayPos++] = 0f;
        this.vertexArray[this.previousVertexArrayPos++] = 0f;

        for (float attribute: attributes) {
            this.vertexArray[this.previousVertexArrayPos++] = attribute;
        }

        //Tercer vértice: arriba derecha
        //Posición
        this.vertexArray[this.previousVertexArrayPos++] = screenPosX + WorldScene.SPRITE_SIZE * sizeX;
        this.vertexArray[this.previousVertexArrayPos++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;

        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayPos++] = 1f;
        this.vertexArray[this.previousVertexArrayPos++] = 0f;

        for (float attribute: attributes) {
            this.vertexArray[this.previousVertexArrayPos++] = attribute;
        }

        //cuarto vértice: abajo izquierda
        //Posición
        this.vertexArray[this.previousVertexArrayPos++] = screenPosX;
        this.vertexArray[this.previousVertexArrayPos++] = screenPosY;

        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayPos++] = 0f;
        this.vertexArray[this.previousVertexArrayPos++] = 1f;

        for (float attribute: attributes) {
            this.vertexArray[this.previousVertexArrayPos++] = attribute;
        }

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
