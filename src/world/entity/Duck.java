package world.entity;

import main.Main;
import npc.Node;
import npc.Pathfinding;
import org.joml.Vector2d;
import org.joml.Vector2f;
import utils.render.texture.Texture;
import utils.render.texture.Textures;
import world.World;
import world.feature.building.House;
import world.location.Location;

import java.lang.reflect.Array;
import java.nio.file.Path;

/**
 * Entidad que representa un pato.
 */
public class Duck extends Entity {
    private ActivityType state;
    /*{"8:30", "17:30", "22:00"},
      {"15:20", "21:20", "05:00"},
      {"23:40", "06:30", "12:20"}*/
    private final static int[][] SCHEDULE = new int[][]{ //Hará referencia a los horarios de los patos. HORA ENTRADA TRABAJO || HORA SALIDA TRABAJO || HORA IRSE A DORMIR
             {31875, 65625, 82500},
             {57500, 80000, 18750},
             {88750, 24375, 46250}
    }; //Entre medias de las horas de irse a dormir, tendrán varias horas de ocio, aquí es donde comenzarán a hacer el Pathfinding aleatorio.
    private static int positionSchedule = Main.RANDOM.nextInt(SCHEDULE.length); //Hará referencia a la posición del array de horario. Dependiendo de cuál salga, será una posición fija y tendrá que seguir el horario.
    private House house;
    //private Work work;
    /**
     * @param location Posición donde se va a generar un pato.
     */
    public Duck(Location location, House house) {
        super(location);
        this.house = house;
    }

    @Override
    public Texture getTexture() {
        return Textures.DUCK;
    }

    //TODO: Ia de los patos.
    @Override
    public void onTick(long deltaTime) {
        if (Main.RANDOM.nextFloat() >= 0.7) {
            this.move(new Vector2f(Main.RANDOM.nextFloat() -0.5f, Main.RANDOM.nextFloat() -0.5f));
        }
    }

    public void setState(ActivityType state) {
        this.state = state;
    }

    public ActivityType getState() {
        return state;
    }

    public void scheduleActivities() { //Dependiendo del horario que tenga, cambiará el estado del pato y hará un Pathfinding diferente a un sitio diferente.
        if (Main.world.getDayTime() == SCHEDULE[positionSchedule][0]) {
            setState(ActivityType.WORK);
        }else if (Main.world.getDayTime() == SCHEDULE[positionSchedule][1]) {
            //Vamos a hacer un Mathrandom del 1 al 2, el 1 será para ir a un sitio de ocio, el 2 será para ir a deambular por el mundo.
            if (Main.RANDOM.nextInt(1, 3) == 1)
                setState(ActivityType.IN_FREETIME);
            else
                setState(ActivityType.WALKING);
        }else if (Main.world.getDayTime() == SCHEDULE[positionSchedule][2]) {
            setState(ActivityType.IN_HOUSE);
        }
    }

    public void doPathfinding() {
        ActivityType actualState = getState();
        switch (actualState) {
            case IN_HOUSE -> {
                Pathfinding pf = new Pathfinding();
                Location start = getLocation();
                Location finish = house.getLocation();
                pf.findPath(start, finish);
            }
            case WORK -> {
                /*Pathfinding pf = new Pathfinding();
                Location start = getLocation();
                Location finish = house.getLocation();
                pf.findPath(start, finish);*/
            }
            case WALKING -> {//Le tenemos que pasar la posición actual del pato y que calcule x bloques más allá del pato.
                Pathfinding pf = new Pathfinding();
                Location start = getLocation();
                Location finish = location.randomLocation(20);
                pf.findPath(start, finish);
            }

            //case IN_FREETIME ->;
        }
    }

    public enum ActivityType {
        IN_HOUSE,
        WORK,
        WALKING,
        IN_FREETIME;
    }
}
