package listener;

import main.Main;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import utils.KeyBind;
import utils.render.Window;
import utils.render.scene.WorldScene;
import world.location.Location;

/**
 * Listener para eventos de ratón. Registra la posición y si se está presionando algún botón del ratón.
 *
 * @author Izan
 */
public class MouseListener {
    /**
     * Posiciones actuales y anteriores del ratón.
     */
    private static double posX, posY, lastX, lastY;

    /**
     * Almacena si los botones del ratón están siendo pulsados o no. Cada índice es un botón.
     */
    private static boolean[] isMouseButtonPressed = new boolean[3];

    /**
     * Almacena si se está arrastrando (mover y mantener click) el ratón.
     */
    private static boolean isDragging;

    /**
     * Posición del ratón dentro del juego.
     */
    private static Location inGameLocation = new Location(0, 0);

    /**
     * Actualiza la posición del ratón dentro del juego y actualiza el selector.
     */
    public static void updateInGameLocation() {
        if (Window.currentScene instanceof WorldScene && Main.PLAYER != null) {
            MouseListener.inGameLocation = Main.PLAYER.getCamera().getInGameLocationMousePosition(new Vector2f((float) MouseListener.posX, (float) MouseListener.posY));
        }
    }

    /**
     * Evento que se llama cada vez que se cambia la posición del ratón.
     * @param window Ventana en la que se ha movido el ratón.
     * @param posX Nueva posición del ratón en el eje X.
     * @param posY Nueva posición del ratón en el eje Y.
     */
    public static void mousePosCallback(long window, double posX,double posY) {
        MouseListener.lastX = MouseListener.posX;
        MouseListener.lastY = MouseListener.posY;

        MouseListener.posX = posX;
        MouseListener.posY = posY;

        MouseListener.updateInGameLocation();

        MouseListener.isDragging = MouseListener.isMouseButtonPressed[0] || MouseListener.isMouseButtonPressed[1] || MouseListener.isMouseButtonPressed[2];

        Window.currentScene.moveMouse((float) posX, (float) posY);

        if (!Main.PLAYER.isMouseOnInventory() && !Main.PLAYER.isPaused()) {
            //Destruir features cuando se mantiene click sobre ellas.
            if (KeyBind.INTERACT.isPressed()) {
                if (Main.PLAYER.isUsingBulldozer()) {
                    Main.PLAYER.destroySelectedFeature();
                }
            }
        }
    }

    /**
     * Evento que se llama cada vez que se presiona un botón del ratón.
     * @param window Ventana en la que se ha clickado el ratón.
     * @param button Botón del ratón que se ha presionado.
     * @param action Acción que se ha realizado.
     * @param mods Modificador de teclado, por ejemplo <code>ctrl</code>, <code>alt</code>, <code>sifht</code>.
     */
    public static void mouseButtonCallback(long window, int button, int action, int mods) {
      if (action == GLFW.GLFW_PRESS) {
          MouseListener.setIsMouseButtonPressed(button, true);
          Window.currentScene.click((float) MouseListener.posX, (float) MouseListener.posY);

          if (!Main.PLAYER.isMouseOnInventory() && !Main.PLAYER.isPaused()) {
              //Colocar/Destruir features cuando se hace click sobre ellas.
              if (KeyBind.INTERACT.isPressed()) {
                  if (Main.PLAYER.isUsingBulldozer()) {
                      Main.PLAYER.destroySelectedFeature();
                  } else {
                      Main.PLAYER.placeSelectedItem();
                  }
              }
          }
      } else if (action == GLFW.GLFW_RELEASE) {
          MouseListener.setIsMouseButtonPressed(button, false);
          MouseListener.isDragging = false;
      }
    }

    /**
     * Evento que se llama cuando se mueve la rueda del ratón.
     * @param window Ventana en la que se ha movido la rueda del ratón.
     * @param offsetX Movimiento de la rueda del ratón en el eje X.
     * @param offsetY Movimiento de la rueda del ratón en el eje Y.
     */
    public static void mouseScrollCallback(long window, double offsetX, double offsetY) {
        if (!Main.PLAYER.isPaused()) {
            if (offsetY >= 1) {
                Main.PLAYER.getCamera().zoomIn();
            } else if (offsetY <= -1) {
                Main.PLAYER.getCamera().zoomOut();
            }
        }
    }

    /**
     * Establece si un botón del ratón está siendo pulsado o no.
     * @param button Botón del cuál se quiere actualizar su estado.
     * @param isPressed Estado del botón.
     */
    private static void setIsMouseButtonPressed(int button, boolean isPressed) {
        if (button < MouseListener.isMouseButtonPressed.length) {
            MouseListener.isMouseButtonPressed[button] = isPressed;
        }
    }

    public static boolean isMouseButtonPressed(int button) {
        return MouseListener.isMouseButtonPressed[button];
    }

    /**
     * @return Desplazamiento en el eje X del ratón.
     */
    public static double getDx() {
        return MouseListener.lastX - MouseListener.posX;
    }

    /**
     * Desplazamiento en el eje Y del ratón.
     * @return
     */
    public static double getDy() {
        return MouseListener.lastY - MouseListener.posY;
    }

    public static Location getInGameLocation() {
        return MouseListener.inGameLocation.clone();
    }
}
