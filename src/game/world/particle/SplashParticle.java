package game.world.particle;

import game.Main;
import org.joml.Vector2f;
import game.utils.render.texture.Texture;
import game.utils.render.texture.Textures;
import game.world.location.Location;

/**
 * Partícula que se genera cuando un pato está en el agua.
 */
public class SplashParticle extends AbstractPhysicsParticle {
    /**
     * @param location        Posición donde se va a generar la partícula.
     */
    public SplashParticle(Location location) {
        super(location,
                new Vector2f(Main.RANDOM.nextFloat(-0.01f, 0.01f), Main.RANDOM.nextFloat(-0.007f, -0.002f)),
                0.008f,
                -0.1f,
                0.025f);
        ;
        super.scale = Main.RANDOM.nextFloat(.7f, .9f);
    }

    @Override
    public void onTick() {
        super.onTick();
    }

    @Override
    public Texture getTexture() {
        return Textures.SPLASH_PARTICLE;
    }

    @Override
    protected int getMaxLifeTime() {
        return 10;
    }
}
