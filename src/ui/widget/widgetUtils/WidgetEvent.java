package ui.widget.widgetUtils;

/**
 * Implementa un método para gestionar los eventos de clic y hover.
 * @see ui.widget.Widget#setOnClickEvent(WidgetEvent)
 * @see ui.widget.Widget#setOnHoverEvent(WidgetEvent)
 */
public interface WidgetEvent {

    /**
     * Evento que se llama en clic o hover.
     * @see ui.widget.Widget#setOnClickEvent(WidgetEvent)
     * @see ui.widget.Widget#setOnHoverEvent(WidgetEvent)
     */
    void widgetEvent();
}
