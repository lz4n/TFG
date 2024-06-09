package world.feature.building;

import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.render.scene.WorldScene;
import world.location.Location;

public class Hospital extends Building {
    public Hospital(Location location) {
        super(location, new Vector2f(24, 24).div(WorldScene.SPRITE_SIZE), FeatureType.SERVICES, 0);
    }
}
