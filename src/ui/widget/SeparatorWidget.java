package ui.widget;

import utils.render.texture.StaticTexture;
import utils.render.texture.Texture;
import utils.render.texture.Textures;

/**
 * Widget que separa dos partes del inventario mediante una barra vertical.
 */
public class SeparatorWidget extends Widget {
    /**
     * @param posX Posición en el eje X del separador, en coordenadas in-game.
     * @param posY Posición en el eje Y del separador, en coordenadas in-game.
     */
    public SeparatorWidget(float posX, float posY) {
        super(posX, posY);
    }

    @Override
    public float getHeight() {
        return 41;
    }

    @Override
    public float getWidth() {
        return 2;
    }

    @Override
    public Texture getTexture() {
        return Textures.SEPARATOR_WIDGET;
    }
}
