package game.ui.widget.widgetUtils;

/**
 * Implementa un evento para cuando la ventana cambia de tamaño.
 */
public interface OnResizeWindowEvent {

    /**
     * Método que se llama cada vez que se cambia el tamaño de la ventana.
     * @param newWidth Nuevo ancho de la ventana en pixeles in-game.
     * @param newHeight Nuevo alto de la ventana en pixeles in-game.
     */
    void onResizeWindowEvent(float newWidth, float newHeight);
}
