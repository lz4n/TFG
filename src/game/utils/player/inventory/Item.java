package game.utils.player.inventory;

import game.utils.render.texture.Texture;

/**
 * Representa un item en el inventario, que se asignará a un slot durante la creación del inventario.
 */
public class Item {
    private final Texture TEXTURE;
    private final String NAME, DESCRIPTION;
    private final int PRICE;

    /**
     * Representa un ítem con una textura, nombre, descripción y precio.
     *
     * @param texture     La textura del ítem.
     * @param name        El nombre del ítem.
     * @param description La descripción del ítem.
     * @param price       El precio del ítem.
     */
    public Item(Texture texture, String name, String description, int price) {
        this.TEXTURE = texture;
        this.NAME = name;
        this.DESCRIPTION = description;
        this.PRICE = price;

    }

    /**
     * Retorna la textura de este ítem.
     *
     * @return La textura del ítem.
     */
    public Texture getTexture() {
        return this.TEXTURE;
    }

    /**
     * Retorna el nombre de este ítem.
     *
     * @return El nombre del ítem.
     */
    public String getName() {
        return NAME;
    }

    /**
     * Retorna la descripción de este ítem.
     *
     * @return La descripción del ítem.
     */
    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * Retorna el precio de este ítem.
     *
     * @return El precio del ítem.
     */
    public int getPrice() {
        return PRICE;
    }
}
