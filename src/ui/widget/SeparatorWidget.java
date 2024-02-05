package ui.widget;

import utils.BoundingBox;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

/**
 * Widget que separa dos partes del inventario mediante una barra vertical.
 */
public class SeparatorWidget extends Widget {
    /**
     * BoundingBox base del widget, cuando se instancia se duplica y se mueve a la posición del widget.
     */
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(2, 41);

    /**
     * @param posX Posición en el eje X, en coordenadas in-game.
     * @param posY Posición en el eje Y, en coordendas in-game.
     * @see ui.container.Inventory
     */
    public SeparatorWidget(float posX, float posY) {
        super(posX, posY, SeparatorWidget.BASE_BOUNDING_BOX);
    }

    @Override
    public Texture getTexture() {
        return Textures.SEPARATOR_WIDGET;
    }
}
