package world.location;

public class Location {
    private double x, y;

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void add(double x, double y) {
        this.x += x;
        this.y += y;
    }

    pu
}
