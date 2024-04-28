package world.feature.building;

import org.joml.Vector2i;
import world.location.Location;
import world.tick.Tickable;

abstract class Work extends Building implements Tickable {
    public Work(Location location, Vector2i sizeInBlocks, FeatureType featureType, int variant) {
        super(location, sizeInBlocks, featureType, variant);
    }
    /*public Work(Location location, int variant) {
        super(location, Work.getHouseSize(variant +1), FeatureType.WORK, variant);
    }*/
}
