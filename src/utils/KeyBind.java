package utils;

import listener.KeyListener;
import listener.MouseListener;
import org.lwjgl.glfw.GLFW;

/**
 * Gestiona las asignaciones de teclas y del teclado.
 */
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
    OPEN_TAB_1(GLFW.GLFW_KEY_1),
    OPEN_TAB_2(GLFW.GLFW_KEY_2),
    OPEN_TAB_3(GLFW.GLFW_KEY_3),
    OPEN_TAB_4(GLFW.GLFW_KEY_4),
    OPEN_TAB_5(GLFW.GLFW_KEY_5),
    OPEN_TAB_6(GLFW.GLFW_KEY_6),
    OPEN_TAB_7(GLFW.GLFW_KEY_7),
    OPEN_TAB_8(GLFW.GLFW_KEY_8),
    OPEN_TAB_9(GLFW.GLFW_KEY_9),
    TOGGLE_FULLSCREEN(GLFW.GLFW_KEY_F11),
    DEBUG_SPAWN_ENTITY(GLFW.GLFW_KEY_E);

    /**
     * Tecla o botón del ratón que está asignada.
     */
    private int key;

    KeyBind(int key) {
        this.key = key;
    }

    /**
     * Comprueba la tecla asignada es igual a la tecla que se pasa como parámetro.
     * @param keyToCheck Tecla que se quiere comprobar.
     * @return Si la tecla equivale a la asignación.
     */
    public boolean checkKey(int keyToCheck) {
        return this.key == keyToCheck;
    }

    /**
     * @return Si la tecla asignada está presionada o no.
     */
    public boolean isPressed() {
        try {
            return KeyListener.isKeyPressed(this.key) || MouseListener.isMouseButtonPressed(this.key);
        } catch (ArrayIndexOutOfBoundsException exception) {
            return false;
        }

    }

    /**
     * Comprueba cual acción tiene asignada la tecla que se pasa como parámetro.
     * @param key Tecla de la cual se quiere saber la acción.
     * @return Acción asignada a esa tecla, <code>KeyBind.NONE</code> si no se encuentra ninguna.
     * @see KeyBind#NONE
     */
    public static KeyBind getKeyBind(int key) {
        for (KeyBind keyBind : KeyBind.values()) {
            if (keyBind.checkKey(key)) return keyBind;
        }
        return KeyBind.NONE;
    }
}
