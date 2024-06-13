package game.utils.player.ui.widget.widgetUtils;

import game.utils.player.ui.widget.Widget;

/**
 * Implementa un método para gestionar los eventos de clic y hover.
 * @see Widget#setOnClickEvent(WidgetEvent)
 * @see Widget#setOnHoverEvent(WidgetEvent)
 */
public interface WidgetEvent {

    /**
     * Evento que se llama en clic o hover.
     * @see Widget#setOnClickEvent(WidgetEvent)
     * @see Widget#setOnHoverEvent(WidgetEvent)
     */
    void widgetEvent();
}
