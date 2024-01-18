package world.particle;

import org.joml.Vector2f;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.location.Location;

/**
 * Partícula utilizada para simular las nubes y su sombra.
 */
public class CloudParticle extends AbstractPhysicsParticle {
    /**
     * Indica si la partícula es una nube o su sombra.
     */
    private final boolean IS_SHADOW;

    /**
     * @param location Posición donde se va a generar la partícula.
     * @param velocity Velocidad de la partícula.
     * @param isShadow Si es una sombra o una nube.
     */
    public CloudParticle(Location location, float velocity, boolean isShadow) {
        super(location, new Vector2f(velocity, 0), 0, Float.MIN_VALUE, 0);
        this.IS_SHADOW = isShadow;
    }

    @Override
    public Texture getTexture() {
        return this.IS_SHADOW? Textures.CLOUD_SHADOW_PARTICLE: Textures.CLOUD_PARTICLE;
    }

    @Override
    protected int getMaxLifeTime() {
        return Integer.MAX_VALUE;
    }
}
