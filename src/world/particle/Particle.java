package world.particle;

import main.Main;
import utils.render.texture.Texture;
import world.location.Location;
import world.tick.ForceTicking;
import world.tick.Ticking;

/**
 * Pequeño sprite dinámico que se utiliza para dar efectos visuales. El jugador no puede interactuar con las partículas.
 * Si una partícula está fuera del mundo se elimina.
 */
public abstract class Particle extends Ticking {

    /**
     * Posición de la partícula.
     */
    protected Location location;

    /**
     * Parámetros de renderizado de la partícula: rotación y escala.
     */
    protected float rotation = 0, scale = 1;

    /**
     * Tiempo de vida de la partícula que aumenta en cada tick. Cuando supera el tiempo máximo de vida se elimina.
     * @see Particle#getMaxLifeTime()
     */
    protected int lifeTime = 0;

    /**
     * Crea una partícula, pero no la genera en el mundo. Para generar una partícula en el mundo es necesario utilizar
     * <code>spawnParticle(Particle)</code>.
     * @see world.World#spawnParticle(Particle)
     * @param location
     */
    public Particle(Location location) {
        this.location = location;
    }

    @Override
    public void onTick(long deltaTime) {
        //Aumentamos el lifetime y eliminamos la partícula si supera el tiempo máximo.
        if (this.lifeTime++ > this.getMaxLifeTime()) {
            Main.world.removeParticle(this);
        }

        //Si está fuera del mundo la eliminamos.
        if (this.location.isOutOfTheWorld()) {
            Main.world.removeParticle(this);
        }
    }

    /**
     * @return Posición de la partícula.
     */
    public Location getLocation() {
        return location.clone();
    }

    /**
     * @return Rotación de la partícula.
     */
    public float getRotation() {
        return this.rotation;
    }

    /**
     * @return Escala de la partícula.
     */
    public float getScale() {
        return this.scale;
    }

    /**
     * @return Textura de la partícula.
     */
    public abstract Texture getTexture();

    /**
     * @return Tiempo máximo de vida de la partícula.
     */
    protected abstract int getMaxLifeTime();
}
