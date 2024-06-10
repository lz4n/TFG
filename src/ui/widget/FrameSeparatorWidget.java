package ui.widget;

import utils.BoundingBox;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

/**
 * Widget que separa dos partes de un frame mediante una barra horizontal.
 */
public class FrameSeparatorWidget extends Widget {
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(93, 3);

    /**
     * @param posX Posición en el eje X, en coordenadas in-game.
     * @param posY Posición en el eje Y, en coordendas in-game.
     * @see ui.container.Inventory
     */
    public FrameSeparatorWidget(float posX, float posY) {
        super(posX, posY, FrameSeparatorWidget.BASE_BOUNDING_BOX);
    }

    @Override
    public Texture getTexture() {
        return Textures.FRAME_SEPARATOR_WIDGET;
    }
}
