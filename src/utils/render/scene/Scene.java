package utils.render.scene;

import javax.swing.plaf.PanelUI;

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

    public abstract void resizeWindow();

    public abstract void click(float mouseX, float mouseY);
}
