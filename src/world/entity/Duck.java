package world.entity;

import main.Main;
import npc.Pathfinding;
import org.joml.Vector2f;
import ui.tooltip.TextTooltip;
import ui.tooltip.Tooltip;
import utils.BoundingBox;
import utils.render.GameFont;
import utils.render.scene.WorldScene;
import utils.render.texture.Colors;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.World;
import world.feature.building.Building;
import world.feature.building.House;
import world.feature.building.jobSite.JobSite;
import world.location.Location;
import world.tick.Ticking;

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
public class Duck extends Ticking implements Serializable {
    public static final float NO_FLIP = 0, FLIP_HORIZONTAL = 1, FLIP_VERTICAL = 2, FLIP_BOTH = 3;
    private static final int SQUAWKING_TIME = 50;
    /*{"8:30", "17:30", "22:00"},
          {"15:20", "21:20", "05:00"},
          {"23:40", "06:30", "12:20"}*/
    private final static int[][] SCHEDULE = new int[][]{ //Hará referencia a los horarios de los patos. HORA ENTRADA TRABAJO || HORA SALIDA TRABAJO || HORA IRSE A DORMIR
             {World.hourToTick(8, 30), World.hourToTick(17, 30), World.hourToTick(22, 0)},
             {World.hourToTick(15, 20), World.hourToTick(21,20), World.hourToTick(5, 0)},
             {World.hourToTick(23, 40), World.hourToTick(6, 30), World.hourToTick(12, 20)}
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
    private ActivityType state;
    private int squawkingTime = 0;
    private Animation currentAnimation = Animation.IDLE;
    private transient boolean isSelected = false;
    private BoundingBox boundingBox = new BoundingBox(-2, -2, 18, 18).divide(WorldScene.SPRITE_SIZE);
    private TextTooltip tooltip = null;


    /**
     * Instancia la entidad, pero no la genera. Hay que utilizar <code>World#spawnEntity(Entity)</code> para generar la
     * entidad en el mundo.
     * @param location Posición donde se va a generar la entidad.
     * @see world.World#addDuck(Duck)
     */
    public Duck(Location location, House house) {
        this.location = location;
        this.house = house;

        this.boundingBox.move(location.getX(), location.getY());
    }

    public boolean nextPath() {
        if (this.pathfinding == null) return false;
        if (this.pathIndex >= this.pathfinding.size()) return true;


        Vector2f targetPosition = this.pathfinding.get(this.pathIndex).toVector2f();

        Vector2f direction = new Vector2f(targetPosition).sub(this.getLocation().toVector2f()).normalize();

        this.moveTo(this.getLocation().add(new Vector2f(direction).mul(0.05f)));

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

        this.findJob();

        Main.world.updateUnemployment();
    }

    public Texture getTexture() {
        return this.currentAnimation.TEXTURE;
    }

    public void onRemove() {
        this.removeJobSite();
    }

    public void onAdd() {
        this.findJob();
        this.scheduleActivities();
    }

    //TODO: Ia de los patos.
    @Override
    public void onTick(long deltaTime) {
        if (this.pathfinding == null) {
            if (this.squawkingTime > 0) {
                this.squawkingTime--;
                this.currentAnimation = Animation.SQUAWKING;
            } else {
                if (Main.RANDOM.nextFloat() > 0.93) {
                    if (Main.RANDOM.nextFloat() > 0.55) {
                        this.currentAnimation = Animation.SQUAWKING;
                        this.squawkingTime = Duck.SQUAWKING_TIME;
                    } else {
                        this.currentAnimation = Animation.IDLE;
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
    }

    public void findJob() {
        if (!this.isUnemployed()) {
            return;
        }

        for (Building building : Main.world.getBuildings()) {
            if (building instanceof JobSite jobSite && this.setJobSite(jobSite)) {
                break;
            }
        }

        this.scheduleActivities();
    }

    public void setState(ActivityType state) {
        this.state = state;
    }

    public ActivityType getState() {
        return state;
    }

    public void scheduleActivities() { //Dependiendo del horario que tenga, cambiará el estado del pato y hará un Pathfinding diferente a un sitio diferente.
        this.setState(ActivityType.IN_FREETIME);
        /*if (Main.world.getDayTime() == SCHEDULE[positionSchedule][0]) {
            setState(ActivityType.WORK);
        }else if (Main.world.getDayTime() == SCHEDULE[positionSchedule][1]) {
            //Vamos a hacer un Mathrandom del 1 al 2, el 1 será para ir a un sitio de ocio, el 2 será para ir a deambular por el mundo.
            if (Main.RANDOM.nextInt(1, 3) == 1)
                setState(ActivityType.IN_FREETIME);
            else
                setState(ActivityType.WALKING);
        }else if (Main.world.getDayTime() == SCHEDULE[positionSchedule][2]) {
            setState(ActivityType.IN_HOUSE);
        }*/
    }

    private void doPathfinding() {
        this.pathfinding = Pathfinding.getPathfinder().findPath(
                getLocation(),
                this.house.getLocation().randomLocation(15));

        System.gc();

        this.pathIndex = 0;
    }

    private void updateTooltip() {
        this.tooltip = new TextTooltip();

        this.tooltip.addLine("cuack", GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);
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

    public JobSite getJobSite() {
        return this.jobSite;
    }

    public Tooltip getTooltip() {
        return this.tooltip;
    }

    private enum ActivityType {
        IN_HOUSE,
        WORK,
        WALKING,
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
