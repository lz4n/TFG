package world.feature.building.jobSite;

import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.render.scene.WorldScene;
import world.entity.Duck;
import world.feature.Feature;
import world.location.Location;
import world.terrain.Terrain;

public class Port extends JobSite {
    public Port(Location location) {
        super(location, new Vector2i(2, 3), FeatureType.HOUSE, 7);
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
        } else {
            System.out.println(thereMustBeTerrainHere.toString() + thereMustBeTerrainHere.getTerrain().getType() + "2");
        }

        thereMustBeTerrainHere.add(0, 1);
        if (thereMustBeTerrainHere.getTerrain().getType().isBuildable()) {
            return true;
        } else {
            System.out.println(thereMustBeTerrainHere.toString() + thereMustBeTerrainHere.getTerrain().getType()+ "3");
        }

        return false;
    }

    @Override
    public Vector2f getRealSize() {
        return new Vector2f(25, 39).div(WorldScene.SPRITE_SIZE);
    }
}
