package main;

import utils.Logger;
import utils.Player;
import utils.render.Window;
import world.World;

import java.io.*;
import java.util.Random;
import java.util.regex.Pattern;

public class Main {
    //https://www.youtube.com/watch?v=88oZT7Aum6s&list=PLtrSb4XxIVbp8AKuEAlwNXDxr99e3woGE&index=3

    /**
     * Directorio de instalación del juego.
     * <ul>
     *     <li>En Windows: <strong>AppData\Roaming\.elPatoJuego</strong></li>
     *     <li>En Mac/Linux: <strong>/.config/.elPatoJuego</strong></li>
     * </ul>
     */
    public static File installDirectory;

    /**
     * Mundo de juego. Si no hay ningún mundo guardado se crea uno por defecto.
     */
    public static World world = new World("world", new Random().nextInt(100000000), 500);

    /**
     * Velocidad de tick del juego, por defecto.
     */
    public static int tickSpeed = 1;

    /**
     * Representa al jugador, independiente del mundo.
     * @see Player
     */
    public static final Player PLAYER = new Player();

    /**
     * Objeto de tipo Random para gestionar los aspectos aleatorios.
     * @see Random
     */
    public static final Random RANDOM = new Random();

    /**
     * Indica si el juego está en modo debug o no.
     */
    public static boolean isDebugging = false;

    public static void main(String[] args) {
        //Leemos los argumentos.
        for (String argument: args) {
            if (argument.equals("--activeDebug")) { //"--activeDebug" sirve para activar el modo debug.
                Main.isDebugging = true;
            } else if (Pattern.compile("--directory:(.+)").matcher(argument).matches()) { //"--directory:<dir>" cambia el directorio de juego.
                Main.installDirectory = new File(argument.replace("--directory:", ""));
            }
        }

        //Si no se ha establecido ningun directorio de instalación usando el argumento --directory se establecerá el directorio por defecto.
        if (Main.installDirectory == null) {
            final String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("win")) { //En Windows se utiliza "appdata".
                Main.installDirectory = new File(System.getenv("APPDATA"), ".elPatoJuego");
            } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) { //En Linux, Unix y Mac se utiliza ".config".
                Main.installDirectory = new File(System.getProperty("user.home") + "/.config", ".elPatoJuego");
            } else { //Si no se utiliza ninguno de los anteriores sistemas operativos se lanza una excepción.
                throw new RuntimeException(String.format("Se está utilizando un sistema opertativo para el cual no se tiene soporte (%s). Puedes solucionar este error indicando el directorio de instalación con el argumento --directory:<dir>", osName));
            }
        }

        //Si el direcetorio no existe, y no se puede crear, se lanza una excepción.
        if (!Main.installDirectory.exists() && !Main.installDirectory.mkdirs()) {
            throw new RuntimeException("No se ha podido iniciar la aplicación: no se ha conseguido establecer ningún directorio de juego (" + Main.installDirectory + ") o no se tienen permisos. Intenta utilizar --directory:<dir>.");
        }

        Logger.sendMessage("Direcctorio de juego: %s", Logger.LogMessageType.DEBUG, Main.installDirectory.getAbsolutePath());

        //Leemos el mundo "world.dat". Si no existe se creará un nuevo mundo por defecto.
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(Main.installDirectory, "worlds/world.dat")))) {
            Main.world = (World) inputStream.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            Logger.sendMessage("No se ha podido leer el mundo: %s", Logger.LogMessageType.WARNING, exception.getMessage());
        }

        Window.run();
    }


}
