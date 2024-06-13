package game.utils.player.inventory;

import game.utils.render.texture.Texture;
import game.world.terrain.Terrain;

/**
 * Representa un item que sirve para colocar terreno.
 */
public class TerrainItem extends Item {
    private final Terrain.TerrainType TERRAIN_TO_PLACE;

    /**
     * Constructor para un ítem de terreno, inicializa con la textura proporcionada, el tipo de terreno a colocar,
     * nombre, descripción y precio del ítem.
     *
     * @param texture       La textura del ítem.
     * @param terrainToPlace El tipo de terreno que este ítem colocará.
     * @param name          El nombre del ítem.
     * @param description   La descripción del ítem.
     * @param price         El precio del ítem.
     */
    public TerrainItem(Texture texture, Terrain.TerrainType terrainToPlace, String name, String description, int price) {
        super(texture, name, description, price);
        this.TERRAIN_TO_PLACE = terrainToPlace;
    }

    /**
     * @return Terreno que se va a colocar cuando se haga click.
     */
    public Terrain.TerrainType getTerrainToPlace() {
        return this.TERRAIN_TO_PLACE;
    }
}
