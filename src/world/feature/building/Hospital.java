package world.feature.building;

import main.Main;
import org.joml.Vector2f;
import org.joml.Vector2i;
import ui.tooltip.TextTooltip;
import ui.tooltip.Tooltip;
import utils.render.GameFont;
import utils.render.scene.WorldScene;
import world.location.Location;

import java.awt.*;

/**
 * Representa un hospital en el mundo del juego.
 * Proporciona beneficios de salud a las áreas circundantes y tiene un costo de mantenimiento.
 */
public class Hospital extends Building {

    /**
     * Constructor para la clase Hospital.
     *
     * @param location La ubicación del hospital.
     */
    public Hospital(Location location) {
        super(location, new Vector2f(24, 24).div(WorldScene.SPRITE_SIZE), FeatureType.BUILDING, 13);
    }

    /**
     * Obtiene la cantidad de protección que proporciona el hospital.
     *
     * @return La cantidad de protección.
     */
    public int getProtection() {
        return 25;
    }

    @Override
    public void onAdd() {
        super.onAdd();

        Main.world.updateHealth();
    }

    @Override
    public void onRemove() {
        super.onRemove();

        Main.world.updateHealth();
    }

    @Override
    public int getIncomes() {
        return -200;
    }

    @Override
    public Tooltip getTooltip() {
        TextTooltip tooltip = new TextTooltip();

        tooltip.addLine(String.format("Capacidad: %d patos", this.getProtection()), GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);
        tooltip.addLine(String.format("Coste: %d€", this.getIncomes()), GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);

        return tooltip;
    }
}
