package game.world.feature.building;

import game.Main;
import game.utils.player.ui.tooltip.TextTooltip;
import game.utils.player.ui.tooltip.Tooltip;
import game.utils.render.GameFont;
import game.utils.render.scene.WorldScene;
import game.world.entity.Duck;
import org.joml.Vector2f;
import game.world.location.Location;
import game.world.particle.SmokeParticle;

import java.awt.*;

/**
 * Representa una casa en el mundo del juego.
 * Cada casa tiene una cantidad de habitantes dependiendo de su nivel.
 */
public class House extends Building {
    protected Duck[] inhabitants;

    /**
     * Constructor para la clase House.
     *
     * @param location La ubicación de la casa.
     * @param variant El nivel de la casa, que determina su tamaño y la cantidad de habitantes.
     */
    public House(Location location, int variant) {
        super(location, House.getSize(variant +1), FeatureType.BUILDING, variant);
        switch (variant) {
            case 0 -> this.inhabitants = new Duck[1];
            case 1 -> this.inhabitants = new Duck[3];
            case 2 -> this.inhabitants = new Duck[6];
            case 3 -> this.inhabitants = new Duck[9];
            case 4 -> this.inhabitants = new Duck[12];
            case 5 -> this.inhabitants = new Duck[15];
            case 6 -> this.inhabitants = new Duck[20];
        }
    }

    private static Vector2f getSize(int level) {
        return switch (level) {
            case 1 -> new Vector2f(15, 11).div(WorldScene.SPRITE_SIZE);
            case 2 -> new Vector2f(17, 17).div(WorldScene.SPRITE_SIZE);
            case 3 -> new Vector2f(17, 17).div(WorldScene.SPRITE_SIZE);
            case 4 -> new Vector2f(21, 18).div(WorldScene.SPRITE_SIZE);
            case 5-> new Vector2f(21, 24).div(WorldScene.SPRITE_SIZE);
            case 6 -> new Vector2f(23, 21).div(WorldScene.SPRITE_SIZE);
            case 7 -> new Vector2f(25, 22).div(WorldScene.SPRITE_SIZE);
            default -> new Vector2f(0, 0);
        };
    }

    @Override
    public void onAdd() {
        for (int index = 0; index < this.inhabitants.length; index++) {
            Duck duck = new Duck(this.getLocation(), this);
            this.inhabitants[index] = duck;

            Main.world.addDuck(duck);
        }
        Main.world.addPopulation(this.inhabitants.length);
    }

    @Override
    public void onRemove() {
        super.onRemove();

        for (Duck duck: this.inhabitants) {
            duck.removeJobSite();
            Main.world.removeDuck(duck);
        }

        Main.world.addPopulation(this.inhabitants.length *-1);
    }

    @Override
    public int getIncomes() {
        return 0;
    }

    @Override
    public void onTick() {
        if (this.getVariant() +1 >= 3 && this.getVariant() +1 <= 5 && Main.RANDOM.nextFloat() > 0.97) {
            Main.world.addParticle(new SmokeParticle(this.getLocation().add(0.4f, 0.8f)));
        }
    }

    @Override
    public Tooltip getTooltip() {
        TextTooltip tooltip = new TextTooltip();

        tooltip.addLine(String.format("Capacidad: %d patos", this.inhabitants.length), GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);

        return tooltip;
    }
}
