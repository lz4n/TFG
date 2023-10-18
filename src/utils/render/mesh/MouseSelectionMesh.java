package utils.render.mesh;

/**
 * <code>Mesh</code> utilizado para el cursor del ratón en el mundo.
 */
public abstract class MouseSelectionMesh extends Mesh {

    /**
     * @param attributesSize Tamaño de los atributos, en unidades.
     */
    public MouseSelectionMesh(int... attributesSize) {
        super(attributesSize);
    }

    /**
     * Establece el vértice del selector.
     * @param x posición en el eje X, en coordenadas in-game.
     * @param y posición en el eje Y, en coordenadas in-game.
     * @param sizeX tamaño del vértice en el eje X, en coordenadas in-game.
     * @param sizeY tamaño del vértice en el eje Y, en coordenadas in-game.
     */
    public abstract void setVertex(float x, float y, int sizeX, int sizeY);
}
