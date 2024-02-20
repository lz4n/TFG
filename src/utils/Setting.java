package utils;

import main.Main;

import java.io.*;
import java.util.Properties;

public enum Setting {
    FULLSCREEN("false");

    private static final File PROPERTIES_FILE = new File(Main.installDirectory, "settings.txt");
    private static final Properties PROPERTIES = new Properties();

    private final Object DEFAULT_VALUE;

    Setting(String defaultValue) {
        DEFAULT_VALUE = defaultValue;
    }

    public static void load() {
        try {
            Setting.PROPERTIES.load(new FileReader(PROPERTIES_FILE));
            Logger.sendMessage("Se ha leido con éxito el archivo de configuración.", Logger.LogMessageType.INFO);
        } catch (IOException exception) {
            Logger.sendMessage("Error leyendo el archivo de configuración: %s. Se va a intentar crear uno nuevo.", Logger.LogMessageType.WARNING, exception.getMessage());
            try {
                Setting.PROPERTIES_FILE.createNewFile();
            } catch (IOException exception2) {
                Logger.sendMessage("Error creando el archivo de configuración: %s", Logger.LogMessageType.WARNING, exception2.getMessage());
            }
        }
    }

    public static void save() {
        try {
            Setting.PROPERTIES.store(new FileWriter(Setting.PROPERTIES_FILE, false), "");
            Logger.sendMessage("Se ha guardado con éxito el archivo de configuración.", Logger.LogMessageType.INFO);
        } catch (IOException exception) {
            Logger.sendMessage("Error escribiendo el archivo de configuración: %s", Logger.LogMessageType.WARNING, exception.getMessage());
        }
    }

    public boolean getAsBoolean() {
        System.out.println(Setting.PROPERTIES.get(this.toString()));
        return Boolean.parseBoolean(Setting.PROPERTIES.getProperty(this.toString()));
    }

    public void setSetting(boolean value) {
        Setting.PROPERTIES.setProperty(this.toString(), String.valueOf(value));
    }
}
