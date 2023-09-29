package utils;

public class Time {
    public static final long TIME_STARTED = System.nanoTime();

    public static long getTimeInNanoseconds() {
        return System.nanoTime() - Time.TIME_STARTED;
    }

    public static double nanosecondsToSeconds(long time) {
        return (double) time * 1E-9;
    }

    public static float getTime() { return (float)((System.nanoTime() - TIME_STARTED) * 1E-9); }
}
