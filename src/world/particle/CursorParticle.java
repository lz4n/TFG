package world.particle;

import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.location.Location;
import world.tick.ForceTicking;

/**
 * Partícula utilizada para el selector del cursor.
 */
@ForceTicking
public class CursorParticle extends Particle {

    /**
     * @param location Posición donde se va a generar la partícula.
     */
    public CursorParticle(Location location) {
        super(location.clone().add(0, 1).truncate());
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
