package world.tick;

/**
 * Representa la funcionalidad de un objeto que necesitamos que se procese en cada tick.
 * @see Ticking
 */
public interface Tickable {

    /**
     * Método que se llama en cada tick.
     * @param deltaTime Tiempo que ha pasado desde el anterior tick.
     */
    void onTick(long deltaTime);
}
