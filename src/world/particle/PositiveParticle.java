package world.particle;

import main.Main;
import org.joml.Vector2f;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.location.Location;
import world.tick.ForceTicking;

/**
 * Partícula utilizada para hacer llegar al usuario un input positivo.
 */
@ForceTicking
public class PositiveParticle extends AbstractPhysicsParticle {

    /**
     * @param location Posición donde se va a generar la partícula.
     */
    public PositiveParticle(Location location) {
        super(location,
                new Vector2f(Main.RANDOM.nextFloat(-.03f, .03f), Main.RANDOM.nextFloat(-0.03f, 0.03f)),
                0.003f,
                Float.MIN_VALUE,
                0);

        super.scale = Main.RANDOM.nextFloat(0.5f, 1f);
    }

    @Override
    public Texture getTexture() {
        return Textures.POSITIVE_PARTICLE;
    }

    @Override
    protected int getMaxLifeTime() {
        return 50;
    }
}
