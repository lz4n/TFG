package ui.widget.widgetUtils;

/**
 * Interfaz que represeneta los Widgets con un render extra (ademása del render del propio Widget), por ejemplo,
 * el contenido de un botón, o el texto de un TextField.
 */
public interface CustomDrawWidget {

    /**
     * Dibuja el render especial del Widget.
     * @param pixelSizeInScreen Tamaño de un pixel de la interfaz in-game en píxeles de pantalla.
     * @param posX Posición del widget en el eje X, en coordenadas de pantalla.
     * @param posY Posición del widget en el eje Y, en coordenadas de pantalla.
     */
    void draw(float pixelSizeInScreen, float posX, float posY);
}
