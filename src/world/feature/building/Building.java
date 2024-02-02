package world.feature.building;

import main.Main;
import org.joml.Vector2i;
import world.feature.Feature;
import world.location.Location;
import world.terrain.Terrain;

public abstract class Building extends Feature {
    public Building(Location location, Vector2i sizeInBlocks, FeatureType featureType, int variant) {
        super(location, sizeInBlocks, featureType, variant);
    }

    @Override
    protected boolean checkSpecificConditions() {
        Location location = this.getLocation();
        for (int x = (int) location.getX(); x <= location.getX() +this.getSize().x() -1; x++) {
            for (int y = (int) location.getY(); y <= location.getY() +this.getSize().y() -1; y++) {
                Terrain.TerrainType terrainType = Main.world.getTerrain(x, y).getType();
                if (terrainType.equals(Terrain.TerrainType.WATER) || terrainType.equals(Terrain.TerrainType.SNOW)) {
                    return false;
                }
            }
        }
        return true;
    }
}
