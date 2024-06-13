package game.utils.npc;

import game.world.location.Location;

/**
 * Representa un nodo en el mundo del juego que extiende la clase Location.
 * Un nodo contiene información adicional para el cálculo de caminos, como los costos
 * de caminar, heurística y el costo total, así como una referencia al nodo padre.
 */
public class Node extends Location {
    private float pathWalk, pathHeuristic, pathTotal;
    private Node parent;

    /**
     * Crea un nuevo nodo basado en una ubicación existente.
     *
     * @param location la ubicación base para este nodo.
     */
    public Node(Location location) {
        this((int) location.getX(), (int) location.getY());
    }

    /**
     * Crea un nuevo nodo en las coordenadas especificadas.
     *
     * @param x la coordenada X del nodo.
     * @param y la coordenada Y del nodo.
     */
    public Node(int x, int y) {
        super(x, y);
        this.pathWalk = 0;
        this.pathHeuristic = 0;
        this.pathTotal = 0;
        this.parent = null;
    }

    /**
     * Obtiene el costo de caminar hasta este nodo.
     *
     * @return el costo de caminar hasta este nodo.
     */
    public float getPathWalk() { return pathWalk; }

    /**
     * Establece el costo de caminar hasta este nodo.
     *
     * @param pathWalk el costo de caminar hasta este nodo.
     */
    public void setPathWalk(float pathWalk) { this.pathWalk = pathWalk; }

    /**
     * Obtiene el valor heurístico de este nodo.
     *
     * @return el valor heurístico de este nodo.
     */
    public float getPathHeuristic() { return pathHeuristic; }

    /**
     * Establece el valor heurístico de este nodo.
     *
     * @param pathHeuristic el valor heurístico de este nodo.
     */
    public void setPathHeuristic(float pathHeuristic) { this.pathHeuristic = pathHeuristic; }

    /**
     * Obtiene el costo total (caminar + heurística) de este nodo.
     *
     * @return el costo total de este nodo.
     */
    public float getPathTotal() { return pathTotal; }

    /**
     * Establece el costo total (caminar + heurística) de este nodo.
     *
     * @param pathTotal el costo total de este nodo.
     */
    public void setPathTotal(float pathTotal) { this.pathTotal = pathTotal; }

    /**
     * Obtiene el nodo padre de este nodo.
     *
     * @return el nodo padre de este nodo.
     */
    public Node getParent() { return parent; }

    /**
     * Establece el nodo padre de este nodo.
     *
     * @param parent el nodo padre a establecer.
     */
    public void setParent(Node parent) { this.parent = parent; }
}