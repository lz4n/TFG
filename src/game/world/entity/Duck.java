package game.world.entity;

import game.Main;
import game.npc.Pathfinding;
import game.ui.tooltip.TextTooltip;
import game.ui.tooltip.Tooltip;
import game.utils.BoundingBox;
import game.utils.render.GameFont;
import game.utils.render.scene.WorldScene;
import game.utils.render.texture.Texture;
import game.utils.render.texture.Textures;
import game.world.World;
import game.world.particle.Particle;
import game.world.particle.SplashParticle;
import org.joml.Vector2f;
import game.world.feature.building.Building;
import game.world.feature.building.House;
import game.world.feature.building.jobSite.JobSite;
import game.world.location.Location;
import game.world.terrain.Terrain;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

/**
 * Las entidades son elementos móviles del juego con los que el jugador puede interactuar (el jugador no puede interactuar
 * con las partículas).
 * @see Particle
 */
public class Duck implements Serializable {

    public static final float NO_FLIP = 0, FLIP_HORIZONTAL = 1;

    private static final int SQUAWKING_TIME = 50;
    private final static int[][] SCHEDULE = new int[][]{
             {World.hourToTick(8, 30), World.hourToTick(17, 30), World.hourToTick(22, 0)},
             {World.hourToTick(15, 20), World.hourToTick(21,20), World.hourToTick(5, 0)},
             {World.hourToTick(0, 40), World.hourToTick(6, 30), World.hourToTick(12, 20)}
    };

    private Location location;
    private float flip = Duck.NO_FLIP;
    private List<Location> pathfinding;
    private int pathIndex = 0;
    private House house;
    private JobSite jobSite = null;
    private ActivityType state = ActivityType.IN_HOUSE;
    private int squawkingTime = 0;
    private int scheduleIndex;
    private transient Animation currentAnimation = Animation.IDLE;
    private transient boolean isSelected = false;
    private BoundingBox boundingBox = new BoundingBox(-2, -2, 18, 18).divide(WorldScene.SPRITE_SIZE);
    private transient TextTooltip tooltip = null;
    private float happiness = 1;
    private int taxes = 0;
    private DuckVariant duckVariant = DuckVariant.getRandomVariant();


    /**
     * Instancia la entidad, pero no la genera. Hay que utilizar <code>World#spawnEntity(Entity)</code> para generar la
     * entidad en el mundo.
     * @param location Posición donde se va a generar la entidad.
     * @see World#addDuck(Duck)
     */
    public Duck(Location location, House house) {
        this.location = location;
        this.house = house;

        this.scheduleIndex = Main.RANDOM.nextInt(Duck.SCHEDULE.length);

        this.boundingBox.move(location.getX(), location.getY());
    }

    private boolean nextPath() {
        if (this.pathfinding == null) return false;
        if (this.pathIndex >= this.pathfinding.size()) return true;


        Vector2f targetPosition = this.pathfinding.get(this.pathIndex).toVector2f();

        Vector2f direction = new Vector2f(targetPosition).sub(this.location.toVector2f()).normalize();

        this.moveTo(
                this.getLocation()
                        .add(new Vector2f(direction).mul(Main.RANDOM.nextFloat(.01f, .04f) +(this.getLocation().getTerrain().getType().equals(Terrain.TerrainType.WATER) ? .02f : 0)).mul(Main.tickSpeed))
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
     * Mueve la entidad hacia una ubicación especificada.
     *
     * @param destination La ubicación a la que se moverá la entidad.
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

    /**
     * Obtiene el tipo de terreno bajo la entidad.
     *
     * @return El tipo de terreno bajo la entidad.
     */
    public Terrain.TerrainType getTerrainTypeUnder() {
        Location location =  this.getLocation().add(.5f);

        if (location.isOutOfTheWorld()) {
            return Terrain.TerrainType.GRASS;
        }

        return location.getTerrain().getType();
    }

    /**
     * Obtiene un iterador sobre la lista de ubicaciones de la ruta de búsqueda.
     *
     * @return Un iterador sobre la lista de ubicaciones de la ruta de búsqueda.
     */
    public ListIterator<Location> getPathfinding() {
        if (this.pathfinding == null) {
            return Collections.emptyListIterator();
        }

        return this.pathfinding.listIterator();
    }

    /**
     * Obtiene el índice actual de la ruta de búsqueda.
     *
     * @return El índice actual de la ruta de búsqueda.
     */
    public int getPathIndex() {
        return this.pathIndex;
    }

    /**
     * Obtiene el estado de volteo actual.
     *
     * @return El estado de volteo actual.
     */
    public float getFlip() {
        return this.flip;
    }

    /**
     * Establece el sitio de trabajo para el pato.
     *
     * @param jobSite El sitio de trabajo que se va a establecer.
     * @return true si el pato fue asignado correctamente al sitio de trabajo, false de lo contrario.
     */
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

    /**
     * Remueve el sitio de trabajo asignado al pato, si tiene alguno. Esto implica retirar al pato
     * de la lista de trabajadores del sitio de trabajo y luego establecer el sitio de trabajo como nulo.
     * Además, actualiza la situación de desempleo en el mundo.
     */
    public void removeJobSite() {
        if (this.jobSite == null) {
            return;
        }

        this.jobSite.removeWorker(this);
        this.jobSite = null;

        Main.world.updateUnemployment();
    }

    /**
     * Obtiene la textura de superposición del pato, dependiendo del tipo de terreno bajo sus pies.
     * Si el pato está sobre agua, devuelve la textura de natación del pato. En otro caso, devuelve
     * la textura correspondiente a la animación actual del pato.
     *
     * @return La textura de superposición del pato.
     */
    public Texture getOverlay() {
        if (this.currentAnimation == null) {
            this.currentAnimation = Animation.IDLE;
        }

        if (this.getTerrainTypeUnder().equals(Terrain.TerrainType.WATER)) {
            return Textures.DUCK_SWIMMING;
        }

        return this.currentAnimation.TEXTURE;
    }

    /**
     * Obtiene la textura corporal del pato, que está determinada por la variante del pato.
     *
     * @return La textura corporal del pato.
     */
    public Texture getBodyTexture() {
        return this.duckVariant.TEXTURE;
    }

    /**
     * Realiza las acciones del pato en cada tic de juego. Dependiendo de las condiciones,
     * el pato puede cambiar su animación entre caminar, graznar o estar quieto, así como
     * realizar acciones específicas si está sobre el agua.
     */
    public void onTick() {
        if (this.pathfinding == null) {
            if (this.squawkingTime > 0) {
                this.squawkingTime--;
                this.currentAnimation = Animation.SQUAWKING;
            } else {
                if (Main.RANDOM.nextFloat() > 0.2) {
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

        if (this.getTerrainTypeUnder().equals(Terrain.TerrainType.WATER) && Main.RANDOM.nextFloat() > 0.8) {
            Main.world.addParticle(
                    new SplashParticle(this.getLocation().add(Main.RANDOM.nextFloat(this.boundingBox.getWidth() /2f), -.4f))
            );
        }
    }

    /**
     * Busca un trabajo para el pato si actualmente está desempleado. Itera sobre todos los edificios
     * del mundo, ordenados por la distancia desde la ubicación del pato, y asigna el primer trabajo
     * disponible que esté dentro de un radio de 100 unidades y que el pato pueda aceptar.
     *
     * @see Building
     * @see JobSite
     */
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

    /**
     * Establece el estado actual del pato basado en la hora del día y su situación laboral.
     * Dependiendo de la hora del día y si el pato tiene un sitio de trabajo asignado, el estado se
     * establecerá como trabajo, tiempo libre o en casa.
     */
    private void setState() {
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
    }

    private void doPathfinding() {
        if (this.jobSite == null) {
            this.findJob();
        }

        this.updateHappiness();

        this.setState();

        if (this.isSelected) {
            this.updateTooltip();
        }

        Location destination = this.location;

        switch (this.state) {
            case WORK -> {
                if (jobSite != null) {
                    destination = this.jobSite.getLocation().randomLocation(3);
                }
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
                .3f *Main.world.getHealth()
                +.3f *Main.world.getSecurity()
                +.4f *(1 -Main.world.getUnemployment())
                +.2f *(this.isUnemployed() ? 0 : 1);

        this.happiness = Math.clamp(this.happiness, 0, 1);

        Main.world.updateHappiness();
    }

    private void updateTooltip() {
        if (this.tooltip != null) {
            this.tooltip.remove();
        }

        this.tooltip = new TextTooltip();

        this.tooltip.addLine(String.format("""
                %s
                %s
                Felicidad: %d%%
                
                Impuestos: %d§
                """, this.state.DESCRIPTION, this.isUnemployed() ? "No encuentra trabajo a una distancia razonable" : "Con trabajo", (int) (this.happiness *100), this.taxes), GameFont.PIXEL_CONTENT, Font.PLAIN, TextTooltip.CONTENT_TEXT_SIZE);
    }

    private void onSelected() {
        this.updateTooltip();
    }

    private void onUnselected() {
        this.tooltip = null;
    }

    /**
     * Devuelve si el pato está seleccionado.
     *
     * @return true si el pato está seleccionado, false de lo contrario.
     */
    public boolean isSelected() {
        return this.isSelected;
    }

    /**
     * Establece si el pato está seleccionado. Llama a los métodos correspondientes
     * {@link #onSelected()} o {@link #onUnselected()} dependiendo del cambio de estado.
     *
     * @param selected true para seleccionar el pato, false para deseleccionarlo.
     */
    public void setSelected(boolean selected) {
        if (!this.isSelected && selected) {
            this.onSelected();
        } else if (this.isSelected && !selected) {
            this.onUnselected();
        }

        this.isSelected = selected;
    }

    /**
     * Devuelve el cuadro delimitador (bounding box) del pato, clonado para evitar modificaciones
     * accidentales.
     *
     * @return El cuadro delimitador clonado del pato.
     */
    public BoundingBox getBoundingBox() {
        return this.boundingBox.clone();
    }

    /**
     * Determina si el pato está desempleado, es decir, si no tiene un sitio de trabajo asignado.
     *
     * @return true si el pato está desempleado, false de lo contrario.
     */
    public boolean isUnemployed() {
        return this.jobSite == null;
    }

    /**
     * Obtiene el nivel de felicidad actual del pato.
     *
     * @return El nivel de felicidad del pato.
     */
    public float getHappiness() {
        return this.happiness;
    }

    /**
     * Calcula los impuestos que debe pagar el pato en función de su felicidad.
     *
     * @return Los impuestos que debe pagar el pato.
     */
    public int getTaxes() {
        if (this.isUnemployed()) {
            this.taxes = -20;
        } else {
            this.taxes = (int) Math.ceil(25 *this.getHappiness());
        }

        return this.taxes;
    }

    /**
     * Obtiene el tooltip asociado al pato.
     *
     * @return El tooltip asociado al pato.
     */
    public Tooltip getTooltip() {
        return this.tooltip;
    }

    /**
     * Enumeración que representa los tipos de actividad del pato.
     */
    private enum ActivityType {
        IN_HOUSE("En casa."),
        WORK("Trabajando"),
        IN_FREETIME("Pasando el rato.");

        private final String DESCRIPTION;


        ActivityType(String description) {
            this.DESCRIPTION = description;
        }
    }

    /**
     * Enumeración que representa las animaciones del pato.
     */
    public enum Animation {
        IDLE(Textures.DUCK_IDLE),
        WALKING(Textures.DUCK_WALKING),
        SQUAWKING(Textures.DUCK_SQUAWKING);

        private final Texture TEXTURE;

        Animation(Texture texture) {
            this.TEXTURE = texture;
        }
    }

    /**
     * Enumeración que representa las variantes posibles del pato.
     */
    public enum DuckVariant {
        GREEN(Textures.DUCK_BODY_GREEN),
        YELLOW(Textures.DUCK_BODY_YELLOW),
        BLACK(Textures.DUCK_BODY_BLACK),
        WHITE(Textures.DUCK_BODY_WHITE);

        private final Texture TEXTURE;

        DuckVariant(Texture texture) {
            this.TEXTURE = texture;
        }

        private static DuckVariant getRandomVariant() {
            return DuckVariant.values()[Main.RANDOM.nextInt(DuckVariant.values().length)];
        }
    }
}
