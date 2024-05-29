package npc;

import world.location.Location;


public class Node extends Location {
    private double pathWalk, pathHeuristic, pathTotal;
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

    public double getPathWalk() { return pathWalk; }
    public void setPathWalk(double pathWalk) { this.pathWalk = pathWalk; }
    public double getPathHeuristic() { return pathHeuristic; }
    public void setPathHeuristic(double pathHeuristic) { this.pathHeuristic = pathHeuristic; }
    public double getPathTotal() { return pathTotal; }
    public void setPathTotal(double pathTotal) { this.pathTotal = pathTotal; }
    public Node getParent() { return parent; }
    public void setParent(Node parent) { this.parent = parent; }
}