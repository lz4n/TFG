package world.feature;

import org.joml.Vector2i;
import world.location.Location;
import world.terrain.Terrain;

public class Tree extends Feature {
    private static final Vector2i FEATURE_SIZE = new Vector2i(1, 2), RANDOM_OFFSET = new Vector2i(4, 4);

    protected Tree(Location location, int variant) {
        super(location, Tree.FEATURE_SIZE, FeatureType.TREE, variant);
    }

    @Override
    protected boolean checkSpecificConditions() {
        return this.getLocation().getTerrain().getType().equals(Terrain.TerrainType.GRASS);
    }

    @Override
    public Vector2i getRandomOffset() {
        return Tree.RANDOM_OFFSET;
    }
}
