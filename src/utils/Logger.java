package utils;

public class Logger {
    private static final String RESET = "\u001B[0m",
            BLACK = "\u001B[30m",
            RED = "\u001B[31m",
            GREEN = "\u001B[32m",
            YELLOW = "\u001B[33m",
            BLUE = "\u001B[34m",
            PURPLE = "\u001B[35m",
            CYAN = "\u001B[36m",
            WHITE = "\u001B[37m";

    public static void sendMessage(String content, LogMessageType messageType) {
        System.out.println(messageType.COLOR + content + Logger.RESET);
    }

    public static void sendMessage(String content, LogMessageType messageType, Object... attributes) {
        Logger.sendMessage(String.format(content, attributes), messageType);
    }

    public enum LogMessageType {
        INFO(Logger.GREEN),
        DEBUG(Logger.BLUE),
        WARNING(Logger.YELLOW),
        FATAL(Logger.RED),
        DEFAULT(Logger.WHITE);

        private final String COLOR;

        LogMessageType(String color) {
            this.COLOR = color;
        }
    }
}
