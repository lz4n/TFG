package world.entity;

import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.render.mesh.EntityMesh;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;
import world.location.Location;

import java.util.Random;

public abstract class Entity {
    protected static final Random RANDOM = new Random();

    protected Location location;
    private final EntityType ENTITY_TYPE;

    public Entity(EntityType entityType, Location location) {
        this.ENTITY_TYPE = entityType;
        this.location = location;
    }

    public void move(Vector2f movement) {
        this.location.add(movement.x(), movement.y());
    }

    public Location getLocation() {
        return this.location.clone();
    }

    public EntityType getEntityType() {
        return this.ENTITY_TYPE;
    }

    public abstract void tick();

    public enum EntityType {
        DUCK(new EntityMesh(new Vector2i(1, 1)), new StaticTexture("assets/textures/entity/duck.png"));

        private final EntityMesh MESH;
        private final Texture TEXTURE;

        EntityType(EntityMesh mesh, Texture texture) {
            this.MESH = mesh;
            this.TEXTURE = texture;
        }

        public EntityMesh getMesh() {
            return this.MESH;
        }

        public Texture getTexture() {
            return this.TEXTURE;
        }
    }
}
