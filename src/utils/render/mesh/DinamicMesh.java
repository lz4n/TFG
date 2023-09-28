package utils.render.mesh;

import utils.render.mesh.Mesh;
import utils.render.scene.WorldScene;

import java.util.Arrays;

public class DinamicMesh extends Mesh {
    @Override
    public void addVertex(float x, float y, int sizeX, int sizeY) {
        float screenPosX = WorldScene.SPRITE_SIZE * x, screenPosY = WorldScene.SPRITE_SIZE * y;

        int previousVertexArrayLength = this.vertexArray.length;
        this.vertexArray = Arrays.copyOf(this.vertexArray, previousVertexArrayLength + 9*4);

        int previousElementArrayLength = this.elementArray.length;
        this.elementArray = Arrays.copyOf(this.elementArray, previousElementArrayLength + 2*3);

        //Primer vértice: abajo derecha
        //Posición
        this.vertexArray[previousVertexArrayLength++] = screenPosX + WorldScene.SPRITE_SIZE * sizeX;
        this.vertexArray[previousVertexArrayLength++] = screenPosY;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        //Color
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        //Coordenadas UV
        this.vertexArray[previousVertexArrayLength++] = 1f;
        this.vertexArray[previousVertexArrayLength++] = 1f;

        //Segundo vértice: arriba izquierda
        //Posición
        this.vertexArray[previousVertexArrayLength++] = screenPosX;
        this.vertexArray[previousVertexArrayLength++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        //Color
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        //Coordenadas UV
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 0f;

        //Tercer vértice: arriba derecha
        //Posición
        this.vertexArray[previousVertexArrayLength++] = screenPosX + WorldScene.SPRITE_SIZE * sizeX;
        this.vertexArray[previousVertexArrayLength++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        //Color
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        //Coordenadas UV
        this.vertexArray[previousVertexArrayLength++] = 1f;
        this.vertexArray[previousVertexArrayLength++] = 0f;

        //cuarto vértice: abajo izquierda
        //Posición
        this.vertexArray[previousVertexArrayLength++] = screenPosX;
        this.vertexArray[previousVertexArrayLength++] = screenPosY;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        //Color
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        //Coordenadas UV
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 1f;

        //Añadir elementos
        //Primer triangulo
        this.elementArray[previousElementArrayLength++] = this.elementsCount +2;
        this.elementArray[previousElementArrayLength++] = this.elementsCount +1;
        this.elementArray[previousElementArrayLength++] = this.elementsCount;

        //Segundo triangulo
        this.elementArray[previousElementArrayLength++] = this.elementsCount;
        this.elementArray[previousElementArrayLength++] = this.elementsCount +1;
        this.elementArray[previousElementArrayLength++] = this.elementsCount +3;

        this.elementsCount += 4;
    }
}
