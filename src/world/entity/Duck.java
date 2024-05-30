package world.entity;

import main.Main;
import npc.Node;
import npc.Pathfinding;
import org.joml.Vector2f;
import utils.Logger;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.World;
import world.feature.building.House;
import world.location.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un pato.
 */
public class Duck extends Entity {
    private static final int SQUAWKING_TIME = 50;
    /*{"8:30", "17:30", "22:00"},
      {"15:20", "21:20", "05:00"},
      {"23:40", "06:30", "12:20"}*/
    private final static int[][] SCHEDULE = new int[][]{ //Hará referencia a los horarios de los patos. HORA ENTRADA TRABAJO || HORA SALIDA TRABAJO || HORA IRSE A DORMIR
             {World.hourToTick(8, 30), World.hourToTick(17, 30), World.hourToTick(22, 0)},
             {World.hourToTick(15, 20), World.hourToTick(21,20), World.hourToTick(5, 0)},
             {World.hourToTick(23, 40), World.hourToTick(6, 30), World.hourToTick(12, 20)}
    };

    private House house;
    private ActivityType state;
    private int squawkingTime = 0;
    private Animation currentAnimation = Animation.IDLE;

    /**
     * @param location Posición donde se va a generar un pato.
     */
    public Duck(Location location, House house) {
        super(location);
        this.house = house;

        this.scheduleActivities();
    }

    @Override
    public Texture getTexture() {
        return this.currentAnimation.TEXTURE;
    }

    //TODO: Ia de los patos.
    @Override
    public void onTick(long deltaTime) {
        if (this.pathfinding == null) {
            if (this.squawkingTime > 0) {
                this.squawkingTime--;
                this.currentAnimation = Animation.SQUAWKING;
            } else {
                if (Main.RANDOM.nextFloat() > 0.63) {
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
                super.getLocation(),
                this.house.getLocation().randomLocation(15));

        System.gc();

        this.pathIndex = 0;
    }

    public enum ActivityType {
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
