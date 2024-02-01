package world.feature;

import org.joml.Vector2f;
import org.joml.Vector2i;
import world.location.Location;
import world.terrain.Terrain;

/**
 * Feature que representa una flor.
 */
public class Flower extends Feature {
    /**
     * Tamaño de la feature en unidades in-game.
     */
    private static final Vector2i FEATURE_SIZE = new Vector2i(1, 1);

    /**
     * @param location Posición donde se va a generar la feature.
     * @param variant Variante de la feature.
     */
    protected Flower(Location location, int variant) {
        super(location, Flower.FEATURE_SIZE, FeatureType.FLOWER, variant);
    }

    @Override
    protected boolean checkSpecificConditions() {
        return this.getLocation().getTerrain().getType().equals(Terrain.TerrainType.GRASS);
    }
}
