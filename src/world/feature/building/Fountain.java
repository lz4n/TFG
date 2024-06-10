package world.feature.building;

import main.Main;
import org.joml.Vector2f;
import ui.tooltip.TextTooltip;
import ui.tooltip.Tooltip;
import utils.render.GameFont;
import utils.render.scene.WorldScene;
import world.location.Location;

import java.awt.*;

public class Fountain extends Building {
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

        tooltip.addLine(String.format("Coste: %d€", this.getIncomes()), GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);

        return tooltip;
    }
}
