package ui.widget.buttonWidget;

import org.joml.Vector2f;
import ui.container.Container;
import ui.widget.widgetUtils.OnResizeWindowEvent;
import utils.BoundingBox;
import utils.render.GameFont;
import utils.render.texture.Graphics2dTexture;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

import java.awt.*;

/**
 * Botón con todas las funcionalidades de AbstractButtonWidget, cuyo contenido es una cadena de texto.
 * @see AbstractButtonWidget
 */
public class FrameButtonWidget extends AbstractButtonWidget implements OnResizeWindowEvent {
    /**
     * BoundingBox base del widget, cuando se instancia se duplica y se mueve a la posición del widget.
     */
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(84, 16);

    /**
     * Tamaño del texto por pixel in-game.
     */
    private final static int UNIT_TEXT_SIZE = 8;

    /**
     * Texto del botón.
     */
    private final String TEXT;

    /**
     * Textura que contiene el texto del botón. Cada vez que se cambie el tamaño del botón, debe cambiar el tamaño del texto,
     * y se recalcula la textura.
     */
    private Graphics2dTexture textTexture;

    /**
     * @param posX Posición en el eje X.
     * @param posY Posición en el eje Y.
     * @param text Texto del botón.
     */
    public FrameButtonWidget(float posX, float posY, String text) {
        super(posX, posY, Textures.FRAME_BUTTON, Textures.CLICKED_FRAME_BUTTON, new Vector2f(0, -1), FrameButtonWidget.BASE_BOUNDING_BOX);
        this.TEXT = text;
    }

    @Override
    public Texture getTexture() {
        return this.clickTime > -1 ? Textures.CLICKED_FRAME_BUTTON : (this.isHovered() ? Textures.HOVERED_FRAME_BUTTON : Textures.FRAME_BUTTON);
    }

    @Override
    public Texture getIcon() {
        return this.textTexture;
    }

    @Override
    public void onResizeWindowEvent(float newWidth, float newHeight) {
        //Recalculamos la textura del texto (si ya había una textura la borramos, para evitar una fuga de memoria).
        if (this.textTexture != null) {
            this.textTexture.remove();
        }
        this.textTexture = GameFont.PIXEL.drawText(this.TEXT, Font.PLAIN, FrameButtonWidget.UNIT_TEXT_SIZE *(int) Container.pixelSizeInScreen);
    }
}
