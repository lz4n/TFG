package world.location;

import main.Main;
import org.joml.Vector2f;
import org.joml.Vector2i;
import world.feature.Feature;
import world.terrain.Terrain;

public class Location implements Cloneable {
    private float x, y;

    public Location(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isOutOfTheWorld() {
        return !(x >= 0 && x < Main.WORLD.getSize() && y >= 0 && y < Main.WORLD.getSize());
    }

    public Location add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Location truncate() {
        this.x = (int) this.getX();
        this.y = (int) this.getY();
        return this;
    }

    public Vector2f toVector2f() {
        return new Vector2f(this.getX(), this.getY());
    }

    public Vector2i toVector2i() {
        return new Vector2i((int) this.getX(), (int) this.getY());
    }

    public Terrain getTerrain() {
        return Main.WORLD.getTerrain((int) this.getX(), (int) this.getY());
    }

    public Feature getFeature() {
        return Main.WORLD.getFeature((int) this.getX(), (int) this.getY());
    }

    @Override
    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (CloneNotSupportedException exception) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("Location(x=%f,y=%f)", this.getX(), this.getY());
    }
}
