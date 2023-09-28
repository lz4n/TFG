package listener;

import org.lwjgl.glfw.GLFW;

public class KeyListener {
    private static boolean[] isKeyPressed = new boolean[348]; //Hay 348 teclas

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (key != GLFW.GLFW_KEY_UNKNOWN) {
            KeyListener.isKeyPressed[key] = action == GLFW.GLFW_PRESS;
        }
    }

    public static boolean isKeyPressed(int key) {
        return KeyListener.isKeyPressed[key];
    }
}
