package world.particle;

import main.Main;
import org.joml.Vector2f;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.location.Location;

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
