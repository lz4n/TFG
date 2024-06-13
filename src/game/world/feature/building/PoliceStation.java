package game.world.feature.building;

import game.Main;
import game.utils.player.ui.tooltip.Tooltip;
import org.joml.Vector2f;
import game.utils.player.ui.tooltip.TextTooltip;
import game.utils.render.GameFont;
import game.utils.render.scene.WorldScene;
import game.world.location.Location;

import java.awt.*;

/**
 * Representa una estación de policía en el mundo del juego.
 * Proporciona seguridad a las áreas circundantes y tiene un costo de mantenimiento.
 */
public class PoliceStation extends Building {

    /**
     * Constructor para la clase PoliceStation.
     *
     * @param location La ubicación de la estación de policía.
     */
    public PoliceStation(Location location) {
        super(location, new Vector2f(20, 19).div(WorldScene.SPRITE_SIZE), FeatureType.BUILDING, 14);
    }

    /**
     * Obtiene la cantidad de protección que proporciona la estación de policía.
     *
     * @return La cantidad de protección.
     */
    public int getProtection() {
        return 40;
    }

    @Override
    public void onAdd() {
        super.onAdd();

        Main.world.updateSecurity();
    }

    @Override
    public void onRemove() {
        super.onRemove();

        Main.world.updateSecurity();
    }

    @Override
    public int getIncomes() {
        return -100;
    }

    @Override
    public Tooltip getTooltip() {
        TextTooltip tooltip = new TextTooltip();

        tooltip.addLine(String.format("Capacidad: %d patos", this.getProtection()), GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);
        tooltip.addLine(String.format("Coste: %d§", this.getIncomes()), GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);

        return tooltip;
    }
}
