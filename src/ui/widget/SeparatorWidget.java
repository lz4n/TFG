package ui.widget;

import utils.BoundingBox;
import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

/**
 * Widget que separa dos partes del inventario mediante una barra vertical.
 */
public class SeparatorWidget extends Widget {
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(2, 41);

    public SeparatorWidget(float posX, float posY) {
        super(posX, posY, SeparatorWidget.BASE_BOUNDING_BOX);
    }

    @Override
    public Texture getTexture() {
        return Textures.SEPARATOR_WIDGET;
    }
}
