package world;

import main.Main;
import utils.Logger;
import utils.render.scene.WorldScene;

public class WorldGenerator {
    public void run() {
        for (int x = 0; x < Main.world.getSize(); x++) for (int y = Main.world.getSize() -1; y >=0; y--) {
            Main.world.getTerrain(x, y).addToMesh(x, y);
        }
        System.gc();
        Logger.sendMessage("Mundo generado correctamente.", Logger.LogMessageType.INFO);
    }
}
