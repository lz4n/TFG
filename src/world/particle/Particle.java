package world.particle;

import main.Main;
import utils.render.texture.Texture;
import world.location.Location;
import world.tick.ForceTicking;
import world.tick.Ticking;

public abstract class Particle extends Ticking {
    protected Location location;
    protected float rotation = 0, scale = 1;
    protected int lifeTime = 0;

    public Particle(Location location) {
        this.location = location;
    }

    @Override
    public void onTick(long deltaTime) {
        if (this.lifeTime++ > this.getMaxLifeTime()) {
            Main.world.removeParticle(this);
        }

        if (this.location.isOutOfTheWorld()) {
            Main.world.removeParticle(this);
        }
    }

    public Location getLocation() {
        return location.clone();
    }

    public float getRotation() {
        return this.rotation;
    }

    public float getScale() {
        return this.scale;
    }

    public abstract Texture getTexture();

    protected abstract int getMaxLifeTime();
}