package utils.render;

import org.joml.Vector2i;
import utils.render.scene.WorldScene;

import java.util.Arrays;

public class TerrainMesh {
    public float[] vertexArray = new float[0];
    public int[] elementArray = new int[0];
    public int vaoID;
    private int elementsCount = 0;

    public void addVertex(int x, int y) {
        int previousVertexArrayLength = this.vertexArray.length;
        int previousElementArrayLength = this.elementArray.length;

        this.vertexArray = Arrays.copyOf(this.vertexArray, previousVertexArrayLength + 9*4);
        this.elementArray = Arrays.copyOf(this.elementArray, previousVertexArrayLength + 2*3);

        //Primer vértice: abajo derecha
        //Posición
        this.vertexArray[previousVertexArrayLength++] = WorldScene.SPRITE_SIZE * x + WorldScene.SPRITE_SIZE;
        this.vertexArray[previousVertexArrayLength++] = WorldScene.SPRITE_SIZE * y;
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
        this.vertexArray[previousVertexArrayLength++] = WorldScene.SPRITE_SIZE * x;
        this.vertexArray[previousVertexArrayLength++] = WorldScene.SPRITE_SIZE * y + WorldScene.SPRITE_SIZE;
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
        this.vertexArray[previousVertexArrayLength++] = WorldScene.SPRITE_SIZE * x + WorldScene.SPRITE_SIZE;
        this.vertexArray[previousVertexArrayLength++] = WorldScene.SPRITE_SIZE * y + WorldScene.SPRITE_SIZE;
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
        this.vertexArray[previousVertexArrayLength++] = WorldScene.SPRITE_SIZE * x;
        this.vertexArray[previousVertexArrayLength++] = WorldScene.SPRITE_SIZE * y;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        //Color
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        //Coordenadas UV
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength] = 1f;

        //Añadir elementos
        //Primer triangulo
        this.elementArray[previousElementArrayLength++] = elementsCount +2;
        this.elementArray[previousElementArrayLength++] = elementsCount +1;
        this.elementArray[previousElementArrayLength++] = elementsCount;

        //Segundo triangulo
        this.elementArray[previousElementArrayLength++] = elementsCount;
        this.elementArray[previousElementArrayLength++] = elementsCount +1;
        this.elementArray[previousElementArrayLength++] = elementsCount +3;

        elementsCount += 4;
    }

    public void clear() {
        this.vertexArray = new float[0];
        this.elementArray = new int[0];
    }
}
