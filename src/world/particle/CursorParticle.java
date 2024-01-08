package world.particle;

import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.location.Location;
import world.tick.ForceTicking;

@ForceTicking
public class CursorParticle extends Particle {
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
