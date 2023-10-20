package utils.render.scene;

/**
 * La escena es la encargada de la parte gráfica del juego, cada pantalla tiene una escena.
 *
 * @author Izan
 */
public abstract class Scene {
    /**
     * Se llama cuando se inicia la escena, justo antes de aparecer en pantalla. No cuando se instancia.
     */
    public abstract void init();


    /**
     * Se llama en cada frame.
     * @param dTime Tiempo que ha pasado desde el anterior frame.
     */
    public abstract void update(long dTime);

    /**
     * Método que se llama cuando se cámbia el tamaño de la ventana.
     */
    public abstract void resizeWindow();

    /**
     * Método que se llama cuando se hace click en la ventana.
     * @param mouseX Coordenadas del ratón en el eje X, en unidades de pantalla.
     * @param mouseY Coordenadas del ratón en el eje Y, en unidades de pantalla.
     */
    public abstract void click(float mouseX, float mouseY);
}
