package world.particle;

import main.Main;
import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.render.mesh.EntityMesh;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;
import world.location.Location;

public class BulldozerParticle extends AbstractPhysicsParticle {
    private static final Texture TEXTURE = new StaticTexture("assets/textures/particle/bulldozer.png");
    private static final EntityMesh MESH = new EntityMesh(new Vector2i(1, 1));

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
    public EntityMesh getMesh() {
        return BulldozerParticle.MESH;
    }

    @Override
    public Texture getTexture() {
        return BulldozerParticle.TEXTURE;
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
