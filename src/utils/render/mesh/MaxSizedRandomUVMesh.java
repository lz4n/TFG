package utils.render.mesh;

import utils.render.scene.WorldScene;
import utils.render.texture.Texture;

import java.util.Random;

public class MaxSizedRandomUVMesh extends MaxSizedMesh {
    public MaxSizedRandomUVMesh(int maxSize, Texture texture) {
        super(maxSize, texture);
    }

    @Override
    public void addVertex(float x, float y, int sizeX, int sizeY) {
        float screenPosX = WorldScene.SPRITE_SIZE * x, screenPosY = WorldScene.SPRITE_SIZE * y;
        int[] randomizedUV = this.getRandomizedUV();

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
        this.vertexArray[this.previousVertexArrayLength++] = randomizedUV[0];
        this.vertexArray[this.previousVertexArrayLength++] = randomizedUV[1];

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
        this.vertexArray[this.previousVertexArrayLength++] = randomizedUV[2];
        this.vertexArray[this.previousVertexArrayLength++] = randomizedUV[3];

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
        this.vertexArray[this.previousVertexArrayLength++] = randomizedUV[4];
        this.vertexArray[this.previousVertexArrayLength++] = randomizedUV[5];

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
        this.vertexArray[this.previousVertexArrayLength++] = randomizedUV[6];
        this.vertexArray[this.previousVertexArrayLength++] = randomizedUV[7];

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

    private int[] getRandomizedUV() {
        return switch (new Random().nextInt(4)) {
            case 0 -> new int[]{1, 1, 0, 0, 1, 0, 0, 1};
            case 1 -> new int[]{1, 1, 0, 0, 0, 1, 1, 0};
            case 2 -> new int[]{0, 0, 1, 1, 1, 0, 0, 1};
            default -> new int[]{0, 0, 1, 1, 0, 1, 1, 0};
        };
    }
}
