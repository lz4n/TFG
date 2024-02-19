package listener;

import main.Main;
import org.lwjgl.glfw.GLFW;
import ui.container.Inventory;
import utils.KeyBind;
import utils.render.Window;

/**
 * Listener para los eventos de teclado. Rastrea el estado de cada tecla y lanza un evento cuando se presiona una.
 * @author Izan
 */
public class KeyListener {
    /**
     * Almacena si una tecla está siendo pulsada o no. Cada índice se refiere a una tecla, y hay 348 teclas.<br>Las teclas son constantes de tipo <code>int</code> de la clase <code>GLFW</code>, por ejemplo: <code>GLFW.GLFW_KEY_UP</code>.
     * @see GLFW
     */
    private static boolean[] isKeyPressed = new boolean[348]; //Hay 348 teclas

    /**
     * Se llama cada vez que se presiona una tecla.
     * @param window Ventana en la que se ha presionado la tecla.
     * @param key Tecla que se ha presionado.
     * @param scancode Código único de la tecla presionada.
     * @param action Acción que se ha realizado.
     * @param mods Modificador de teclado, por ejemplo <code>ctrl</code>, <code>alt</code>, <code>sifht</code>.
     * @see GLFW
     */
    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (key != GLFW.GLFW_KEY_UNKNOWN) {
            KeyListener.isKeyPressed[key] = action == GLFW.GLFW_PRESS || action ==  GLFW.GLFW_REPEAT;

            //Acciones que se realizan cuando se presiona una tecla.
            if (action == GLFW.GLFW_PRESS) {
                if (Main.PLAYER.isPaused()) {
                    if (KeyBind.TOGGLE_GAME_MENU.checkKey(key)) {
                        Main.PLAYER.openInventory();
                    }
                } else {
                    switch (KeyBind.getKeyBind(key)) {
                        case KeyBind.TOGGLE_HIDING_UI:
                            Main.PLAYER.toggleIsHidingUi();
                            break;
                        case KeyBind.TOGGLE_GAME_SPEED:
                            Main.PLAYER.toggleGameSpeed();
                            break;
                        case KeyBind.TOGGLE_PAUSE:
                            Main.PLAYER.togglePauseGame();
                            break;
                        case KeyBind.TOGGLE_BULLDOZER:
                            Main.PLAYER.toggleBulldozer();
                            break;
                        case KeyBind.TOGGLE_GAME_MENU:
                            Main.PLAYER.openMenu();
                            break;
                        case OPEN_TAB_1:
                            if (Main.PLAYER.getContainer() instanceof Inventory inventory) {
                                inventory.goToTab(1);
                            }
                            break;
                        case OPEN_TAB_2:
                            if (Main.PLAYER.getContainer() instanceof Inventory inventory) {
                                inventory.goToTab(2);
                            }
                            break;
                        case OPEN_TAB_3:
                            if (Main.PLAYER.getContainer() instanceof Inventory inventory) {
                                inventory.goToTab(3);
                            }
                            break;
                        case OPEN_TAB_4:
                            if (Main.PLAYER.getContainer() instanceof Inventory inventory) {
                                inventory.goToTab(4);
                            }
                            break;
                        case OPEN_TAB_5:
                            if (Main.PLAYER.getContainer() instanceof Inventory inventory) {
                                inventory.goToTab(5);
                            }
                            break;
                        case OPEN_TAB_6:
                            if (Main.PLAYER.getContainer() instanceof Inventory inventory) {
                                inventory.goToTab(6);
                            }
                            break;
                        case OPEN_TAB_7:
                            if (Main.PLAYER.getContainer() instanceof Inventory inventory) {
                                inventory.goToTab(7);
                            }
                            break;
                        case OPEN_TAB_8:
                            if (Main.PLAYER.getContainer() instanceof Inventory inventory) {
                                inventory.goToTab(8);
                            }
                            break;
                        case OPEN_TAB_9:
                            if (Main.PLAYER.getContainer() instanceof Inventory inventory) {
                                inventory.goToTab(9);
                            }
                            break;
                        case TOGGLE_FULLSCREEN:
                            Window.toggleFullscreen();
                            break;
                    }
                }
            }
        }
    }

    /**
     * Devuelve si una tecla está siendo presionada o no.
     * @param key Tecla de la cual queremos saber si está siendo presionada o no.
     * @return Si está siendo presionada o no.
     */
    public static boolean isKeyPressed(int key) {
        return KeyListener.isKeyPressed[key];
    }
}
