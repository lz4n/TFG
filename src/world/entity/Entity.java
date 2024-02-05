package world.entity;

import org.joml.Vector2f;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.location.Location;
import world.tick.Ticking;

import java.io.Serializable;
import java.util.Random;

/**
 * Las entidades son elementos móviles del juego con los que el jugador puede interactuar (en jugador no puede interactuar
 * con las partículas).
 * @see world.particle.Particle
 */

//TODO: Sistema para detectar clic a las entidades.
public abstract class Entity extends Ticking implements Serializable {

    /**
     * Posición donde se encuentra la entidad.
     */
    protected Location location;

    /**
     * Instancia la entidad, pero no la genera. Hay que utilizar <code>World#spawnEntity(Entity)</code> para generar la
     * entidad en el mundo.
     * @param location Posición donde se va a generar la entidad.
     * @see world.World#spawnEntity(Entity)
     */
    public Entity(Location location) {
        this.location = location;
    }

    /**
     * Mueve la entidad.
     * @param movement Vector bidimensional de flotantes que indica cúantas unidades in-game se va a mover en cada eje.
     */
    public void move(Vector2f movement) {
        this.location.add(movement.x(), movement.y());
    }

    /**
     * @return Posición actual de la entidad.
     */
    public Location getLocation() {
        return this.location.clone();
    }

    /**
     * @return Textura que está mostrando la entidad en ese momento.
     */
    public abstract Texture getTexture();
}
