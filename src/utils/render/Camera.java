package utils.render;

import listener.MouseListener;
import org.joml.*;
import utils.render.scene.WorldScene;

public class Camera {
    private static final int RIGHT_ORTHO_CONST = 80, TOP_ORTHO_CONST = 41;

    private Matrix4f projectionMatrix = new Matrix4f(), viewMatrix = new Matrix4f();
    public Vector2f cameraPosition;
    private double zoom = 1;

    public Camera(Vector2f cameraPosition) {
        this.cameraPosition = cameraPosition;
        this.updateProjection();
    }

    public void updateProjection() {
        this.projectionMatrix.identity(); //Usamos Matrix4f#identity() para establecer los valores de la matriz como si fuera una matriz unidad.
        this.projectionMatrix.ortho(0f, 16f * (float) this.zoom * Camera.RIGHT_ORTHO_CONST, 0f, 16f * (float) this.zoom * Camera.TOP_ORTHO_CONST, 0f, 100f); //No vamos a poder ver más cerca de 0 unidades (no píxeles) ni más lejos de 100 unidades.
        MouseListener.updateInGameLocation();
    }

    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0f, 0f, -1f), cameraUp = new Vector3f(0f, 1f, 0f);

        this.viewMatrix.identity();
        this.viewMatrix = this.viewMatrix.lookAt(new Vector3f(this.cameraPosition.x, this.cameraPosition.y, 20f), //Donde está la camara
                cameraFront.add(this.cameraPosition.x, this.cameraPosition.y, 0f), //A donde mira la cámara
                cameraUp);

        return this.viewMatrix;
    }

    public Vector2f getInGameLocationMousePosition(Vector2f mousePosition) {
        //Calculamos las coordenadas normalizadas del cursor en pantalla (de -1 a 1)
        float normalizedX = (2.0f * mousePosition.x() / Window.getWidth()) - 1.0f;
        float normalizedY = 1.0f - (2.0f * (mousePosition.y() + 48) / Window.getHeight());

        //Vector 4D con las coordenadas normalizadas y profundidad entre -1 y 1
        Vector4f cursorClip = new Vector4f(normalizedX, normalizedY, -1.0f, 1.0f);

        //Calculamos las coordenadas in-game
        Matrix4f inverseTransform = projectionMatrix.mul(viewMatrix, new Matrix4f()).invert();
        Vector4f cursorView = inverseTransform.transform(cursorClip, new Vector4f());
        cursorView.div(cursorView.w);
        return new Vector2f(cursorView.x() / WorldScene.SPRITE_SIZE, cursorView.y() / WorldScene.SPRITE_SIZE);
    }

    public void zoomIn() {
        this.zoom *= 0.75;
        updateProjection();
    }

    public void zoomOut() {
        this.zoom *= 1.75;
        updateProjection();
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }
}
