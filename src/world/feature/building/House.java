package world.feature.building;

import main.Main;
import org.joml.Vector2f;
import org.joml.Vector2i;
import utils.render.scene.Scene;
import utils.render.scene.WorldScene;
import world.entity.Duck;
import world.location.Location;
import world.particle.SmokeParticle;
import world.tick.Tickable;

public class House extends Building implements Tickable {
    public House(Location location, int variant) {
        super(location, House.getHouseSize(variant +1), FeatureType.HOUSE, variant);
        switch (variant) {
            case 1 -> this.inhabitants = new Duck[3];
            case 2 -> this.inhabitants = new Duck[6];
            case 3 -> this.inhabitants = new Duck[9];
            case 4 -> this.inhabitants = new Duck[12];
            case 5 -> this.inhabitants = new Duck[15];
            case 6 -> this.inhabitants = new Duck[21];
            case 7 -> this.inhabitants = new Duck[25];
        }
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
