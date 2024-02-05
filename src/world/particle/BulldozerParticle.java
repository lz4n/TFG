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
public class BulldozerParticle extends AbstractPhysicsParticle {

    /**
     * @param location Posición donde se va a generar la partícula.
     */
    public BulldozerParticle(Location location) {
        super(location,
                new Vector2f(Main.RANDOM.nextFloat(-0.11f, 0.11f), Main.RANDOM.nextFloat(0.07f, 0.25f)),
                0.008f,
                -0.07f,
                0.015f);

        super.lifeTime = Main.RANDOM.nextInt(20);
        super.scale = Main.RANDOM.nextFloat(0.7f, 1);
        super.rotation = Main.RANDOM.nextInt(360 +1);
    }

    @Override
    public Texture getTexture() {
        return Textures.BULLDOZER_PARTICLE;
    }

    @Override
    protected int getMaxLifeTime() {
        return 50;
    }

    @Override
    public void onTick(long deltaTime) {
        super.onTick(deltaTime);

        super.rotation += 0.01f;
        if (super.rotation > 360) {
            super.rotation = 0;
        }
    }
}
