package world.feature;

import org.joml.Vector2i;
import world.location.Location;
import world.terrain.Terrain;

public class Flower extends Feature {
    private static final Vector2i FEATURE_SIZE = new Vector2i(1, 1), RANDOM_OFFSET = new Vector2i(0, 4);

    protected Flower(Location location, int variant) {
        super(location, Flower.FEATURE_SIZE, FeatureType.FLOWER, variant);
    }

    @Override
    protected boolean checkSpecificConditions() {
        return this.getLocation().getTerrain().getType().equals(Terrain.TerrainType.GRASS);
    }

    @Override
    public Vector2i getRandomOffset() {
        return Flower.RANDOM_OFFSET;
    }
}