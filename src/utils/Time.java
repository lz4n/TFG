package utils;

/**
 * Clase utilizada para calcular fps y otras medidas de tiempo respecto al inicio de la aplicación.
 *
 * @author Izan
 */
public class Time {
    /**
     * Constante que almacena cuando se ha iniciado la aplicación.
     */
    public static final long TIME_STARTED = System.nanoTime();

    /**
     * @return Tiempo que ha pasado desde que se ha iniciado la aplicación en milisegundos.
     */
    public static long getTimeInNanoseconds() {
        return System.nanoTime() - Time.TIME_STARTED;
    }

    /**
     * Pasa de nanosegundos a segundos,
     * @param time Tiempo en nanosegundos.
     * @return Tiempo en segundos.
     */
    public static double nanosecondsToSeconds(long time) {
        return (double) time * 1E-9;
    }
}
