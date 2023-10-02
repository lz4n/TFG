package listener;

import org.lwjgl.glfw.GLFW;

/**
 * Listener para los eventos de teclado. Rastrea el estado de cada tecla y lanza un evento cuando se presiona una.
 * @since 2-10-2023
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
            KeyListener.isKeyPressed[key] = action == GLFW.GLFW_PRESS;
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
