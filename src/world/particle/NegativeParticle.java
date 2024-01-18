package world.particle;

import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.location.Location;
import world.tick.ForceTicking;

/**
 * Partícula utilizada para enviar al usuario un input negativo.
 */
@ForceTicking
public class NegativeParticle extends Particle {

    /**
     * @param location Posición donde se va a generar la partícula.
     */
    public NegativeParticle(Location location) {
        super(location);

        super.scale = 0;
    }

    @Override
    public void onTick(long deltaTime) {
        super.onTick(deltaTime);

        //Vamos aumentando la escala en cada tick.
        if (super.scale < 1) {
            super.scale += 0.1f;
        } else {
            super.scale = 1;
        }
    }

    @Override
    public Texture getTexture() {
        return Textures.NEGATIVE_PARTICLE;
    }

    @Override
    protected int getMaxLifeTime() {
        return 60;
    }
}
