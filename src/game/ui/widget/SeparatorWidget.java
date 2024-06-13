package game.ui.widget;

import game.ui.container.Inventory;
import game.utils.BoundingBox;
import game.utils.render.texture.Texture;
import game.utils.render.texture.Textures;

/**
 * Widget que separa dos partes del inventario mediante una barra vertical.
 */
public class SeparatorWidget extends Widget {
    private final static BoundingBox BASE_BOUNDING_BOX = new BoundingBox(2, 41);

    /**
     * @param posX Posición en el eje X, en coordenadas in-game.
     * @param posY Posición en el eje Y, en coordendas in-game.
     * @see Inventory
     */
    public SeparatorWidget(float posX, float posY) {
        super(posX, posY, SeparatorWidget.BASE_BOUNDING_BOX);
    }

    @Override
    public Texture getTexture() {
        return Textures.SEPARATOR_WIDGET;
    }
}
