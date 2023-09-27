package world;

import main.Main;
import org.lwjgl.glfw.GLFW;
import utils.render.Window;
import utils.render.scene.WorldScene;

import javax.swing.*;

public class WorldGenerator extends Thread {
    private final World WORLD_TO_LOAD;
    private final WorldScene SCENE;

    public WorldGenerator(World worldToLoad, WorldScene scene) {
        this.WORLD_TO_LOAD = worldToLoad;
        this.SCENE = scene;
    }

    @Override
    public void run() {

        for (int x = 0; x < Main.WORLD.getSize(); x++) for (int y = 0; y < Main.WORLD.getSize(); y++) {
            System.out.println("POS: " + x + "-" + y);
            this.SCENE.RENDER.get(Main.WORLD.getTerrain(x, y).getType()).addVertex(x, y);
        }
    }
}
