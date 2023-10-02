package world.feature;

import org.joml.Vector2d;
import org.joml.Vector2i;
import world.location.Location;

public class Bush extends Feature {
    public Bush(Location location) {
        super(location, new Vector2i(1, 1), FeatureType.BUSH);
    }
}
