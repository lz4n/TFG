package world.entity;

import org.joml.Vector2f;
import world.location.Location;

public class Duck extends Entity {
    public Duck(Location location) {
        super(EntityType.DUCK, location);
    }

    @Override
    public void onTick(long deltaTime) {
        if (Entity.RANDOM.nextFloat() >= 0.7) {
            this.move(new Vector2f(Entity.RANDOM.nextFloat() -0.5f, Entity.RANDOM.nextFloat() -0.5f));
        }
    }
}
