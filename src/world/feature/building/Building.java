package world.feature.building;

import main.Main;
import org.joml.Vector2f;
import org.joml.Vector2i;
import ui.tooltip.Tooltip;
import world.entity.Duck;
import world.feature.Feature;
import world.location.Location;
import world.terrain.Terrain;

public abstract class Building extends Feature {

    public Building(Location location, Vector2f sizeInBlocks, FeatureType featureType, int variant) {
        super(location, sizeInBlocks, featureType, variant);
    }

    public void onAdd() {

    }

    public void onRemove() {

    }

    public abstract int getIncomes();

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
