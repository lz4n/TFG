package world.feature.building.jobSite;

import org.joml.Vector2f;
import org.joml.Vector2i;
import ui.tooltip.TextTooltip;
import ui.tooltip.Tooltip;
import utils.render.GameFont;
import world.entity.Duck;
import world.feature.building.Building;
import world.location.Location;

import java.awt.*;

public abstract class JobSite extends Building {
    protected Duck[] workers;

    public JobSite(Location location, Vector2f sizeInBlocks, FeatureType featureType, int variant) {
        super(location, sizeInBlocks, featureType, variant);
    }

    public boolean addWorker(Duck duck) {
        for (int index = 0; index < this.workers.length; index++) {
            if (this.workers[index] == null) {
                this.workers[index] = duck;
                return true;
            }
        }

        return false;
    }

    private int getWorkersCount() {
        int count = 0;

        for (Duck duck: this.workers) {
            if (duck != null) {
                count++;
            }
        }

        return count;
    }

    @Override
    public void onRemove() {
        super.onRemove();

        for (Duck duck: this.workers) {
            if (duck != null) {
                duck.removeJobSite();
            }
        }
    }

    public void removeWorker(Duck duck) {
        for (int index = 0; index < this.workers.length; index++) {
            if (this.workers[index] == duck) {
                this.workers[index] = null;
                return;
            }
        }
    }

    @Override
    public Tooltip getTooltip() {
        TextTooltip tooltip = new TextTooltip();

        tooltip.addLine(String.format("Trabajadores: %d/%d patos", this.getWorkersCount(), this.workers.length), GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);
        tooltip.addLine("", GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);
        tooltip.addLine(String.format("Coste: %d€", this.getCost()), GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);
        tooltip.addLine(String.format("Ingresos: %d€", this.getIncomes()), GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);

        return tooltip;
    }

    public abstract int getCost();
}
