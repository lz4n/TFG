package world.feature.building;

import main.Main;
import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.render.scene.Scene;
import utils.render.scene.WorldScene;
import world.location.Location;
import world.particle.SmokeParticle;
import world.tick.Tickable;

public class House extends Building implements Tickable {

    public House(Location location, int variant) {
        super(location, House.getHouseSize(variant +1), FeatureType.HOUSE, variant);
    }

    private static Vector2i getHouseSize(int level) {
        return switch (level) {
            case 1 -> new Vector2i(1, 1);
            case 2, 3, 4, 5, 6, 7 -> new Vector2i(2, 2);
            default -> new Vector2i(1, 1);
        };
    }

    @Override
    public void onTick(long deltaTime) {
        if (this.getVariant() +1 >= 3 && this.getVariant() +1 <= 5 && Main.RANDOM.nextFloat() > 0.97) {
            Main.world.spawnParticle(new SmokeParticle(this.getLocation().add(0.4f, 0.8f)));
        }
    }

    @Override
    public Vector2f getRealSize() {
        return switch (this.getVariant() +1) {
            case 1 -> new Vector2f(15, 12).div(WorldScene.SPRITE_SIZE);
            case 2 -> new Vector2f(15).div(WorldScene.SPRITE_SIZE);
            case 3 -> new Vector2f(17, 16).div(WorldScene.SPRITE_SIZE);
            case 4 -> new Vector2f(21, 18).div(WorldScene.SPRITE_SIZE);
            case 5-> new Vector2f(21, 24).div(WorldScene.SPRITE_SIZE);
            case 6 -> new Vector2f(23, 21).div(WorldScene.SPRITE_SIZE);
            case 7 -> new Vector2f(25, 22).div(WorldScene.SPRITE_SIZE);
            default -> super.getRealSize();
        };
    }
}
