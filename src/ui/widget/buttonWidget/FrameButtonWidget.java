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

public class FrameButtonWidget extends AbstractButtonWidget implements OnResizeWindowEvent {
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(91, 16);
    private final static int UNIT_TEXT_SIZE = 8;

    private final String TEXT;
    private Graphics2dTexture textTexture;

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
        if (this.textTexture != null) {
            this.textTexture.remove();
        }
        this.textTexture = GameFont.PIXEL.drawText(this.TEXT, Font.PLAIN, FrameButtonWidget.UNIT_TEXT_SIZE *(int) Container.pixelSizeInScreen);
    }
}
