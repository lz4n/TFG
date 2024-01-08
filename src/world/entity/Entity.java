package world.entity;

import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.render.mesh.EntityMesh;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.location.Location;
import world.tick.Ticking;

import java.io.Serializable;
import java.util.Random;

public abstract class Entity extends Ticking implements Serializable {
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

    public enum EntityType implements Serializable {
        DUCK(new EntityMesh(new Vector2i(1, 1)), Textures.DUCK);

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
