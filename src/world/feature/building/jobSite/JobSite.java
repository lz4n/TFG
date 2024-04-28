package world.feature.building.jobSite;

import org.joml.Vector2i;
import world.feature.building.Building;
import world.location.Location;

public abstract class JobSite extends Building {
    public JobSite(Location location, Vector2i sizeInBlocks, FeatureType featureType, int variant) {
        super(location, sizeInBlocks, featureType, variant);
    }
}
