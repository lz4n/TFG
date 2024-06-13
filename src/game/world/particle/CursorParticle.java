package game.world.particle;

import game.utils.render.texture.Texture;
import game.utils.render.texture.Textures;
import game.world.location.Location;

/**
 * Partícula que indica el bloque y/o Feature que se está seleccionando.
 */
public class CursorParticle extends Particle {

    /**
     * @param location Posición donde se va a generar la partícula.
     */
    public CursorParticle(Location location) {
        super(location.clone().truncate());
    }

    @Override
    public Texture getTexture() {
        return Textures.SELECTOR;
    }

    @Override
    protected int getMaxLifeTime() {
        return 0;
    }
}
