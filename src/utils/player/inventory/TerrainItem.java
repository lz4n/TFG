package utils.player.inventory;

import utils.render.texture.Texture;
import world.terrain.Terrain;

/**
 * Representa un item que sirve para colocar terreno.
 */
public class TerrainItem extends Item {

    /**
     * Terreno que se va a colocar.
     */
    private final Terrain.TerrainType TERRAIN_TO_PLACE;

    /**
     * @param texture Textura del item.
     * @param terrainToPlace Terreno que se va a colocar.
     */
    public TerrainItem(Texture texture, Terrain.TerrainType terrainToPlace) {
        super(texture);
        this.TERRAIN_TO_PLACE = terrainToPlace;
    }

    /**
     * @return Terreno que se va a colocar cuando se haga click.
     */
    public Terrain.TerrainType getTerrainToPlace() {
        return this.TERRAIN_TO_PLACE;
    }
}
