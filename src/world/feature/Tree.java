package world.feature;

import main.Main;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import world.location.Location;

public class Tree extends Feature {
    private static final Vector2i FEATURE_SIZE = new Vector2i(1, 2), RANDOM_OFFSET = new Vector2i(4, 4);

    public Tree(Location location) {
        super(location, Tree.FEATURE_SIZE, FeatureType.TREE);
    }

    @Override
    public Vector2i getRandomOffset() {
        return Tree.RANDOM_OFFSET;
    }
}
