package ui.widget.textWidgets;

import utils.BoundingBox;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

public class SmallTextBox extends AbstractTextBox {
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(36, 6);
    private final static int TEXT_SIZE = 15;

    public SmallTextBox(float posX, float posY, String text, int style) {
        super(posX, posY, SmallTextBox.BASE_BOUNDING_BOX, text, style);
    }

    @Override
    public Texture getTexture() {
        return Textures.SMALL_TEXT_BOX;
    }

    @Override
    public int getTextSize() {
        return SmallTextBox.TEXT_SIZE;
    }
}
