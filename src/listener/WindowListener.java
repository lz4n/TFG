package listener;

import org.lwjgl.opengl.GL20;
import utils.render.Window;

public class WindowListener {
    public static void windowCallback(long window, int width, int height) {
        GL20.glViewport(0, 0, width, height);
        Window.currentScene.resizeWindow();
    }
}
