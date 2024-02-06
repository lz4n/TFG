package utils.player.inventory;

import utils.render.texture.Texture;
import world.feature.Feature;

public class FeatureItem extends Item {
    /**
     * Tipo de feature que colocará el jugador cuando seleccione ese slot.
     */
    private final Feature.FeatureType FEATURE_TO_PLACE;

    /**
     * Variante de la feature que colocará el jugador cuando seleccione ese slot.
     */
    private final int VARIANT;


    /**
     * @param texture        Textura del item.
     * @param featureToPlace Tipo de feature del item.
     * @param variant        Variante de la feature del item.
     */
    public FeatureItem(Texture texture, Feature.FeatureType featureToPlace, int variant) {
        super(texture);
        this.FEATURE_TO_PLACE = featureToPlace;
        this.VARIANT = variant;
    }

    public Feature.FeatureType getFeatureToPlace() {
        return FEATURE_TO_PLACE;
    }

    public int getVariantToPlace() {
        return VARIANT;
    }
}
