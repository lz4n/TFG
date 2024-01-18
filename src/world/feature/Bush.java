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
     * Desplazamiento aleatorio de la feature en unidades in-game.
     */
    private static final Vector2f RANDOM_OFFSET = new Vector2f(0, .5f);

    /**
     * @param location Posición donde se va a generar la feature.
     */
    protected Bush(Location location) {
        super(location, Bush.FEATURE_SIZE, FeatureType.BUSH, 0);
    }

    @Override
    protected boolean checkSpecificConditions() {
        return this.getLocation().getTerrain().getType().equals(Terrain.TerrainType.GRASS);
    }

    @Override
    public Vector2f getRandomOffset() {
        return Bush.RANDOM_OFFSET;
    }
}
