package world.feature;

import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.render.scene.WorldScene;
import world.location.Location;
import world.terrain.Terrain;

/**
 * Feature que representa un pequeño arbusto.
 */
public class Bush extends Feature {

    /**
     * @param location Posición donde se va a generar la feature.
     */
    public Bush(Location location) {
        super(location, new Vector2f(16, 16).div(WorldScene.SPRITE_SIZE), FeatureType.BUSH, 0);
    }

    @Override
    public boolean checkSpecificConditions() {
        return this.getLocation().getTerrain().getType().equals(Terrain.TerrainType.GRASS);
    }
}
