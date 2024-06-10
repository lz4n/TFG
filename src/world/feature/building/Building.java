package world.feature.building;

import main.Main;
import org.joml.Vector2f;
import org.joml.Vector2i;
import ui.tooltip.Tooltip;
import world.entity.Duck;
import world.feature.Feature;
import world.location.Location;
import world.terrain.Terrain;

/**
 * Representa un edificio en el mundo del juego. Los edificios pueden variar en tamaño,
 * tipo y características, y pueden tener efectos en el entorno del juego y en las entidades dentro de él.
 */
public abstract class Building extends Feature {

    /**
     * Constructor para la clase Building.
     *
     * @param location La ubicación del edificio.
     * @param sizeInBlocks El tamaño del edificio en bloques del mundo del juego.
     * @param featureType El tipo de característica del edificio.
     * @param variant La variante específica del edificio.
     */
    public Building(Location location, Vector2f sizeInBlocks, FeatureType featureType, int variant) {
        super(location, sizeInBlocks, featureType, variant);
    }

    /**
     * Acción a realizar cuando el edificio se agrega al mundo del juego.
     * Este método puede ser sobreescrito por las subclases para definir comportamientos específicos.
     */
    public void onAdd() {

    }

    /**
     * Acción a realizar cuando el edificio se elimina del mundo de juego.
     * Este método puede ser sobreescrito por las subclases para definir comportamientos específicos.
     */
    public void onRemove() {

    }

    /**
     * Obtiene los ingresos asociados con el edificio.
     * Este método debe ser implementado por las subclases para definir los ingresos específicos de cada edificio.
     *
     * @return Los ingresos del edificio.
     */
    public abstract int getIncomes();

    /**
     * Obtiene el tooltip que se muestra al interactuar con el edificio.
     * Este método debe ser implementado por las subclases para definir la información específica de cada edificio.
     *
     * @return Un tooltip con información sobre el edificio.
     */
    public Tooltip getTooltip() {
        return null;
    }

    @Override
    public boolean checkSpecificConditions() {
        Location location = this.getLocation();
        for (int x = (int) location.getX(); x <= location.getX() +this.getBlockSize().x() -1; x++) {
            for (int y = (int) location.getY(); y <= location.getY() +this.getBlockSize().y() -1; y++) {
                Terrain.TerrainType terrainType = Main.world.getTerrain(x, y).getType();
                if (!terrainType.isBuildable()) {
                    return false;
                }
            }
        }
        return true;
    }
}
