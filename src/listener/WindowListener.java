package listener;

import org.lwjgl.opengl.GL20;
import utils.render.Window;

/**
 * Listener para eventos de la ventana.
 */
public class WindowListener {

    /**
     * Evento que se llama cada vez que se cambia el tamaño de la ventana.
     * @param window Identificador numérico de la ventana.
     * @param width Nuevo ancho de la ventana.
     * @param height Nueva altura de la ventana.
     */
    public static void windowCallback(long window, int width, int height) {
        GL20.glViewport(0, 0, width, height);
        Window.currentScene.resizeWindow();
    }
}
