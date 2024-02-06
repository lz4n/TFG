package world.feature;

import org.joml.Vector2f;
import org.joml.Vector2i;
import world.location.Location;
import world.terrain.Terrain;

/**
 * Feature que representa un pequeño arbusto.
 */
public class Bush extends Feature {
    /**
     * Tamaño de la feature en unidades in-game.
     */
    private static final Vector2i FEATURE_SIZE = new Vector2i(1, 1);


    /**
     * @param location Posición donde se va a generar la feature.
     */
    protected Bush(Location location) {
        super(location, Bush.FEATURE_SIZE, FeatureType.BUSH, 0);
    }

    @Override
    public boolean checkSpecificConditions() {
        return this.getLocation().getTerrain().getType().equals(Terrain.TerrainType.GRASS);
    }
}
