package world;

import main.Main;
import utils.Logger;
import utils.render.scene.WorldScene;

public class WorldGenerator extends Thread {
    @Override
    public void run() {
        for (int x = 0; x < Main.world.getSize(); x++) for (int y = Main.world.getSize() -1; y >=0; y--) {
            Main.world.getTerrain(x, y).getType().getMesh().addVertex(x, y, 1, 1);
        }
        Logger.sendMessage("Mundo generado correctamente.", Logger.LogMessageType.INFO);
    }
}
