package world.feature;

import org.joml.Vector2i;
import world.location.Location;
import world.terrain.Terrain;

public class Bush extends Feature {
    private static final Vector2i FEATURE_SIZE = new Vector2i(1, 1), RANDOM_OFFSET = new Vector2i(0, 2);

    protected Bush(Location location) {
        super(location, Bush.FEATURE_SIZE, FeatureType.BUSH, 0);
    }

    @Override
    protected boolean checkSpecificConditions() {
        return this.getLocation().getTerrain().getType().equals(Terrain.TerrainType.GRASS);
    }

    @Override
    public Vector2i getRandomOffset() {
        return Bush.RANDOM_OFFSET;
    }
}
