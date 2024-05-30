package world.entity;

import npc.Node;
import org.joml.Vector2f;
import utils.render.texture.Texture;
import world.location.Location;
import world.tick.Ticking;

import java.io.Serializable;
import java.util.*;

/**
 * Las entidades son elementos móviles del juego con los que el jugador puede interactuar (en jugador no puede interactuar
 * con las partículas).
 * @see world.particle.Particle
 */

//TODO: Sistema para detectar clic a las entidades.
public abstract class Entity extends Ticking implements Serializable {
    public static final float NO_FLIP = 0, FLIP_HORIZONTAL = 1, FLIP_VERTICAL = 2, FLIP_BOTH = 3;

    /**
     * Posición donde se encuentra la entidad.
     */
    protected Location location;
    protected float flip = Entity.NO_FLIP;
    protected List<Location> pathfinding;
    protected int pathIndex = 0;

    /**
     * Instancia la entidad, pero no la genera. Hay que utilizar <code>World#spawnEntity(Entity)</code> para generar la
     * entidad en el mundo.
     * @param location Posición donde se va a generar la entidad.
     * @see world.World#spawnEntity(Entity)
     */
    public Entity(Location location) {
        this.location = location;
    }

    public boolean nextPath() {
        if (this.pathfinding == null) return false;
        if (this.pathIndex >= this.pathfinding.size()) return true;


        Vector2f targetPosition = this.pathfinding.get(this.pathIndex).toVector2f();

        Vector2f direction = new Vector2f(targetPosition).sub(this.getLocation().toVector2f()).normalize();

        Vector2f newPosition = this.getLocation().toVector2f().add(new Vector2f(direction).mul(0.05f));
        this.moveTo(newPosition);

        if (direction.x() > 0) {
            this.flip = Entity.FLIP_HORIZONTAL;
        } else if (direction.x() < 0) {
            this.flip = Entity.NO_FLIP;
        }

        if (this.getLocation().toVector2f().distance(targetPosition) < 0.1f) {
            this.pathIndex++;
        }

        return false;
    }

    /**
     * Mueve la entidad.
     */
    public void moveTo(Vector2f destination) {
        this.location.setX(destination.x());
        this.location.setY(destination.y());
    }

    /**
     * @return Posición actual de la entidad.
     */
    public Location getLocation() {
        return this.location.clone();
    }

    public ListIterator<Location> getPathfinding() {
        if (this.pathfinding == null) {
            return Collections.emptyListIterator();
        }

        return this.pathfinding.listIterator();
    }

    public int getPathIndex() {
        return this.pathIndex;
    }

    public float getFlip() {
        return this.flip;
    }

    /**
     * @return Textura que está mostrando la entidad en ese momento.
     */
    public abstract Texture getTexture();
}
