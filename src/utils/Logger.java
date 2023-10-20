package utils;

/**
 * Clase que se utiliza para enviar mensajes a la consola de manera unificada.
 */
public class Logger {

    /**
     * Colores que se pueden utilizar en la consola de java. Algunos pueden no funcionar debido al sistema operativo
     * o al IDE.
     */
    private static final String RESET = "\u001B[0m",
            BLACK = "\u001B[30m",
            RED = "\u001B[31m",
            GREEN = "\u001B[32m",
            YELLOW = "\u001B[33m",
            BLUE = "\u001B[34m",
            PURPLE = "\u001B[35m",
            CYAN = "\u001B[36m",
            WHITE = "\u001B[37m";

    /**
     * Envía un mensaje a la consola.
     * @param content Mensaje que se va a enviar.
     * @param messageType Tipo de mensaje.
     */
    public static void sendMessage(String content, LogMessageType messageType) {
        System.out.printf("%s[%s]: %s%s",  messageType.COLOR, messageType, content, Logger.RESET);
        System.out.println();
    }

    /**
     * Envía un mensaje a la consola con argumentos.
     * @param content Mensaje que se va a enviar.
     * @param messageType Tupo de mensaje.
     * @param arguments Argumentos del mensaje, que se insertan mediante <code>%</code>
     */
    public static void sendMessage(String content, LogMessageType messageType, Object... arguments) {
        Logger.sendMessage(String.format(content, arguments), messageType);
    }

    /**
     * Enumeración con todos los tipos de mensaje que se pueden enviar.
     */
    public enum LogMessageType {
        /**
         * Mensaje de información genérica.
         */
        INFO(Logger.GREEN),

        /**
         * Mensaje de información de debug.
         */
        DEBUG(Logger.BLUE),

        /**
         * Mensaje de error no crítico (el juego se puede seguir ejecutando sin problemas aparentes, aunque este problema
         * pueda derivar en otros críticos.
         */
        WARNING(Logger.YELLOW),

        /**
         * Error crítico.
         */
        FATAL(Logger.RED),

        /**
         * Tipo de error para mensajes que no encajen en ninguno de los otros tipos.
         */
        DEFAULT(Logger.WHITE);

        /**
         * Cada tipo de mensaje tiene un color identificativo.
         */
        private final String COLOR;

        /**
         * @param color Color que va a indentificar al tipo de mensaje en la consola.
         */
        LogMessageType(String color) {
            this.COLOR = color;
        }
    }
}
