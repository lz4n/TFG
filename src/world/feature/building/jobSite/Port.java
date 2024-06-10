package world.feature.building.jobSite;

import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.render.scene.WorldScene;
import world.entity.Duck;
import world.feature.Feature;
import world.location.Location;
import world.terrain.Terrain;

/**
 * Representa un puerto en el mundo del juego.
 * Un puerto debe estar ubicado junto al agua y puede emplear a patos.
 */
public class Port extends JobSite {

    /**
     * Constructor para la clase Port.
     *
     * @param location La ubicación del puerto.
     */
    public Port(Location location) {
        super(location, new Vector2f(25, 39).div(WorldScene.SPRITE_SIZE), FeatureType.BUILDING, 7);
        if (super.getLocation().add(1, 0).getTerrain().getType().equals(Terrain.TerrainType.WATER)) {
            super.setFlip(Feature.FLIP_HORIZONTAL);
        } else  if (super.getLocation().add(-1, 0).getTerrain().getType().equals(Terrain.TerrainType.WATER)) {
            location.add(-1, 0);
        }

        if (super.getLocation().add(0, -1).getTerrain().getType().equals(Terrain.TerrainType.WATER)) {
            location.add(0, -1);
        }

        super.workers = new Duck[50];
    }

    @Override
    public boolean checkSpecificConditions() {
        Location thereMustBeWaterHere = super.getLocation();
        if (super.getFlip() == Feature.FLIP_HORIZONTAL) {
            thereMustBeWaterHere.add(1, 0);
        }
        if (!thereMustBeWaterHere.getTerrain().getType().equals(Terrain.TerrainType.WATER)) {
            return false;
        }

        Location thereMustBeTerrainHere = thereMustBeWaterHere.clone();
        thereMustBeTerrainHere.add(0, 1);
        if (thereMustBeTerrainHere.getTerrain().getType().isBuildable()) {
            return true;
        }

        thereMustBeTerrainHere = super.getLocation();
        if (super.getFlip() != Feature.FLIP_HORIZONTAL) {
            thereMustBeTerrainHere.add(1, 0);
        }
        if (thereMustBeTerrainHere.getTerrain().getType().isBuildable()) {
            return true;
        }

        thereMustBeTerrainHere.add(0, 1);
        if (thereMustBeTerrainHere.getTerrain().getType().isBuildable()) {
            return true;
        }

        return false;
    }

    @Override
    public int getIncomes() {
        int incomes = this.getCost();

        for (Duck duck: this.workers) {
            if (duck != null) {
                incomes += (int) (70 *duck.getHappiness());
            }
        }

        return incomes;
    }

    @Override
    public int getCost() {
        return -220;
    }
}
