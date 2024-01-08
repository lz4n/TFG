package world.particle;

import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.render.mesh.EntityMesh;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.location.Location;

public class CloudParticle extends AbstractPhysicsParticle {
    private final boolean IS_SHADOW;

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
