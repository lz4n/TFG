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
    public static File installDirectory;

    public static World world = new World("world", new Random().nextInt(100000000), 500);;
    public static int tickSpeed = 1;
    public static final Player PLAYER = new Player();
    public static final Random RANDOM = new Random();

    /**
     * Si el juego está en modo debug o no.
     */
    public static boolean isDebugging = false;

    public static void main(String[] args) {
        for (String argument: args) {
            if (argument.equals("--activeDebug")) {
                Main.isDebugging = true;
            } else if (Pattern.compile("--directory:(.+)").matcher(argument).matches()) {
                Main.installDirectory = new File(argument.replace("--directory:", ""));
            }
        }

        if (Main.installDirectory == null) {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("win")) {
                Main.installDirectory = new File(System.getenv("APPDATA"), ".elPatoJuego");
            } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
                Main.installDirectory = new File(System.getProperty("user.home") + "/.config", ".elPatoJuego");
            }
        }

        if (Main.installDirectory == null || (!Main.installDirectory.exists() && !Main.installDirectory.mkdirs())) {
            throw new RuntimeException("No se ha podido iniciar la aplicación: no se ha conseguido establecer ningún directorio de juego (" + Main.installDirectory + ") o no se tienen permisos. Intenta utilizar --directory:<dir>.");
        }
        Logger.sendMessage("Direcctorio de juego: %s", Logger.LogMessageType.DEBUG, Main.installDirectory.getAbsolutePath());

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(Main.installDirectory, "worlds/world.dat")))) {
            Main.world = (World) inputStream.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            Logger.sendMessage("No se ha podido leer el mundo: %s", Logger.LogMessageType.WARNING, exception.getMessage());
        }

        Window.run();
    }


}