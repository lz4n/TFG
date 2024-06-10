package npc;

import world.location.Location;


public class Node extends Location {
    private float pathWalk, pathHeuristic, pathTotal;
    private Node parent;

    public Node(Location location) {
        this((int) location.getX(), (int) location.getY());
    }

    public Node(int x, int y) {
        super(x, y);
        this.pathWalk = 0;
        this.pathHeuristic = 0;
        this.pathTotal = 0;
        this.parent = null;
    }

    public float getPathWalk() { return pathWalk; }
    public void setPathWalk(float pathWalk) { this.pathWalk = pathWalk; }
    public float getPathHeuristic() { return pathHeuristic; }
    public void setPathHeuristic(float pathHeuristic) { this.pathHeuristic = pathHeuristic; }
    public float getPathTotal() { return pathTotal; }
    public void setPathTotal(float pathTotal) { this.pathTotal = pathTotal; }
    public Node getParent() { return parent; }
    public void setParent(Node parent) { this.parent = parent; }
}