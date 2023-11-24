package world.particle;

import main.Main;
import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.render.mesh.EntityMesh;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;
import world.location.Location;

public class PositiveParticle extends AbstractPhysicsParticle {
    private static final Texture TEXTURE = new StaticTexture("assets/textures/particle/positive.png");
    private static final EntityMesh MESH = new EntityMesh(new Vector2i(1, 1));

    public PositiveParticle(Location location) {
        super(location,
                new Vector2f(Main.RANDOM.nextFloat(-0.03f, 0.03f), Main.RANDOM.nextFloat(-0.03f, 0.03f)),
                0.003f,
                Float.MIN_VALUE,
                0);

        super.scale = Main.RANDOM.nextFloat(0.5f, 1f);
    }

    @Override
    public EntityMesh getMesh() {
        return PositiveParticle.MESH;
    }

    @Override
    public Texture getTexture() {
        return PositiveParticle.TEXTURE;
    }

    @Override
    protected int getMaxLifeTime() {
        return 50;
    }
}
