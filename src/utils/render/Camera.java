package utils.render;

import listener.MouseListener;
import org.joml.*;
import utils.render.scene.WorldScene;
import world.location.Location;

/**
 * Representa la posición y prespectiva ortográfica de la cámara dentro del mundo.
 *
 * @author Izan
 */
public class Camera {
    /**
     * Contantes para la amplitud de la camara.
     */
    private static final int RIGHT_ORTHO_CONST = 80, TOP_ORTHO_CONST = 41;

    /**
     * La matriz de proyección define como se van a mapear los objetos en la pantalla.
     */
    private Matrix4f projectionMatrix = new Matrix4f();

    /**
     * La matriz de vista indica la posición u orientación de la camara respecto a los objetos in-game.
     */
    private Matrix4f viewMatrix = new Matrix4f();

    /**
     * Posición de la cámara en pixeles, no indica la posición in-game.
     */
    private Vector2f cameraPosition;

    /**
     * Zoom de la cámara, por defecto es 1. Cuanto más se acerce a 0 más grande será el zoom.
     */
    private double zoom = 1;

    /**
     * @param cameraPosition Posición inicial de la cámara.
     */
    public Camera(Vector2f cameraPosition) {
        this.cameraPosition = cameraPosition;
        this.updateProjection();
    }

    /**
     * Mueve la cámara y actualiza la posición del ratón in-game.
     * @param movement Unidades que se va a mover la cámara.
     */
    public void moveCamera(Vector2f movement) {
        cameraPosition.add(movement);
        MouseListener.updateInGameLocation();
    }

    /**
     * @return devuelve el zoom de la cámara.
     *
     * @see Camera#zoom
     * @see Camera#zoomIn()
     * @see Camera#zoomOut()
     */
    public double getZoom() {
        return this.zoom;
    }

    /**
     * Actualiza la matriz de proyección de la cámara según el zoom y actualiza la posición del ratón in-game.
     *
     * @see Camera#projectionMatrix
     */
    public void updateProjection() {
        this.projectionMatrix.identity(); //Usamos Matrix4f#identity() para establecer los valores de la matriz como si fuera una matriz unidad.
        this.projectionMatrix.ortho(0f, 16f * (float) this.zoom * Camera.RIGHT_ORTHO_CONST, 0f, 16f * (float) this.zoom * Camera.TOP_ORTHO_CONST, 0f, 100f); //No vamos a poder ver más cerca de 0 unidades (no píxeles) ni más lejos de 100 unidades.
        MouseListener.updateInGameLocation();
    }

    /**
     * Devuelve y actualiza la matriz de vista de la cámara según su posición.
     * @return Matriz de vista de la cámara.
     *
     * @see Camera#viewMatrix
     */
    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0f, 0f, -1f), cameraUp = new Vector3f(0f, 1f, 0f);

        this.viewMatrix.identity();
        this.viewMatrix = this.viewMatrix.lookAt(new Vector3f(this.cameraPosition.x, this.cameraPosition.y, 20f), //Donde está la camara
                cameraFront.add(this.cameraPosition.x, this.cameraPosition.y, 0f), //A donde mira la cámara
                cameraUp);

        return this.viewMatrix;
    }

    /**
     * Convierte las coordenadas de pantalla en coordenadas in-game, según la posición y zoom de la cámara-
     * @param mousePosition Posición del ratón en la pantalla.
     * @return Posición del ratón in-game.
     *
     * @see MouseListener#inGameLocation
     */
    public Location getInGameLocationMousePosition(Vector2f mousePosition) {
        //Calculamos las coordenadas normalizadas del cursor en pantalla (de -1 a 1)
        float normalizedX = (2.0f * mousePosition.x() / Window.getWidth()) - 1.0f;
        float normalizedY = 1.0f - (2.0f * (mousePosition.y()) / Window.getHeight());

        //Vector 4D con las coordenadas normalizadas y profundidad entre -1 y 1
        Vector4f cursorClip = new Vector4f(normalizedX, normalizedY, -1.0f, 1.0f);

        //Calculamos las coordenadas in-game
        Matrix4f inverseTransform = projectionMatrix.mul(viewMatrix, new Matrix4f()).invert();
        Vector4f cursorView = inverseTransform.transform(cursorClip, new Vector4f());
        cursorView.div(cursorView.w);
        return new Location(cursorView.x() / WorldScene.SPRITE_SIZE, cursorView.y() / WorldScene.SPRITE_SIZE);
    }

    /**
     * Aumenta el zoom de la cámara en un 75%.
     *
     * @see Camera#zoom
     */
    public void zoomIn() {
        this.zoom *= 0.75;
        updateProjection();
    }

    /**
     * Disminuye el zoom de la cámara en un 175%.
     *
     * @see Camera#zoom
     */
    public void zoomOut() {
        this.zoom *= 1.75;
        updateProjection();
    }

    /**
     * Devuelve la matriz de proyección de la cámara, pero no la actualiza.
     * @return Matriz de proyección.
     *
     * @see Camera#projectionMatrix
     */
    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    /**
     * @return Posición de la cámara en coordenadas de pantalla.
     */
    public Vector2f getCameraPosition() {
        return cameraPosition;
    }
}
