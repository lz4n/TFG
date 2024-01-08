package world.particle;

import org.joml.Vector2f;
import world.location.Location;

public abstract class AbstractPhysicsParticle extends Particle {
    protected Vector2f motion;

    protected float maxGravity, gravityVelocity, motionDecay;

    public AbstractPhysicsParticle(Location location, Vector2f motion, float motionDecay, float maxGravity, float gravityVelocity) {
        super(location);
        this.motion = motion;
        this.motionDecay = motionDecay;
        this.maxGravity = maxGravity;
        this.gravityVelocity = gravityVelocity;
    }

    @Override
    public void onTick(long deltaTime) {
        super.onTick(deltaTime);

        super.location.add(this.motion.x(), this.motion.y());

        this.motion.add(this.motionDecay *AbstractPhysicsParticle.getDirection(this.motion.x()), this.motionDecay *AbstractPhysicsParticle.getDirection(this.motion.y()));
        if (this.motion.y() <= this.maxGravity) {
            this.motion.set(this.motion.x(), this.maxGravity);
        } else {
            this.motion.sub(0, this.gravityVelocity);
        }
    }

    private static int getDirection(float num) {
        if (num == 0) return 0;
        return num>0?-1:1;
    }
}
