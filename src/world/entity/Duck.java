package world.entity;

import main.Main;
import npc.Pathfinding;
import org.joml.Vector2f;
import ui.tooltip.TextTooltip;
import ui.tooltip.Tooltip;
import utils.BoundingBox;
import utils.render.GameFont;
import utils.render.scene.WorldScene;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.World;
import world.feature.building.Building;
import world.feature.building.House;
import world.feature.building.jobSite.JobSite;
import world.location.Location;
import world.particle.SplashParticle;
import world.terrain.Terrain;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

/**
 * Las entidades son elementos móviles del juego con los que el jugador puede interactuar (en jugador no puede interactuar
 * con las partículas).
 * @see world.particle.Particle
 */

//TODO: Sistema para detectar clic a las entidades.
public class Duck implements Serializable {
    private static final float TAX_BASE = 50;

    public static final float NO_FLIP = 0, FLIP_HORIZONTAL = 1, FLIP_VERTICAL = 2, FLIP_BOTH = 3;

    private static final int SQUAWKING_TIME = 50;
    /*{"8:30", "17:30", "22:00"},
          {"15:20", "21:20", "05:00"},
          {"23:40", "06:30", "12:20"}*/
    private final static int[][] SCHEDULE = new int[][]{ //Hará referencia a los horarios de los patos. HORA ENTRADA TRABAJO || HORA SALIDA TRABAJO || HORA IRSE A DORMIR
             {World.hourToTick(8, 30), World.hourToTick(17, 30), World.hourToTick(22, 0)},
             {World.hourToTick(15, 20), World.hourToTick(21,20), World.hourToTick(5, 0)},
             {World.hourToTick(0, 40), World.hourToTick(6, 30), World.hourToTick(12, 20)}
    };

    /**
     * Posición donde se encuentra la entidad.
     */
    private Location location;
    private float flip = Duck.NO_FLIP;
    private List<Location> pathfinding;
    private int pathIndex = 0;
    private House house;
    private JobSite jobSite = null;
    private ActivityType state = ActivityType.IN_HOUSE;
    private int squawkingTime = 0;
    private int scheduleIndex;
    private Animation currentAnimation = Animation.IDLE;
    private transient boolean isSelected = false;
    private BoundingBox boundingBox = new BoundingBox(-2, -2, 18, 18).divide(WorldScene.SPRITE_SIZE);
    private TextTooltip tooltip = null;
    public float happiness = 1;
    public int taxes = 0;


    /**
     * Instancia la entidad, pero no la genera. Hay que utilizar <code>World#spawnEntity(Entity)</code> para generar la
     * entidad en el mundo.
     * @param location Posición donde se va a generar la entidad.
     * @see world.World#addDuck(Duck)
     */
    public Duck(Location location, House house) {
        this.location = location;
        this.house = house;

        this.scheduleIndex = Main.RANDOM.nextInt(Duck.SCHEDULE.length);

        this.boundingBox.move(location.getX(), location.getY());
    }

    public boolean nextPath() {
        if (this.pathfinding == null) return false;
        if (this.pathIndex >= this.pathfinding.size()) return true;


        Vector2f targetPosition = this.pathfinding.get(this.pathIndex).toVector2f();

        Vector2f direction = new Vector2f(targetPosition).sub(this.location.toVector2f()).normalize();

        this.moveTo(
                this.getLocation()
                        .add(new Vector2f(direction).mul(Main.RANDOM.nextFloat(.02f, .07f) +(this.getLocation().getTerrain().getType().equals(Terrain.TerrainType.WATER) ? .02f : 0)).mul(Main.tickSpeed))
        );

        if (direction.x() > 0) {
            this.flip = Duck.FLIP_HORIZONTAL;
        } else if (direction.x() < 0) {
            this.flip = Duck.NO_FLIP;
        }

        if (this.getLocation().toVector2f().distance(targetPosition) < 0.1f) {
            this.pathIndex++;
        }

        return false;
    }

    /**
     * Mueve la entidad.
     */
    public void moveTo(Location destination) {
        float movementX = destination.getX() - this.location.getX(), movementY = destination.getY() - this.location.getY();

        this.location.add(movementX, movementY);
        this.boundingBox.move(movementX, movementY);
    }

    /**
     * @return Posición actual de la entidad.
     */
    public Location getLocation() {
        return this.location.clone();
    }

    public ListIterator<Location> getPathfinding() {
        if (this.pathfinding == null) {
            return Collections.emptyListIterator();
        }

        return this.pathfinding.listIterator();
    }

    public int getPathIndex() {
        return this.pathIndex;
    }

    public float getFlip() {
        return this.flip;
    }

    public boolean setJobSite(JobSite jobSite) {
        if (jobSite.addWorker(this)) {
            if (this.jobSite != null) {
                this.jobSite.removeWorker(this);
            }

            this.jobSite = jobSite;

            Main.world.updateUnemployment();

            return true;
        }

        return false;
    }

    public void removeJobSite() {
        if (this.jobSite == null) {
            return;
        }

        this.jobSite.removeWorker(this);
        this.jobSite = null;

        Main.world.updateUnemployment();
    }

    public Texture getTexture() {
        if (this.getLocation().getTerrain().getType().equals(Terrain.TerrainType.WATER)) {
            return Textures.DUCK_SWIMMING;
        }

        return this.currentAnimation.TEXTURE;
    }

    //TODO: Ia de los patos.
    public void onTick() {
        if (this.pathfinding == null) {
            if (this.squawkingTime > 0) {
                this.squawkingTime--;
                this.currentAnimation = Animation.SQUAWKING;
            } else {
                if (Main.RANDOM.nextFloat() > 0.3) {
                    if (Main.RANDOM.nextFloat() > 0.55) {
                        this.currentAnimation = Animation.SQUAWKING;
                        this.squawkingTime = Duck.SQUAWKING_TIME;
                    } else {
                        this.currentAnimation = Animation.IDLE;
                    }

                    if (Main.RANDOM.nextFloat() > 0.4) {
                        this.flip = Main.RANDOM.nextBoolean() ? Duck.NO_FLIP : Duck.FLIP_HORIZONTAL;
                    }
                } else {
                    this.currentAnimation = Animation.WALKING;
                    this.doPathfinding();
                }
            }
        } else {
            if (this.nextPath()) {
                this.pathfinding = null;
            }
        }

        if (this.location.getTerrain().getType().equals(Terrain.TerrainType.WATER) && Main.RANDOM.nextFloat() > 0.8) {
            Main.world.addParticle(
                    new SplashParticle(this.getLocation().add(Main.RANDOM.nextFloat(this.boundingBox.getWidth() /2f), -.4f))
            );
        }
    }

    public void findJob() {
        if (!this.isUnemployed()) {
            return;
        }

        for (Building building : Main.world.getBuildings().stream().sorted(Comparator.comparing(building -> building.getLocation().getDistance(this.location))).toList()) {
            if (building.getLocation().getDistance(this.getLocation()) < 100 && building instanceof JobSite jobSite && this.setJobSite(jobSite)) {
                break;
            }
        }
    }

    private void setState() {
        System.out.println(Arrays.deepToString(Duck.SCHEDULE));

        if (Main.world.getDayTime() >= Duck.SCHEDULE[this.scheduleIndex][0] && Main.world.getDayTime() < Duck.SCHEDULE[this.scheduleIndex][1]) {
            if (this.jobSite == null) {
                this.state = ActivityType.IN_FREETIME;
            } else {
                this.state = ActivityType.WORK;
            }
            return;
        }

        if (Main.world.getDayTime() >= Duck.SCHEDULE[this.scheduleIndex][1] && Main.world.getDayTime() < Duck.SCHEDULE[this.scheduleIndex][2]) {
            this.state = ActivityType.IN_FREETIME;
            return;
        }

        if (Main.world.getDayTime() >= Duck.SCHEDULE[this.scheduleIndex][2]) {
            this.state = ActivityType.IN_HOUSE;
        }

        System.out.println(this.state);
    }

    private void doPathfinding() {
        if (this.jobSite == null) {
            this.findJob();
        }

        this.updateHappiness();

        this.setState();
        this.updateTooltip();

        Location destination = this.location;

        switch (this.state) {
            case WORK -> {
                destination = this.jobSite.getLocation().randomLocation(3);
            }
            case IN_HOUSE -> {
                destination = this.house.getLocation().randomLocation(2);
            }
            case IN_FREETIME -> {
                destination = this.location.randomLocation(15);
            }
        }

        this.pathfinding = Pathfinding.getPathfinder().findPath(
                getLocation(),
                destination);

        System.gc();

        this.pathIndex = 0;
    }

    private void updateHappiness() {
        this.happiness =
                .2f *Main.world.getHealth()
                +.2f *Main.world.getSecurity()
                +.4f *(1 -Main.world.getUnemployment())
                +.2f *(this.isUnemployed() ? 0 : 1);

        Main.world.updateHappiness();
    }

    private void updateTooltip() {
        this.tooltip = new TextTooltip();

        this.tooltip.addLine(this.state.toString(), GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);
        this.tooltip.addLine("Horario: " +this.scheduleIndex, GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);
        this.tooltip.addLine("HappyHappyHappy: " +this.happiness, GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);
        this.tooltip.addLine("Impuestps: " +this.taxes, GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);
    }

    private void onSelected() {
        this.updateTooltip();
    }

    private void onUnselected() {
        this.tooltip = null;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean selected) {
        if (!this.isSelected && selected) {
            this.onSelected();
        } else if (this.isSelected && !selected) {
            this.onUnselected();
        }

        this.isSelected = selected;
    }

    public BoundingBox getBoundingBox() {
        return this.boundingBox.clone();
    }

    public boolean isUnemployed() {
        return this.jobSite == null;
    }

    public float getHappiness() {
        return this.happiness;
    }

    public int getTaxes() {
        this.taxes = (int) Math.ceil(Duck.TAX_BASE *this.getHappiness());

        return this.taxes;
    }

    public Tooltip getTooltip() {
        return this.tooltip;
    }

    private enum ActivityType {
        IN_HOUSE,
        WORK,
        IN_FREETIME;
    }

    public enum Animation {
        IDLE(Textures.DUCK),
        WALKING(Textures.DUCK_WALKING),
        SQUAWKING(Textures.DUCK_SQUAWKING);

        private final Texture TEXTURE;

        Animation(Texture texture) {
            this.TEXTURE = texture;
        }
    }
}
