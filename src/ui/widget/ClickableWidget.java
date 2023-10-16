package ui.widget;

import org.joml.Vector4f;

public interface ClickableWidget {
    void click();
    Vector4f getClickArea();
}
