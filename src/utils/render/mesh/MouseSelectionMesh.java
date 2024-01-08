package utils.render.mesh;

import utils.render.scene.WorldScene;

/**
 * <code>Mesh</code> utilizado para el cursor del ratón en el mundo.
 */
public final class MouseSelectionMesh extends Mesh {

    /**
     * @param attributesSize Tamaño de los atributos, en unidades.
     */
    public MouseSelectionMesh(int... attributesSize) {
        super(attributesSize);
        this.vertexArray = new float[9*4];
        this.elementArray = new int[]{2, 1, 0, 0, 1, 3};
    }

    /**
     * Establece el vértice del selector.
     * @param x posición en el eje X, en coordenadas in-game.
     * @param y posición en el eje Y, en coordenadas in-game.
     * @param sizeX tamaño del vértice en el eje X, en coordenadas in-game.
     * @param sizeY tamaño del vértice en el eje Y, en coordenadas in-game.
     */
    public void setVertex(float x, float y, int sizeX, int sizeY) {
        float screenPosX = WorldScene.SPRITE_SIZE * x, screenPosY = WorldScene.SPRITE_SIZE * y;
        int previousVertexArrayLength = 0;

        //Primer vértice: abajo derecha
        //Posición
        this.vertexArray[previousVertexArrayLength++] = screenPosX + WorldScene.SPRITE_SIZE * sizeX;
        this.vertexArray[previousVertexArrayLength++] = screenPosY;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        //Coordenadas UV
        this.vertexArray[previousVertexArrayLength++] = 1f;
        this.vertexArray[previousVertexArrayLength++] = 1f;

        //Segundo vértice: arriba izquierda
        //Posición
        this.vertexArray[previousVertexArrayLength++] = screenPosX;
        this.vertexArray[previousVertexArrayLength++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        //Coordenadas UV
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength++] = 0f;

        //Tercer vértice: arriba derecha
        //Posición
        this.vertexArray[previousVertexArrayLength++] = screenPosX + WorldScene.SPRITE_SIZE * sizeX;
        this.vertexArray[previousVertexArrayLength++] = screenPosY + WorldScene.SPRITE_SIZE * sizeY;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        //Coordenadas UV
        this.vertexArray[previousVertexArrayLength++] = 1f;
        this.vertexArray[previousVertexArrayLength++] = 0f;

        //cuarto vértice: abajo izquierda
        //Posición
        this.vertexArray[previousVertexArrayLength++] = screenPosX;
        this.vertexArray[previousVertexArrayLength++] = screenPosY;
        this.vertexArray[previousVertexArrayLength++] = 0f;
        //Coordenadas UV
        this.vertexArray[previousVertexArrayLength++] = 0f;
        this.vertexArray[previousVertexArrayLength] = 1f;
    }
}
