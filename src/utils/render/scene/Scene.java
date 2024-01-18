package utils.render.scene;

/**
 * La escena es la encargada de la parte gráfica del juego, cada pantalla tiene una escena.
 *
 * @author Izan
 */
public interface Scene {
    /**
     * Se llama cuando se inicia la escena, justo antes de aparecer en pantalla. No cuando se instancia.
     */
    void init();


    /**
     * Se llama en cada frame.
     * @param dTime Tiempo que ha pasado desde el anterior frame.
     */
    void update(long dTime);

    /**
     * Método que se llama cuando se cámbia el tamaño de la ventana.
     */
    void resizeWindow();

    /**
     * Método que se llama cuando se hace click en la ventana.
     * @param mouseX Coordenadas del ratón en el eje X, en unidades de pantalla.
     * @param mouseY Coordenadas del ratón en el eje Y, en unidades de pantalla.
     */
    void click(float mouseX, float mouseY);
    void moveMouse(float mouseX, float moseY);
}
