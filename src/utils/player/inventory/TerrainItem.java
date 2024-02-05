package utils.player.inventory;

import utils.render.texture.Texture;
import world.terrain.Terrain;

public class TerrainItem extends Item {

    private final Terrain.TerrainType TERRAIN_TO_PLACE;

    /**
     * @param texture        Textura del item.
     * @param terrainToPlace
     */
    public TerrainItem(Texture texture, Terrain.TerrainType terrainToPlace) {
        super(texture);
        this.TERRAIN_TO_PLACE = terrainToPlace;
    }

    public Terrain.TerrainType getTerrainToPlace() {
        return this.TERRAIN_TO_PLACE;
    }
}
