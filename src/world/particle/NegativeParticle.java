package world.particle;

import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.location.Location;
import world.tick.ForceTicking;

@ForceTicking
public class NegativeParticle extends Particle {
    private static final Texture TEXTURE = Textures.NEGATIVE_PARTICLE;

    public NegativeParticle(Location location) {
        super(location);

        super.scale = 0;
    }

    @Override
    public void onTick(long deltaTime) {
        super.onTick(deltaTime);

        if (super.scale < 1) {
            super.scale += 0.1f;
        } else {
            super.scale = 1;
        }
    }

    @Override
    public Texture getTexture() {
        return NegativeParticle.TEXTURE;
    }

    @Override
    protected int getMaxLifeTime() {
        return 60;
    }
}
