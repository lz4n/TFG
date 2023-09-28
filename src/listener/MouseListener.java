package listener;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import utils.render.Window;
import utils.render.scene.WorldScene;

public class MouseListener {
    private static double scrollX, scrollY, posX, posY, lastX, lastY;
    private static boolean[] isMouseButtonPressed = new boolean[3];
    private static boolean isDragging;
    private static Vector2f inGameLocation;

    public static void updateInGameLocation() {
        if (Window.currentScene instanceof WorldScene worldScene) {
            MouseListener.inGameLocation = WorldScene.CAMERA.getInGameLocationMousePosition(new Vector2f((float) MouseListener.posX, (float) MouseListener.posY));
            worldScene.updateSelection((int) MouseListener.inGameLocation.x(), (int) MouseListener.inGameLocation.y());
        }
    }

    public static void mousePosCallback(long window, double posX,double posY) {
        MouseListener.lastX = MouseListener.posX;
        MouseListener.lastY = MouseListener.posY;

        MouseListener.posX = posX;
        MouseListener.posY = posY;

        MouseListener.updateInGameLocation();

        MouseListener.isDragging = MouseListener.isMouseButtonPressed[0] || MouseListener.isMouseButtonPressed[1] || MouseListener.isMouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
      if (action == GLFW.GLFW_PRESS) {
          MouseListener.setIsMouseButtonPressed(button, true);
      } else if (action == GLFW.GLFW_RELEASE) {
          MouseListener.setIsMouseButtonPressed(button, false);
          MouseListener.isDragging = false;
      }
    }

    public static void mouseScrollCallback(long window, double offsetX, double offsetY) {
        MouseListener.scrollX = offsetX;
        MouseListener.scrollY = offsetY;

        if (MouseListener.scrollY >= 1) {
            WorldScene.CAMERA.zoomIn();
        } else if (MouseListener.scrollY <= -1) {
            WorldScene.CAMERA.zoomOut();
        }
    }

    private static void setIsMouseButtonPressed(int button, boolean isPressed) {
        if (button < MouseListener.isMouseButtonPressed.length) {
            MouseListener.isMouseButtonPressed[button] = isPressed;
        }
    }

    public static double getDx() {
        return MouseListener.lastX - MouseListener.posX;
    }

    public static double getDy() {
        return MouseListener.lastY - MouseListener.posY;
    }
}
