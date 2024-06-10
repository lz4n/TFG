package world.feature;

import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.render.scene.WorldScene;
import world.location.Location;
import world.terrain.Terrain;

/**
 * Feature que representa un árbol.
 */
public class Tree extends Feature {
    /**
     * @param location Posición donde se va a generar la feature.
     * @param variant Variante de la feature.
     */
    public Tree(Location location, int variant) {
        super(location, new Vector2f(16, 32).div(WorldScene.SPRITE_SIZE), FeatureType.TREE, variant);
    }

    @Override
    public boolean checkSpecificConditions() {
        return this.getLocation().getTerrain().getType().equals(Terrain.TerrainType.GRASS);
    }
}
