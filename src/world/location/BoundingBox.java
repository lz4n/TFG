package world.location;

import org.joml.Vector2f;
import utils.Logger;

public class BoundingBox implements Cloneable {
    private float xMin, yMin, xMax, yMax;

    public BoundingBox(float x1, float y1, float x2, float y2) {
        this.xMin = Math.min(x1, x2);
        this.xMax = Math.max(x1, x2);
        this.yMin = Math.min(y1, y2);
        this.yMax = Math.max(y1, y2);
    }

    public Vector2f getSize() {
        return new Vector2f(this.xMax - this.xMin, this.yMax - this.yMin);
    }

    public void move(float movementX, float movementY) {
        this.xMin += movementX;
        this.xMax += movementX;
        this.yMin += movementY;
        this.yMax += movementY;
    }

    public boolean collidesWith(BoundingBox boundingBox) {
        return this.xMax <= boundingBox.xMin &&
                this.xMin >= boundingBox.xMax &&
                this.yMax <= boundingBox.yMin &&
                this.yMin >= boundingBox.yMax;
    }

    public boolean containsLocation(Location location) {
        return location.getX() >= this.xMin &&
                location.getX() <= this.xMax &&
                location.getY() >= this.yMin &&
                location.getY() <= this.yMax;
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
}
