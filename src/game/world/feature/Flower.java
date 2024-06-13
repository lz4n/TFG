package game.world.feature;

import game.utils.render.scene.WorldScene;
import org.joml.Vector2f;
import game.world.location.Location;
import game.world.terrain.Terrain;

/**
 * Feature que representa una flor.
 */
public class Flower extends Feature {

    /**
     * @param location Posición donde se va a generar la feature.
     * @param variant Variante de la feature.
     */
    public Flower(Location location, int variant) {
        super(location, new Vector2f(16, 16).div(WorldScene.SPRITE_SIZE), FeatureType.FLOWER, variant);
    }

    @Override
    public boolean checkSpecificConditions() {
        return this.getLocation().getTerrain().getType().equals(Terrain.TerrainType.GRASS);
    }
}
