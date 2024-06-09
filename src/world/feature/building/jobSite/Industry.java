package world.feature.building.jobSite;

import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.render.scene.WorldScene;
import world.entity.Duck;
import world.location.Location;

public class Industry extends JobSite {
    public Industry(Location location, int variant) {
        super(location, Industry.getSize(variant), FeatureType.JOB_SITE, variant);
        switch (variant) {
            case 0 -> this.workers = new Duck[1];
            case 1 -> this.workers = new Duck[6];
            case 2 -> this.workers = new Duck[9];
            case 3 -> this.workers = new Duck[12];
            case 4 -> this.workers = new Duck[15];
            case 5 -> this.workers = new Duck[21];
            case 6 -> this.workers = new Duck[25];
        }
    }




    private static Vector2f getSize(int level) {
        return switch (level) {
            case 1, 2, 3 -> new Vector2f(16, 16).div(WorldScene.SPRITE_SIZE);
            case 4 -> new Vector2f(24, 24).div(WorldScene.SPRITE_SIZE);
            case 5-> new Vector2f(22, 22).div(WorldScene.SPRITE_SIZE);
            default -> new Vector2f(0, 0);
        };
    }
}
