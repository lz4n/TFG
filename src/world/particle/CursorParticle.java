package world.particle;

import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.location.Location;


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
