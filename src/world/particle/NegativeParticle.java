package world.particle;

import org.joml.Vector2i;
import utils.render.mesh.EntityMesh;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.location.Location;

public class NegativeParticle extends Particle {
    private static final Texture TEXTURE = Textures.NEGATIVE_PARTICLE;
    private static final EntityMesh MESH = new EntityMesh(new Vector2i(1, 1));

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
    public EntityMesh getMesh() {
        return NegativeParticle.MESH;
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
