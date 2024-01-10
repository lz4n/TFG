package utils;

import listener.KeyListener;
import listener.MouseListener;
import org.lwjgl.glfw.GLFW;

public enum KeyBind {
    NONE(GLFW.GLFW_KEY_UNKNOWN),
    INTERACT(GLFW.GLFW_MOUSE_BUTTON_1),
    MOVE_UP(GLFW.GLFW_KEY_W),
    MOVE_DOWN(GLFW.GLFW_KEY_S),
    MOVE_RIGHT(GLFW.GLFW_KEY_D),
    MOVE_LEFT(GLFW.GLFW_KEY_A),
    TOGGLE_PAUSE(GLFW.GLFW_KEY_P),
    TOGGLE_GAME_SPEED(GLFW.GLFW_KEY_X),
    TOGGLE_BULLDOZER(GLFW.GLFW_KEY_B),
    TOGGLE_GAME_MENU(GLFW.GLFW_KEY_ESCAPE),
    TOGGLE_HIDING_UI(GLFW.GLFW_KEY_F1),
    DEBUG_SPAWN_ENTITY(GLFW.GLFW_KEY_E);

    private int key;

    KeyBind(int key) {
        this.key = key;
    }

    public boolean checkKey(int keyToCheck) {
        return this.key == keyToCheck;
    }

    public boolean isPressed() {
        try {
            return KeyListener.isKeyPressed(this.key) || MouseListener.isMouseButtonPressed(this.key);
        } catch (ArrayIndexOutOfBoundsException exception) {
            return false;
        }

    }

    public static KeyBind getKeyBind(int key) {
        for (KeyBind keyBind : KeyBind.values()) {
            if (keyBind.checkKey(key)) return keyBind;
        }
        return KeyBind.NONE;
    }
}
