package ui.widget;

import org.joml.Vector4f;
import utils.BoundingBox;

/**
 * Representa la funcionalidad de un Widget que se puede clickar.
 */
public interface ClickableWidget {

    /**
     * Método que se llama cada vez que se hace clic.
     */
    void click();

    BoundingBox getBoundingBox();
}
