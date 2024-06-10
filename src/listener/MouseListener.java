package listener;

import main.Main;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import ui.container.Frame;
import utils.KeyBind;
import utils.render.Window;
import utils.render.scene.WorldScene;
import world.entity.Duck;
import world.location.Location;

/**
 * Listener para eventos de ratón. Registra la posición y si se está presionando algún botón del ratón.
 *
 * @author Izan
 */
public class MouseListener {

    private static double posX, posY, lastX, lastY;
    private static boolean[] isMouseButtonPressed = new boolean[3];
    private static boolean isDragging;
    private static Location inGameLocation = new Location(0, 0);

    /**
     * Actualiza la ubicación en el juego del cursor del ratón según su posición actual en la pantalla.
     * @param posX la posición X del cursor en la pantalla.
     * @param posY la posición Y del cursor en la pantalla.
     * @param windowWidth el ancho de la ventana.
     * @param windowHeight la altura de la ventana.
     * @param camera la cámara que se utiliza para obtener las matrices de proyección y vista.
     * @param spriteSize el tamaño del sprite en la escena del mundo.
     */
    public static void updateInGameLocationMousePosition() {
        //Calculamos las coordenadas normalizadas del cursor en pantalla (de -1 a 1)
        float normalizedX = (2.0f * (float) MouseListener.posX / Window.getWidth()) - 1.0f;
        float normalizedY = 1.0f - (2.0f * (float) MouseListener.posY / Window.getHeight());

        //Vector 4D con las coordenadas normalizadas y profundidad entre -1 y 1
        Vector4f cursorClip = new Vector4f(normalizedX, normalizedY, -1.0f, 1.0f);

        //Calculamos las coordenadas in-game
        Matrix4f inverseTransform = Main.PLAYER.getCamera().getProjectionMatrix().mul(Main.PLAYER.getCamera().getViewMatrix(), new Matrix4f()).invert();
        Vector4f cursorView = inverseTransform.transform(cursorClip, new Vector4f());
        cursorView.div(cursorView.w());
        MouseListener.inGameLocation = new Location(cursorView.x() / WorldScene.SPRITE_SIZE, (cursorView.y() / WorldScene.SPRITE_SIZE));
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

        MouseListener.updateInGameLocationMousePosition();

        MouseListener.isDragging = MouseListener.isMouseButtonPressed[0] || MouseListener.isMouseButtonPressed[1] || MouseListener.isMouseButtonPressed[2];
        if (MouseListener.isDragging) {
            Main.PLAYER.getContainer().onDragEvent((float) posX, (float) posY);
        }

        Window.currentScene.moveMouse((float) posX, (float) posY);
        Main.PLAYER.getContainer().onMouseMoveEvent();

        if (!Main.PLAYER.isMouseOnInventory() && !Main.PLAYER.isPaused()) {
            //Destruir features cuando se mantiene click sobre ellas.
            if (KeyBind.INTERACT.isPressed()) {
                if (Main.PLAYER.isUsingBulldozer()) {
                    Main.PLAYER.destroySelectedFeature();
                } else if (Main.PLAYER.createSelectedTerrain(MouseListener.getInGameLocation()) != null) {
                    Main.PLAYER.addToTerrainSelection(MouseListener.getInGameLocation());
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
            Main.PLAYER.getContainer().onDragEvent((float) posX, (float) posY);

            if (!Main.PLAYER.isMouseOnInventory() && !Main.PLAYER.isPaused()) {
                //Colocar/Destruir features cuando se hace click sobre ellas.
                if (KeyBind.INTERACT.isPressed()) {
                    if (Main.PLAYER.isUsingBulldozer()) {
                        if (Main.PLAYER.destroySelectedFeature()) {
                            return;
                        }
                    } else {
                        if (Main.PLAYER.placeSelectedItem()) {
                            return;
                        }
                    }
                }

                boolean isDuckSelected = false;
                for (Duck duck: Main.world.getDucks()) {
                    if (!isDuckSelected) {
                        isDuckSelected = duck.getBoundingBox().containsLocation(MouseListener.getInGameLocation());
                        duck.setSelected(isDuckSelected);
                    } else {
                        duck.setSelected(false);
                    }
                }
                if (isDuckSelected) {
                    return;
                }
            }
        } else if (action == GLFW.GLFW_RELEASE) {
            MouseListener.setIsMouseButtonPressed(button, false);
            MouseListener.isDragging = false;

            if (Main.PLAYER.getTerrainSelection() != null) {
                Main.PLAYER.placeSelectedItem();
                Main.PLAYER.removeTerrainSelection();
            }
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
        } else {
            if (Window.currentScene instanceof WorldScene && Main.PLAYER.getContainer() instanceof Frame frame) {
                if (offsetY <= -1) {
                    frame.moveScroll((KeyListener.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) ? 2 : 1) * -3.5f);
                } else if (offsetY >= 1) {
                    frame.moveScroll((KeyListener.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) ? 2 : 1) * 3.5f);
                }
            }
        }
    }

    private static void setIsMouseButtonPressed(int button, boolean isPressed) {
        if (button < MouseListener.isMouseButtonPressed.length) {
            MouseListener.isMouseButtonPressed[button] = isPressed;
        }
    }

    /**
     * Verifica si un botón del ratón está presionado.
     *
     * @param button el índice del botón del ratón a verificar.
     * @return true si el botón del ratón especificado está presionado, false en caso contrario.
     */
    public static boolean isMouseButtonPressed(int button) {
        return MouseListener.isMouseButtonPressed[button];
    }

    /**
     * Obtiene la ubicación del cursor del ratón en el juego.
     *
     * @return una copia de la ubicación en el juego del cursor del ratón.
     */
    public static Location getInGameLocation() {
        return MouseListener.inGameLocation.clone();
    }

    /**
     * Obtiene la ubicación del cursor del ratón en la pantalla.
     *
     * @return una nueva instancia de Location con la posición del cursor del ratón en la pantalla.
     */
    public static Location getInScreenLocation() {
        return new Location((float) MouseListener.posX, (float) MouseListener.posY);
    }
}