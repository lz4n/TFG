package utils.player.inventory;

import utils.render.texture.Texture;

/**
 * Representa un item en el inventario, que se asignará a un slot durante la creación del inventario.
 */
public class Item {

    /**
     * Textura del item.
     */
    private final Texture TEXTURE;

    /**
     * @param texture Textura del item.
     */
    public Item(Texture texture) {
        this.TEXTURE = texture;
    }

    public Texture getTexture() {
        return this.TEXTURE;
    }
}
