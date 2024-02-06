package world;

import main.Main;
import utils.Logger;
import utils.render.scene.WorldScene;

public class WorldGenerator extends Thread {
    @Override
    public void run() {
        for (int x = 0; x < Main.world.getSize(); x++) for (int y = Main.world.getSize() -1; y >=0; y--) {
            Main.world.getTerrain(x, y).getType().getMesh().addVertex(
                    x  - (1f/WorldScene.SPRITE_SIZE),
                    y  - (1f/WorldScene.SPRITE_SIZE),
                    1 + (1f/WorldScene.SPRITE_SIZE) *2,
                    1 + (1f/WorldScene.SPRITE_SIZE) *2
            );
        }
        Logger.sendMessage("Mundo generado correctamente.", Logger.LogMessageType.INFO);
    }
}
