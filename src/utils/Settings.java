package utils;

import main.Main;

import java.io.*;
import java.util.Properties;

public class Settings {
    private static final File PROPERTIES_FILE = new File(Main.installDirectory, "settings.txt");
    private static final Properties PROPERTIES = new Properties();

    public static void load() {
        try {
            Settings.PROPERTIES.load(new FileReader(PROPERTIES_FILE));
        } catch (IOException exception) {
            Logger.sendMessage("Error leyendo el archivo de configuración: %s. Se va a intentar crear uno nuevo.", Logger.LogMessageType.WARNING, exception.getMessage());
            try {
                Settings.PROPERTIES_FILE.createNewFile();
            } catch (IOException exception2) {
                Logger.sendMessage("Error creando el archivo de configuración: %s", Logger.LogMessageType.WARNING, exception2.getMessage());
            }
        }
    }

    public static void save() {
        try {
            Settings.PROPERTIES.store(new FileWriter(Settings.PROPERTIES_FILE), "");
        } catch (IOException exception) {
            Logger.sendMessage("Error escribiendo el archivo de configuración: %s", Logger.LogMessageType.WARNING, exception.getMessage());
        }
    }
}
