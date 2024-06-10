package ui.container;

import main.Main;
import ui.widget.Widget;
import ui.widget.widgetUtils.OnMouseDragEvent;
import ui.widget.widgetUtils.OnMouseMoveEvent;
import ui.widget.widgetUtils.OnResizeWindowEvent;

import java.util.LinkedList;
import java.util.List;

/**
 * Representa un contenedor que contiene widgets. Direcciona los eventos de clic, hover y resize a los diferentes widgets.
 */
public abstract class Container {

    /**
     * Píxeles de patalla que ocupa un pixel de la interfaz in-game. Equivalencia entre pixeles de pantalla y pixeles in-game.
     */
    public static float pixelSizeInScreen;

    /**
     * Lista que  contiene todos los widgets que se están mostrando actualmente..
     */
    protected List<Widget> widgets = new LinkedList<>();

    /**
     * Dibuja el contenedor y los widgets.
     * @see Container#widgets
     */
    public abstract void draw();

    /**
     * Evento que se llama cuando se mueve el ratón. Si el ratón se mueve a un widget este pasará a activar la bandera <i>hovered</i>,
     * si el ratón deja de estar sobre el widget el valor de la bandera pasa a false.
     * @param mouseX posición en el eje X del ratón.
     * @param mouseY posición en el eje Y del ratón.
     * @return Si el ratón está sobre el contenedor o alguno de los widets.
     * @see Widget#isHovered()
     */
    public abstract boolean onMouseMoveEvent(float mouseX, float mouseY);

    /**
     * Evento que se llama cuando se llama en cada bucle de renderizado. Redirecciona el evento de hover (pasar el ratón por encima) a los widgets.
     */
    public void onHoverEvent() {
        Main.PLAYER.setTooltip(null);
        //Si la bandera hovered está activa es que el ratón está sobre ese widget.
        for (Widget widget : this.widgets) {
            if (widget.isHovered()) {
                widget.onHoverEvent();

                if (widget.getTooltip() != null) {
                    Main.PLAYER.setTooltip(widget.getTooltip());
                }
            }
        }
    }

    /**
     * Evento que se llama cuando se hace clic. Redirecciona el evento de clic a los widgets.
     * @param mouseX Posición en el eje X en el que se ha hecho clic.
     * @param mouseY Posición en el eje Y en elq ue se ha hecho clic.
     */
    public void onClickEvent(float mouseX, float mouseY) {
        //Si la bandera hovered está activa es que el ratón está sobre ese widget.
        for (Widget widget : new LinkedList<>(this.widgets)) {
            if (widget.isHovered()) {
                widget.onMouseClickEvent();
            }
        }
    }

    /**
     * Evento que se llama cuando se cambia el tamaño de la ventana. Recalcula el tamño de los pixeles y redirecciona el evento a los widgets
     * que implementen la interfaz <i>OnResizeWindowEvent</i>.
     * @param newWidth Nuevo ancho de la ventana.
     * @param newHeight Nuevo alto de la ventana.
     * @see OnResizeWindowEvent
     */
    public void onResizeWindowEvent(float newWidth, float newHeight) {
        Container.pixelSizeInScreen = newHeight / 250f;

        for (Widget widget : this.widgets) {
            if (widget instanceof OnResizeWindowEvent onResizeWindowEvent) {
                onResizeWindowEvent.onResizeWindowEvent(newWidth / Container.pixelSizeInScreen, newHeight / Container.pixelSizeInScreen);
            }
        }
    }

    public void onDragEvent(float mouseX, float mouseY) {
        for (Widget widget : this.widgets) {
            if (widget instanceof OnMouseDragEvent onMouseDragEvent) {
                onMouseDragEvent.onMouseDragEvent(mouseX / Container.pixelSizeInScreen, mouseY / Container.pixelSizeInScreen);
            }
        }
    }

    public void onMouseMoveEvent() {
        for (Widget widget : this.widgets) {
            if (widget instanceof OnMouseMoveEvent onMouseMoveEvent) {
                onMouseMoveEvent.onMouseMoveEvent();
            }
        }
    }
}
