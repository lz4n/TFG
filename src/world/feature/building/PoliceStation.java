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

public class PoliceStation extends Building {
    public PoliceStation(Location location) {
        super(location, new Vector2f(20, 19).div(WorldScene.SPRITE_SIZE), FeatureType.BUILDING, 14);
    }

    public int getProtection() {
        return 10;
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
        tooltip.addLine(String.format("Coste: %d€", this.getIncomes()), GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);

        return tooltip;
    }
}
