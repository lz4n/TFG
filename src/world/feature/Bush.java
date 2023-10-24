package world.feature;

import org.joml.Vector2d;
import org.joml.Vector2i;
import world.location.Location;

public class Bush extends Feature {
    private static final Vector2i FEATURE_SIZE = new Vector2i(1, 1), RANDOM_OFFSET = new Vector2i(0, 2);

    public Bush(Location location) {
        super(location, Bush.FEATURE_SIZE, FeatureType.BUSH);
    }

    @Override
    public Vector2i getRandomOffset() {
        return Bush.RANDOM_OFFSET;
    }

    @Override
    public boolean canFeatureOverlapsWith(Feature feature) {
        return false;
    }
}
