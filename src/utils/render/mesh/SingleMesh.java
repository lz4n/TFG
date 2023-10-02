package utils.render.mesh;

import utils.render.scene.WorldScene;
import utils.render.texture.Texture;

import java.util.Arrays;

public class SingleMesh extends Mesh {
    public SingleMesh(Texture texture) {
        super(texture);
        this.vertexArray = new float[9*4];
        this.elementArray = new int[]{2, 1, 0, 0, 1, 3};
    }

    public void setVertex(float x, float y) {
        float screenPosX = WorldScene.SPRITE_SIZE * x, screenPosY = WorldScene.SPRITE_SIZE * y;
        int previousVertexArrayLength = 0;

        //Primer vértice: abajo derecha
        //Posición
        this.vertexArray[previousVertexArrayLength++] = screenPosX + WorldScene.SPRITE_SIZE;
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
        this.vertexArray[previousVertexArrayLength++] = screenPosY + WorldScene.SPRITE_SIZE;
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
        this.vertexArray[previousVertexArrayLength++] = screenPosX + WorldScene.SPRITE_SIZE;
        this.vertexArray[previousVertexArrayLength++] = screenPosY + WorldScene.SPRITE_SIZE;
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
        this.vertexArray[previousVertexArrayLength] = 1f;
    }
}
