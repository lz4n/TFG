package world.feature.building;

import org.joml.Vector2i;
import world.feature.Feature;
import world.location.Location;

public abstract class Building extends Feature {
    public Building(Location location, Vector2i sizeInBlocks, FeatureType featureType, int variant) {
        super(location, sizeInBlocks, featureType, variant);
    }
}
