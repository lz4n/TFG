package world.feature;

import org.joml.Vector2d;
import org.joml.Vector2i;
import world.location.Location;

public class Tree extends Feature {
    public Tree(Location location) {
        super(location, new Vector2i(1, 2), FeatureType.TREE);
    }
}
