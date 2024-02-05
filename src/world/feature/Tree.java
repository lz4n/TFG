package world.feature;

import org.joml.Vector2f;
import org.joml.Vector2i;
import world.location.Location;
import world.terrain.Terrain;

/**
 * Feature que representa un árbol.
 */
public class Tree extends Feature {
    /**
     * Tamaño de la feature en unidades in-game.
     */
    private static final Vector2i FEATURE_SIZE = new Vector2i(1, 2);

    /**
     * @param location Posición donde se va a generar la feature.
     * @param variant Variante de la feature.
     */
    protected Tree(Location location, int variant) {
        super(location, Tree.FEATURE_SIZE, FeatureType.TREE, variant);
    }

    @Override
    public boolean checkSpecificConditions() {
        return this.getLocation().getTerrain().getType().equals(Terrain.TerrainType.GRASS);
    }
}
