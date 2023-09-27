package world.entity;

import main.Main;
import org.joml.Vector2d;
import org.joml.Vector2f;
import utils.render.Window;
import utils.render.scene.WorldScene;

public class Player extends Entity {
    public Player(Vector2d location) {
        super(location);
    }

    @Override
    public void move(Vector2d movement) {
        super.move(movement);

        WorldScene.CAMERA.cameraPosition = new Vector2f((float) this.location.x() * WorldScene.SPRITE_SIZE, (float) this.location.y() * WorldScene.SPRITE_SIZE);
    }
}
