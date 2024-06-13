package game.world.feature.building;

import game.ui.tooltip.TextTooltip;
import game.ui.tooltip.Tooltip;
import game.utils.render.GameFont;
import game.utils.render.scene.WorldScene;
import org.joml.Vector2f;
import game.world.location.Location;

import java.awt.*;

/**
 * Representa una fuente en el mundo del juego.
 * Las fuentes tienen un costo de mantenimiento significativo y se utilizan principalmente para fines decorativos.
 */
public class Fountain extends Building {

    /**
     * Constructor para la clase Fountain.
     *
     * @param location La ubicación de la fuente.
     */
    public Fountain(Location location) {
        super(location, new Vector2f(22, 33).div(WorldScene.SPRITE_SIZE), FeatureType.BUILDING, 15);
    }

    @Override
    public int getIncomes() {
        return -1000;
    }

    @Override
    public Tooltip getTooltip() {
        TextTooltip tooltip = new TextTooltip();

        tooltip.addLine(String.format("Coste: %d§", this.getIncomes()), GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);

        return tooltip;
    }
}
