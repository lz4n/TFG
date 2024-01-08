package ui.container;

import ui.widget.Widget;
import ui.widget.widgetUtils.OnResizeWindowEvent;
import utils.render.mesh.Mesh;

import java.util.LinkedList;
import java.util.List;

public abstract class Container {
    /**
     * Píxeles de patalla que ocupa un pixel de la interfaz in-game.
     */
    public static float pixelSizeInScreen;

    protected List<Widget> widgets = new LinkedList<>();

    public abstract void draw(Mesh mesh);

    public abstract boolean onMouseMoveEvent(float mouseX, float mouseY);

    public void onHoverEvent() {
        this.widgets.stream()
                .filter(Widget::isHovered)
                .forEach(Widget::onHoverEvent);
    }

    public void onClickEvent(float mouseX, float mouseY) {
        new LinkedList<>(this.widgets).stream()
                .filter(Widget::isHovered)
                .forEach(Widget::onClickEvent);
    }

    public void onResizeWindowEvent(float newWidth, float newHeight) {
        Container.pixelSizeInScreen = newHeight / 250f;

        this.widgets.stream()
                .filter(widget -> widget instanceof OnResizeWindowEvent)
                .forEach(widget -> ((OnResizeWindowEvent) widget).onResizeWindowEvent(newWidth /Container.pixelSizeInScreen, newHeight /Container.pixelSizeInScreen));
    }
}
