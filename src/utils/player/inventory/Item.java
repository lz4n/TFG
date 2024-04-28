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

    private final String NAME, DESCRIPTION;
    private final int PRICE;

    /**
     * @param texture Textura del item.
     */
    public Item(Texture texture, String name, String description, int price) {
        this.TEXTURE = texture;
        this.NAME = name;
        this.DESCRIPTION = description;
        this.PRICE = price;

    }

    /**
     * @return Textura
     *
     */
    public Texture getTexture() {
        return this.TEXTURE;
    }

    public String getName() {
        return NAME;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public int getPrice() {
        return PRICE;
    }
}
