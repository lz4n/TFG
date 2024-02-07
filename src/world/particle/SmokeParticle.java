package world.particle;

import main.Main;
import org.joml.Vector2f;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.location.Location;
import world.tick.ForceTicking;

/**
 * Partícula que se genera cuando se utiliza el bulldozer en algún elemento del juego.
 */
@ForceTicking
public class SmokeParticle extends AbstractPhysicsParticle {

    /**
     * @param location Posición donde se va a generar la partícula.
     */
    public SmokeParticle(Location location) {
        super(location,
                new Vector2f(Main.RANDOM.nextFloat(-0.0001f, 0.001f), Main.RANDOM.nextFloat(0.01f, 0.08f)),
                0f,
                -0.07f,
                0f);

        super.scale = Main.RANDOM.nextFloat(0.7f, 1);
        super.rotation = Main.RANDOM.nextInt(360 +1);
    }

    @Override
    public Texture getTexture() {
        return Textures.SMOKE_PARTICLE;
    }

    @Override
    protected int getMaxLifeTime() {
        return Main.RANDOM.nextInt(300, 450);
    }

    @Override
    public void onTick(long deltaTime) {
        super.onTick(deltaTime);

        super.rotation += 0.001f;
        if (super.rotation > 360) {
            super.rotation = 0;
        }
    }
}
