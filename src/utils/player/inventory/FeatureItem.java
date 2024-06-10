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
    private final FeaturePlacer FEATURE_TO_PLACE;

    /**
     * Representa un ítem que puede colocar una característica (feature) en una ubicación específica del mundo.
     *
     * @param texture        Textura del ítem.
     * @param featureToPlace Tipo de feature que el ítem puede colocar.
     * @param name           Nombre del ítem.
     * @param description    Descripción del ítem.
     * @param price          Precio del ítem.
     */
    public FeatureItem(Texture texture, FeaturePlacer featureToPlace, String name, String description, int price) {
        super(texture, name, description, price);
        this.FEATURE_TO_PLACE = featureToPlace;
    }

    /**
     * Obtiene la feature que este ítem puede colocar en la ubicación especificada.
     *
     * @param location La ubicación donde se desea colocar la feature.
     * @return La instancia de la feature que puede ser colocada por este ítem en la ubicación dada.
     */
    public Feature getFeatureToPlace(Location location) {
        return this.FEATURE_TO_PLACE.apply(location);
    }
}
