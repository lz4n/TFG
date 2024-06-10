package world.feature.building.jobSite;

import org.joml.Vector2f;
import org.joml.Vector2i;
import ui.tooltip.TextTooltip;
import ui.tooltip.Tooltip;
import utils.render.GameFont;
import utils.render.scene.WorldScene;
import world.entity.Duck;
import world.location.Location;

import java.awt.*;

/**
 * Representa una industria en el mundo del juego.
 */
public class Industry extends JobSite {

    /**
     * Constructor para la clase Industry.
     *
     * @param location La ubicación de la industria.
     * @param variant La variante específica de la industria.
     */
    public Industry(Location location, int variant) {
        super(location, Industry.getSize(variant), FeatureType.BUILDING, variant +7);
        switch (variant) {
            case 1 -> this.workers = new Duck[6];
            case 2 -> this.workers = new Duck[9];
            case 3 -> this.workers = new Duck[12];
            case 4 -> this.workers = new Duck[15];
            case 5 -> this.workers = new Duck[21];
        }
    }

    /**
     * Obtiene el tamaño de la industria según la variante.
     *
     * @param level El nivel de la variante.
     * @return Un Vector2f representando el tamaño de la industria.
     */
    private static Vector2f getSize(int level) {
        return switch (level) {
            case 1, 2, 3 -> new Vector2f(16, 16).div(WorldScene.SPRITE_SIZE);
            case 4 -> new Vector2f(24, 24).div(WorldScene.SPRITE_SIZE);
            case 5-> new Vector2f(22, 22).div(WorldScene.SPRITE_SIZE);
            default -> new Vector2f(0, 0);
        };
    }

    /**
     * Obtiene los ingresos por cada pato trabajador según la variante.
     *
     * @return Los ingresos por pato trabajador.
     */
    private int getIncomesPerDuck() {
        switch (this.getVariant() -7) {
            case 1 -> {
                return 25;
            }
            case 2 -> {
                return 30;
            }
            case 3 -> {
                return 35;
            }
            case 4 -> {
                return 40;
            }
            case 5 -> {
                return 45;
            }
        }

        return 0;
    }

    @Override
    public int getCost() {
        switch (this.getVariant() -7) {
            case 1 -> {
                return -40;
            }
            case 2 -> {
                return -50;
            }
            case 3 -> {
                return -80;
            }
            case 4 -> {
                return -110;
            }
            case 5 -> {
                return -140;
            }
        }

        return 0;
    }

    @Override
    public int getIncomes() {
        int incomes = this.getCost();

        for (Duck duck: this.workers) {
            if (duck != null) {
                incomes += this.getIncomesPerDuck();
            }
        }

        return incomes;
    }
}
