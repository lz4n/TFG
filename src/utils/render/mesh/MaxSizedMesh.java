package utils.render.mesh;

import utils.render.mesh.Mesh;
import utils.render.scene.WorldScene;

import java.util.Arrays;

public class MaxSizedMesh extends Mesh {

    protected int previousVertexArrayLength = 0, previousElementArrayLength = 0;

    public MaxSizedMesh(int maxSize) {
        this.vertexArray = new float[maxSize *9*4];
        this.elementArray = new int[maxSize *2*3];
    }

    @Override
    public void addVertex(float x, float y, int sizeX, int sizeY) {
        float screenPosX = WorldScene.SPRITE_SIZE * x, screenPosY = WorldScene.SPRITE_SIZE * y;

        //Primer vértice: abajo derecha
        //Posición
        this.vertexArray[this.previousVertexArrayLength++] = screenPosX + WorldScene.SPRITE_SIZE * sizeX;
        this.vertexArray[this.previousVertexArrayLength++] = screenPosY;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        //Color
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayLength++] = 1f;
        this.vertexArray[this.previousVertexArrayLength++] = 1f;

        //Segundo vértice: arriba izquierda
        //Posición
        this.vertexArray[this.previousVertexArrayLength++] = screenPosX;
        this.vertexArray[this.previousVertexArrayLength++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        //Color
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;

        //Tercer vértice: arriba derecha
        //Posición
        this.vertexArray[this.previousVertexArrayLength++] = screenPosX + WorldScene.SPRITE_SIZE * sizeX;
        this.vertexArray[this.previousVertexArrayLength++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        //Color
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayLength++] = 1f;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;

        //cuarto vértice: abajo izquierda
        //Posición
        this.vertexArray[this.previousVertexArrayLength++] = screenPosX;
        this.vertexArray[this.previousVertexArrayLength++] = screenPosY;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        //Color
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayLength++] = 0f;
        this.vertexArray[this.previousVertexArrayLength++] = 1f;

        //Añadir elementos
        //Primer triangulo
        this.elementArray[this.previousElementArrayLength++] = this.elementsCount +2;
        this.elementArray[this.previousElementArrayLength++] = this.elementsCount +1;
        this.elementArray[this.previousElementArrayLength++] = this.elementsCount;

        //Segundo triangulo
        this.elementArray[this.previousElementArrayLength++] = this.elementsCount;
        this.elementArray[this.previousElementArrayLength++] = this.elementsCount +1;
        this.elementArray[this.previousElementArrayLength++] = this.elementsCount +3;

        this.elementsCount += 4;
    }

    public void adjust() {
        if (this.previousVertexArrayLength > 0) {
            this.vertexArray = Arrays.copyOf(this.vertexArray, this.previousVertexArrayLength);
            this.elementArray = Arrays.copyOf(this.elementArray, this.previousElementArrayLength);
        }
    }
}
