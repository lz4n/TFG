package world;

import main.Main;
import org.lwjgl.glfw.GLFW;
import utils.Logger;
import utils.render.Window;
import utils.render.scene.WorldScene;

import javax.swing.*;
import java.util.Random;

public class WorldGenerator extends Thread {
    private final World WORLD_TO_LOAD;
    private final WorldScene SCENE;

    public WorldGenerator(World worldToLoad, WorldScene scene) {
        this.WORLD_TO_LOAD = worldToLoad;
        this.SCENE = scene;
    }

    @Override
    public void run() {
        for (int x = 0; x < Main.WORLD.getSize(); x++) for (int y = Main.WORLD.getSize() -1; y >=0; y--) {
            Main.WORLD.getTerrain(x, y).getType().getMesh().addVertex(x, y, 1, 1);
        }
        Logger.sendMessage("Mundo generado correctamente.", Logger.LogMessageType.INFO);
    }
}
