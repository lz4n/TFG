package world.feature;

import org.joml.Vector2i;
import world.location.Location;
import world.terrain.Terrain;

public class Rock extends Feature {
    private static final Vector2i FEATURE_SIZE = new Vector2i(1, 1), RANDOM_OFFSET = new Vector2i(0, 6);

    protected Rock(Location location) {
        super(location, Rock.FEATURE_SIZE, FeatureType.ROCK, 0);
    }

    @Override
    public Vector2i getRandomOffset() {
        return Rock.RANDOM_OFFSET;
    }

    @Override
    protected boolean checkSpecificConditions() {
        return !this.getLocation().getTerrain().getType().equals(Terrain.TerrainType.WATER);
    }
}
