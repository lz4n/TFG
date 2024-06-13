package game.utils.player.ui.widget.widgetUtils;

/**
 * Interfaz funcional para manejar eventos de arrastre del ratón.
 */
public interface OnMouseDragEvent {
    /**
     * Método invocado cuando ocurre un evento de arrastre del ratón.
     *
     * @param mouseX Coordenada X del ratón durante el evento de arrastre.
     * @param mouseY Coordenada Y del ratón durante el evento de arrastre.
     */
    void onMouseDragEvent(float mouseX, float mouseY);
}
