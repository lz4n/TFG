package world.feature.building;

import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.render.scene.WorldScene;
import world.location.Location;

public class PoliceStation extends Building {
    public PoliceStation(Location location) {
        super(location, new Vector2f(20, 19).div(WorldScene.SPRITE_SIZE), FeatureType.SERVICES, 1);
    }
}
