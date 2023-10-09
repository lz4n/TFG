package utils.render.mesh;

import utils.render.scene.WorldScene;
import utils.render.texture.Texture;

import java.util.Random;

/**
 * Similar a <code>MaxSizedMesh</code>, pero este rota las coordenadas UV de la textura al azar.
 * @see MaxSizedMesh
 * @see Mesh
 *
 * @author Izan
 */
public class MaxSizedRandomUVMesh extends MaxSizedMesh {

    /**
     * @param maxSize Tamaño máximo del <code>mesh</code>.
     * @param texture Textura correspondiente al <code>mesh</code>.
     */
    public MaxSizedRandomUVMesh(int maxSize, Texture texture) {
        super(maxSize, texture);
    }

    @Override
    public void addVertex(float x, float y, int sizeX, int sizeY) {
        float screenPosX = WorldScene.SPRITE_SIZE * x, screenPosY = WorldScene.SPRITE_SIZE * y;
        int[] randomizedUV = this.getRandomizedUV();

        //Primer vértice: abajo derecha
        //Posición
        this.vertexArray[this.previousVertexArrayPos++] = screenPosX + WorldScene.SPRITE_SIZE * sizeX;
        this.vertexArray[this.previousVertexArrayPos++] = screenPosY;
        this.vertexArray[this.previousVertexArrayPos++] = 0f;

        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayPos++] = randomizedUV[0];
        this.vertexArray[this.previousVertexArrayPos++] = randomizedUV[1];

        //Segundo vértice: arriba izquierda
        //Posición
        this.vertexArray[this.previousVertexArrayPos++] = screenPosX;
        this.vertexArray[this.previousVertexArrayPos++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;
        this.vertexArray[this.previousVertexArrayPos++] = 0f;

        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayPos++] = randomizedUV[2];
        this.vertexArray[this.previousVertexArrayPos++] = randomizedUV[3];

        //Tercer vértice: arriba derecha
        //Posición
        this.vertexArray[this.previousVertexArrayPos++] = screenPosX + WorldScene.SPRITE_SIZE * sizeX;
        this.vertexArray[this.previousVertexArrayPos++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;
        this.vertexArray[this.previousVertexArrayPos++] = 0f;

        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayPos++] = randomizedUV[4];
        this.vertexArray[this.previousVertexArrayPos++] = randomizedUV[5];

        //cuarto vértice: abajo izquierda
        //Posición
        this.vertexArray[this.previousVertexArrayPos++] = screenPosX;
        this.vertexArray[this.previousVertexArrayPos++] = screenPosY;
        this.vertexArray[this.previousVertexArrayPos++] = 0f;

        //Coordenadas UV
        this.vertexArray[this.previousVertexArrayPos++] = randomizedUV[6];
        this.vertexArray[this.previousVertexArrayPos++] = randomizedUV[7];

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

    /**
     * @return array con coordenadas UV rotadas aleatoriamente.
     */
    private int[] getRandomizedUV() {
        return switch (new Random().nextInt(4)) {
            case 0 -> new int[]{1, 1, 0, 0, 1, 0, 0, 1};
            case 1 -> new int[]{1, 1, 0, 0, 0, 1, 1, 0};
            case 2 -> new int[]{0, 0, 1, 1, 1, 0, 0, 1};
            default -> new int[]{0, 0, 1, 1, 0, 1, 1, 0};
        };
    }
}
