package world.location;

import main.Main;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import world.feature.Feature;
import world.terrain.Terrain;
import world.worldBuilder.Biome;

public class Location implements Cloneable {
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

    public Location add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2d getVector2d() {
        return new Vector2d(this.getX(), this.getY());
    }

    public Vector2f getVector2f() {
        return new Vector2f((float) this.getX(), (float) this.getY());
    }

    public Vector2i getVector2i() {
        return new Vector2i((int) this.getX(), (int) this.getY());
    }

    public Terrain getTerrain() {
        return Main.WORLD.getTerrain((int) this.getX(), (int) this.getY());
    }

    public Biome getBiome() {
        return Main.WORLD.getBiome((int) this.getX(), (int) this.getY());
    }

    public Feature getFeature() {
        return Main.WORLD.getFeature((int) this.getX(), (int) this.getY());
    }

    @Override
    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (CloneNotSupportedException exception) {
            System.err.println(exception.getMessage());
        }
        return null;
    }
}
