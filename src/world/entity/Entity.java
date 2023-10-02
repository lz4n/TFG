package world.entity;

import org.joml.Vector2d;
import org.joml.Vector2i;

public abstract class Entity {
    protected Vector2d location;

    public Entity(Vector2d location) {
        this.location = location;
    }

    public void move(Vector2d movement) {
        this.location.add(movement);
    }

}
