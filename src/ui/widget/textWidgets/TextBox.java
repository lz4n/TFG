package ui.widget.textWidgets;

import utils.BoundingBox;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

public class TextBox extends AbstractTextBox {
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(36, 16);
    private final static int TEXT_SIZE = 40;

    public TextBox(float posX, float posY, String text, int style) {
        super(posX, posY, TextBox.BASE_BOUNDING_BOX, text, style);
    }

    @Override
    public Texture getTexture() {
        return Textures.TEXT_BOX;
    }

    @Override
    public int getTextSize() {
        return TextBox.TEXT_SIZE;
    }
}
