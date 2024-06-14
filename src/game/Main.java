package game;

import game.utils.Logger;
import game.utils.Setting;
import game.utils.player.Player;
import game.utils.render.Window;
import game.world.World;

import java.io.*;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
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
    public static World world = new World(0, 0, 0);

    /**
     * Velocidad de tick del juego por defecto.
     */
    public static int tickSpeed = 1;

    /**
     * Representa al jugador, independiente del mundo.
     * @see Player
     */
    public static Player player;

    /**
     * Objeto de tipo Random para gestionar los aspectos aleatorios.
     * @see Random
     */
    public static final Random RANDOM = new Random();
    
    /**
     * Indica si el juego está en modo debug o no.
     */
    public static boolean isDebugging = false;

    /**
     * Punto de entrada principal para la aplicación.
     * <p>
     * Este método procesa los argumentos de línea de comandos para activar el modo de depuración y
     * cambiar el directorio de instalación. Establece el directorio de instalación por defecto si
     * no se proporciona uno, y maneja la configuración del entorno de la aplicación. Intenta leer
     * el archivo del mundo y carga la configuración y la ventana principal.
     * </p>
     *
     * @param args los argumentos de línea de comandos proporcionados a la aplicación.
     */
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

        //Si el directorio no existe, y no se puede crear, se lanza una excepción.
        if (!Main.installDirectory.exists() && !Main.installDirectory.mkdirs()) {
            throw new RuntimeException("No se ha podido iniciar la aplicación: no se ha conseguido establecer ningún directorio de juego (" + Main.installDirectory + ") o no se tienen permisos. Intenta utilizar --directory:<dir>.");
        }

        Logger.sendMessage("Direcctorio de juego: %s", Logger.LogMessageType.INFO, Main.installDirectory.getAbsolutePath());

        Setting.load();

        //Leemos último mundo jugado. Si no existe (-1) o da un error se creará un nuevo mundo por defecto.
        int lastPlayedWorldIndex = Setting.LAST_PLAYED_WORLD.getValue();
        if (lastPlayedWorldIndex != -1) {
            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(Main.installDirectory, String.format("worlds/%d.dat", lastPlayedWorldIndex))))) {
                Main.world = (World) inputStream.readObject();
                Logger.sendMessage("Se ha iniciado el mundo %d.", Logger.LogMessageType.INFO, lastPlayedWorldIndex);
            } catch (IOException | ClassNotFoundException exception) {
                Main.world = new World();
                Logger.sendMessage("No se ha podido leer el último mundo jugado: %s", Logger.LogMessageType.WARNING, exception.getMessage());
            }
        } else {
            Logger.sendMessage("No hay mundo previamente jugado, se va a crear uno nuevo", Logger.LogMessageType.INFO);
            Main.world = new World();
        }

        Main.player = new Player();

        Window.run();
    }

    public static Map<Integer, File> listWorlds() {
        File worldsDirectory = new File(Main.installDirectory, "worlds");
        Map<Integer, File> worlds = new HashMap<>();

        for (File worldFile : worldsDirectory.listFiles((dir, name) -> Pattern.compile("^\\d+\\.dat$").matcher(name).matches())) {
            try {
                worlds.put(Integer.parseInt(worldFile.getName().replace(".dat", "")), worldFile);
            } catch (NumberFormatException exception) {
                Logger.sendMessage("No se ha podido leer el archivo de mundo %s: %s", Logger.LogMessageType.WARNING, worldFile.getName(), exception.getMessage());
            }
        }

        return worlds;
    }

    public static int getNewWorldIndex() {
        File worldsDirectory = new File(Main.installDirectory, "worlds");
        int newWorldIndex = 0;

        for (File worldFile : worldsDirectory.listFiles((dir, name) -> Pattern.compile("^\\d+\\.dat$").matcher(name).matches())) {
            try {
                newWorldIndex = Math.max(Integer.parseInt(worldFile.getName().replace(".dat", "")), newWorldIndex);
            } catch (NumberFormatException exception) {
                Logger.sendMessage("No se ha podido leer el número del mundo %s: %s", Logger.LogMessageType.WARNING, worldFile.getName(), exception.getMessage());
            }
        }

        return newWorldIndex +1;
    }
}
