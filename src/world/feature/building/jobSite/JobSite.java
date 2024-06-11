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

/**
 * Representa un sitio de trabajo en el mundo del juego, donde los patos pueden ser empleados.
 */
public abstract class JobSite extends Building {
    protected Duck[] workers;

    /**
     * Constructor para la clase JobSite.
     *
     * @param location La ubicación del sitio de trabajo.
     * @param sizeInBlocks El tamaño del sitio de trabajo en bloques del mundo del juego.
     * @param featureType El tipo de característica del sitio de trabajo.
     * @param variant La variante específica del sitio de trabajo.
     */
    public JobSite(Location location, Vector2f sizeInBlocks, FeatureType featureType, int variant) {
        super(location, sizeInBlocks, featureType, variant);
    }

    /**
     * Añade un trabajador (pato) al sitio de trabajo.
     *
     * @param duck El pato a añadir.
     * @return true si el trabajador fue añadido exitosamente, false si no hay espacio.
     */
    public boolean addWorker(Duck duck) {
        for (int index = 0; index < this.workers.length; index++) {
            if (this.workers[index] == null) {
                this.workers[index] = duck;
                return true;
            }
        }

        return false;
    }

    /**
     * Obtiene la cantidad de trabajadores actuales en el sitio de trabajo.
     *
     * @return La cantidad de trabajadores.
     */
    private int getWorkersCount() {
        int count = 0;

        for (Duck duck: this.workers) {
            if (duck != null) {
                count++;
            }
        }

        return count;
    }

    /**
     * Elimina un trabajador específico del sitio de trabajo.
     *
     * @param duck El pato a eliminar.
     */
    public void removeWorker(Duck duck) {
        for (int index = 0; index < this.workers.length; index++) {
            if (this.workers[index] == duck) {
                this.workers[index] = null;
                return;
            }
        }
    }

    /**
     * Método abstracto que debe ser implementado por las subclases para definir el costo del sitio de trabajo.
     *
     * @return El costo del sitio de trabajo.
     */
    public abstract int getCost();

    @Override
    public void onRemove() {
        super.onRemove();

        for (Duck duck: this.workers) {
            if (duck != null) {
                duck.removeJobSite();
            }
        }
    }

    @Override
    public Tooltip getTooltip() {
        TextTooltip tooltip = new TextTooltip();

        tooltip.addLine(String.format("Trabajadores: %d/%d patos", this.getWorkersCount(), this.workers.length), GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);
        tooltip.addLine("", GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);
        tooltip.addLine(String.format("Coste: %d§", this.getCost()), GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);
        tooltip.addLine(String.format("Ingresos: %d§", this.getIncomes()), GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);

        return tooltip;
    }
}
