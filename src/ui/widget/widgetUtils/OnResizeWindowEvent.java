package ui.widget.widgetUtils;

/**
 * Implementa un evento para cuando la ventana cambia de tamaño.
 */
public interface OnResizeWindowEvent {

    /**
     * Método que se llama cada vez que se cambia el tamaño de la ventana.
     * @param newWidth Nuevo ancho de la ventana.
     * @param newHeight Nuevo alto de la ventana.
     */
    void onResizeWindowEvent(float newWidth, float newHeight);
}
