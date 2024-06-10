package utils.player.inventory;

import utils.Logger;
import utils.render.texture.Texture;
import world.feature.Feature;
import world.location.Location;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Representa un item que sirve para colocar una feature.
 */
public class FeatureItem extends Item {
    /**
     * Tipo de feature que colocará el jugador cuando seleccione ese slot.
     */
    private final FeaturePlacer FEATURE_TO_PLACE;



    /**
     * @param texture        Textura del item.
     * @param featureToPlace Tipo de feature del item.
     * @param variant        Variante de la feature del item.
     */
    public FeatureItem(Texture texture, FeaturePlacer featureToPlace, String name, String description, int price) {
        super(texture, name, description, price);
        this.FEATURE_TO_PLACE = featureToPlace;
    }

    public Feature getFeatureToPlace(Location location) {
        return this.FEATURE_TO_PLACE.apply(location);
    }
}
