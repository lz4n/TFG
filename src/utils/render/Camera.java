package utils.render;

import listener.MouseListener;
import main.Main;
import org.joml.*;
import utils.BoundingBox;
import utils.render.scene.WorldScene;
import world.location.Location;

import java.lang.Math;

/**
 * Representa la posición y prespectiva ortográfica de la cámara dentro del mundo.
 *
 * @author Izan
 */
public class Camera {
    private static final float MIN_ZOOM = 0f, MAX_ZOOM = 2f;

    /**
     * Contantes para la amplitud de la camara.
     */
    private static final int RIGHT_ORTHO_CONST = 80, TOP_ORTHO_CONST = 41;

    private static final BoundingBox CAMERA_AREA = new BoundingBox(0, 0, Main.world.getSize(), Main.world.getSize()).multiply(WorldScene.SPRITE_SIZE);

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
    private float zoom = 1;

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

        Vector2f newPosition = new Vector2f(cameraPosition).add(movement);

        float halfWidth = (WorldScene.SPRITE_SIZE *this.zoom *Camera.RIGHT_ORTHO_CONST) /2;
        float halfHeight = (WorldScene.SPRITE_SIZE *this.zoom *Camera.TOP_ORTHO_CONST) /2;

        float leftBound = Camera.CAMERA_AREA.getXMin() -halfWidth;
        float rightBound = Camera.CAMERA_AREA.getXMax() -halfWidth;
        float bottomBound = Camera.CAMERA_AREA.getYMin() -halfHeight;
        float topBound = Camera.CAMERA_AREA.getXMax() -halfHeight;

        cameraPosition.x = Math.clamp(newPosition.x(), leftBound, rightBound);
        cameraPosition.y = Math.clamp(newPosition.y(), bottomBound, topBound);

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
        this.projectionMatrix.ortho(0f, WorldScene.SPRITE_SIZE * this.zoom * Camera.RIGHT_ORTHO_CONST, 0f, WorldScene.SPRITE_SIZE * this.zoom * Camera.TOP_ORTHO_CONST, 0f, 100f); //No vamos a poder ver más cerca de 0 unidades (no píxeles) ni más lejos de 100 unidades.
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

    private void setZoom(float zoom) {
        this.moveCamera(new Vector2f(0, 0));

        this.zoom = Math.clamp(zoom, Camera.MIN_ZOOM, Camera.MAX_ZOOM);
    }

    /**
     * Aumenta el zoom de la cámara en un 75%.
     *
     * @see Camera#zoom
     */
    public void zoomIn() {
        this.setZoom(this.zoom *.75f);
        updateProjection();
    }

    /**
     * Disminuye el zoom de la cámara en un 175%.
     *
     * @see Camera#zoom
     */
    public void zoomOut() {
        this.setZoom(this.zoom *1.75f);
        updateProjection();
    }

    /**
     * Devuelve la matriz de proyección de la cámara, pero no la actualiza.
     * @return Matriz de proyección.
     *
     * @see Camera#projectionMatrix
     */
    public Matrix4f getProjectionMatrix() {
        return new Matrix4f(this.projectionMatrix);
    }

    /**
     * @return Posición de la cámara en coordenadas de pantalla.
     */
    public Vector2f getCameraPosition() {
        return new Vector2f(cameraPosition);
    }
}
