package utils;

import org.joml.Vector2f;
import org.joml.Vector4f;
import world.location.Location;

public class BoundingBox implements Cloneable {
    private float xMin = 0, yMin = 0, xMax, yMax;

    public BoundingBox(Location origin, Vector2f size) {
        this(origin.getX(), origin.getY(), origin.getX() +size.x(), origin.getY() +size.y());
    }

    public BoundingBox(float width, float height) {
        this.xMax = width;
        this.yMax = height;
    }


    public BoundingBox(float x1, float y1, float x2, float y2) {
        this.xMin = Math.min(x1, x2);
        this.xMax = Math.max(x1, x2);
        this.yMin = Math.min(y1, y2);
        this.yMax = Math.max(y1, y2);
    }

    public BoundingBox move(float movementX, float movementY) {
        this.xMin += movementX;
        this.xMax += movementX;
        this.yMin += movementY;
        this.yMax += movementY;
        return this;
    }

    public boolean collidesWith(BoundingBox boundingBox) {
        return this.xMax <= boundingBox.xMin &&
                this.xMin >= boundingBox.xMax &&
                this.yMax <= boundingBox.yMin &&
                this.yMin >= boundingBox.yMax;
    }

    public boolean containsLocation(Location location) {
        return containsLocation(location.getX(), location.getY());
    }

    public boolean containsLocation(float x, float y) {
        return x >= this.xMin &&
                x <= this.xMax &&
                y >= this.yMin &&
                y <= this.yMax;
    }

    public Vector2f getSize() {
        return new Vector2f(this.getWidth(), this.getHeight());
    }

    public float getWidth() {
        return this.xMax - this.xMin;
    }

    public float getHeight() {
        return this.yMax - this.yMin;
    }

    public Vector4f getComponents() {
        return new Vector4f(this.xMin, this.yMin, this.xMax, this.yMax);
    }

    @Override
    public BoundingBox clone() {
        try {
            return (BoundingBox) super.clone();
        } catch (CloneNotSupportedException exception) {
            Logger.sendMessage("Error de clonación: %s.", Logger.LogMessageType.FATAL, exception.getMessage());
        }
        return null;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "xMin=" + xMin +
                ", yMin=" + yMin +
                ", xMax=" + xMax +
                ", yMax=" + yMax +
                '}';
    }
}
