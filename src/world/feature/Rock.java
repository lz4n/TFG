package world.feature;

import org.joml.Vector2f;
import org.joml.Vector2i;
import world.location.Location;
import world.terrain.Terrain;

/**
 * Feature que representa una piedra.
 */
public class Rock extends Feature {
    /**
     * Tamaño de la feature en unidades in-game.
     */
    private static final Vector2i FEATURE_SIZE = new Vector2i(1, 1);

    /**
     * @param location Donde se va a generar la feature.
     */
    protected Rock(Location location) {
        super(location, Rock.FEATURE_SIZE, FeatureType.ROCK, 0);
    }

    @Override
    protected boolean checkSpecificConditions() {
        return !this.getLocation().getTerrain().getType().equals(Terrain.TerrainType.WATER);
    }
}
