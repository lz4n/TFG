package world.feature;

import org.joml.Vector2i;
import world.location.Location;

public class Flower extends Feature {
    private static final Vector2i FEATURE_SIZE = new Vector2i(1, 1), RANDOM_OFFSET = new Vector2i(0, 4);

    public Flower(Location location) {
        super(location, Flower.FEATURE_SIZE, FeatureType.FLOWER);
    }

    @Override
    public Vector2i getRandomOffset() {
        return Flower.RANDOM_OFFSET;
    }

    @Override
    public boolean canFeatureOverlapsWith(Feature feature) {
        return feature instanceof Flower;
    }
}
