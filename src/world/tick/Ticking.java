package world.tick;

import java.util.LinkedList;
import java.util.List;

/**
 * Gestiona las tareas que se ejecutan en cada tick.
 */
public class Ticking implements Tickable {
    /**
     * Lista que contiene los objetos que se procesan en cada tick.
     */
    private static List<Tickable> TICKABLE_OBJETS = new LinkedList<>();

    /**
     * Lista que contiene los objetos que se procesan en cada tick aunque el juego esté en pausa.
     */
    private static List<Tickable> FORCE_TICKABLE_OBJETS = new LinkedList<>();

    /**
     * Objeto que contiene el método <code>onTick()</code>, que se llama en cada tick.
     */
    private Tickable onTick;

    /**
     * Añade el objeto a las listas de tickable objets.
     * @param onTick Objeto que queremos tickear.
     * @param force Indica si se va a procesar el tick aunque el juego esté en pausa.
     */
    public Ticking(Tickable onTick, boolean force) {
        this.onTick = onTick;
        if (force) {
            Ticking.FORCE_TICKABLE_OBJETS.add(this.onTick);
        } else {
            Ticking.TICKABLE_OBJETS.add(this.onTick);
        }
    }

    /**
     * Añade el objeto a las listas de tickable objets.<br>
     * Si la clase del objeto implementa la anotación <code>ForceTicking</code> se procesará el tick  aunque esté en pausa.
     * @see ForceTicking
     */
    public Ticking() {
        this.onTick = this;
        if (this.getClass().isAnnotationPresent(ForceTicking.class)) {
            Ticking.FORCE_TICKABLE_OBJETS.add(this.onTick);
        } else {
            Ticking.TICKABLE_OBJETS.add(this.onTick);
        }
    }

    /**
     * Elimina el objeto de las listas de tickable objects, haciendo que ya no se procese.
     */
    public void removeTicking() {
        Ticking.TICKABLE_OBJETS.remove(this);
        Ticking.FORCE_TICKABLE_OBJETS.remove(this);
    }

    /**
     * Elimina el objeto de las listas de tickable objects, haciendo que ya no se procese.
     * @param tickable Objeto tickable que se va a eliminar.
     */
    public static void removeTicking(Tickable tickable) {
        Ticking.TICKABLE_OBJETS.remove(tickable);
        Ticking.FORCE_TICKABLE_OBJETS.remove(tickable);
    }

    @Override
    public void onTick(long deltaTime) {}

    /**
     * Método que se llama en cada tick, siempre que no esté en pausa. Llama a los métodos <code>onTick</code> de los objetos.
     * @param deltaTime Tiempo que ha pasado desde el tick anterior.
     */
    public static void tick(long deltaTime) {
        new LinkedList<>(Ticking.TICKABLE_OBJETS).forEach(tickable -> {
            tickable.onTick(deltaTime);
        });
    }

    /**
     * Método que se llama en cada tick, aunque esté en pausa. Llama a los métodos <code>onTick</code> de los objetos.
     * @param deltaTime Tiempo que ha pasado desde el tick anterior.
     */
    public static void tickForced(long deltaTime) {
        new LinkedList<>(Ticking.FORCE_TICKABLE_OBJETS).forEach(tickable -> {
            tickable.onTick(deltaTime);
        });
    }
}
