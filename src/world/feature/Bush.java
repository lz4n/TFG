package world.feature;

import org.joml.Vector2d;
import org.joml.Vector2i;

public class Bush extends Feature {
    public Bush(Vector2d location) {
        super(location, new Vector2i(1, 1), FeatureType.BUSH);
    }
}
