package ui.widget.widgetUtils;

import utils.render.mesh.Mesh;

/**
 * Interfaz que represeneta los Widgets con un render extra (ademása del render del propio Widget), por ejemplo,
 * el contenido de un botón, o el texto de un TextField.
 */
public interface CustomDrawWidget {

    /**
     * Dibuja el render especial del Widget.
     * @param mesh <code>Mesh</code> que se utiliza para dibujar el widget.
     * @param pixelSizeInScreen Tamaño de un pixel de la interfaz in-game en píxeles de pantalla.
     * @param posX Posición del widget en el eje X, en coordenadas de pantalla.
     * @param posY Posición del widget en el eje Y, en coordenadas de pantalla.
     * @param width Ancho del widget en coordenadas de pantalla.
     * @param height Alto del widget en coordenadas de pantalla.
     */
    void draw(float pixelSizeInScreen, float posX, float posY);
}
