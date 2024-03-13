package utils;

import main.Main;

import java.io.*;
import java.util.Properties;

/**
 * Contiene la configuración general del juego.
 */
public enum Setting {
    /**
     * Determina si el juego está en pantalla completa o no. Si está en "true" al iniciar el juego se pondrá en pantalla
     * completa automaticamente.
     */
    FULLSCREEN("false");

    /**
     * Archivo en el que se guarda la configuración, "settings.txt" dentro del directorio de juego.
     */
    private static final File PROPERTIES_FILE = new File(Main.installDirectory, "settings.txt");

    /**
     * Objeto Properties que almacena la configuración.
     */
    private static final Properties PROPERTIES = new Properties();

    /**
     * Valor por defecto de la opción de juego.
     */
    private final String DEFAULT_VALUE;

    Setting(String defaultValue) {
        DEFAULT_VALUE = defaultValue;
    }

    /**
     * Carga las opciones desde el archivo de configuración.
     */
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

    /**
     * Guarda las opciones en el fichero.
     */
    public static void save() {
        try {
            Setting.PROPERTIES.store(new FileWriter(Setting.PROPERTIES_FILE, false), null);
            Logger.sendMessage("Se ha guardado con éxito el archivo de configuración.", Logger.LogMessageType.INFO);
        } catch (IOException exception) {
            Logger.sendMessage("Error escribiendo el archivo de configuración: %s", Logger.LogMessageType.WARNING, exception.getMessage());
        }
    }

    /**
     * @return Valor de la opción cómo un booleano.
     */
    public boolean getAsBoolean() {
        return Boolean.parseBoolean(Setting.PROPERTIES.getProperty(this.toString(), this.DEFAULT_VALUE));
    }

    /**
     * Establece un valor de la opción de juego. No las guarda en el archivo, para eso hay que utlizar #save().
     * @param value Nuevo valor que va a tener la opción de juego.
     * @see Setting#save()
     */
    public void setValue(Object value) {
        Setting.PROPERTIES.setProperty(this.toString(), String.valueOf(value));
    }
}
