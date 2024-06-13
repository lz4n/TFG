package game.utils;

import game.Main;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Contiene la configuración general del juego.
 */
public class Setting<T> {
    private static final List<Setting<?>> SETTING_LIST = new LinkedList<>();
    private static final File PROPERTIES_FILE = new File(Main.installDirectory, "settings.txt");
    private static final Properties PROPERTIES = new Properties();

    /**
     * Determina si el juego está en pantalla completa o no. Si está en "true" al iniciar el juego se pondrá en pantalla
     * completa automaticamente.
     */
    public static final Setting<Boolean> FULLSCREEN = new Setting<>("FULLSCREEN", false);

    /**
     * Determina si cuando sea por la noche se van a oscurecer los objetos en pantalla. No afecta al funcionamiento, es
     * puramente visual.
     */
    public static final Setting<Boolean> DIURNAL_CYCLE = new Setting<>("DIURNAL_CYCLE", true);

    /**
     * Representa la probabilidad de que <b>NO</b> aparezca una nube en cada tick.
     */
    public static final Setting<Float> CLOUDS = new Setting<>("CLOUDS", 0.98f);

    /**
     * Representa la probabilidad de que <b>NO</b> aparezca una nube en cada tick.
     */
    public static final Setting<Float> GENERAL_VOLUME = new Setting<>("GENERAL_VOLUME", 1f);

    /**
     * Representa la probabilidad de que <b>NO</b> aparezca una nube en cada tick.
     */
    public static final Setting<Float> MUSIC_VOLUME = new Setting<>("MUSIC_VOLUME", 1f);

    /**
     * Representa la probabilidad de que <b>NO</b> aparezca una nube en cada tick.
     */
    public static final Setting<Float> SOUND_EFFECTS_VOLUME = new Setting<>("SOUND_EFFECTS_VOLUME", 1f);

    private final String ID;
    private T value;
    private Setting(String name, T defaultValue) {
        this.ID = name;
        this.value = defaultValue;

        Setting.SETTING_LIST.add(this);
    }

    /**
     * Carga las opciones desde el archivo de configuración.
     */
    public static void load() {
        try {
            Setting.PROPERTIES.load(new FileReader(PROPERTIES_FILE));

            for (Setting<?> setting : Setting.SETTING_LIST) {
                Object value = null;

                try {
                    switch (setting.getValue()) {
                        case Boolean boolValue -> {
                            value = Boolean.parseBoolean(Setting.PROPERTIES.getProperty(setting.ID, boolValue.toString()));
                        }
                        case Float floatValue -> {
                            value = Float.parseFloat(Setting.PROPERTIES.getProperty(setting.ID, floatValue.toString()));
                        }
                        default ->
                                Logger.sendMessage("El tipo de datos de la opción %s no está soportado para lectura.", Logger.LogMessageType.WARNING);
                    }
                } catch (ClassCastException exception) {
                    Logger.sendMessage("Error de casting al leer la opción %s: %s.", Logger.LogMessageType.WARNING, setting.ID, exception.getMessage());
                }

                if (value != null) {
                    setting.setValue(value);
                }
            }

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
            for (Setting<?> setting : Setting.SETTING_LIST) {
                Setting.PROPERTIES.setProperty(setting.ID, setting.getValue().toString());
            }

            Setting.PROPERTIES.store(new FileWriter(Setting.PROPERTIES_FILE, false), null);
            Logger.sendMessage("Se ha guardado con éxito el archivo de configuración.", Logger.LogMessageType.INFO);
        } catch (IOException exception) {
            Logger.sendMessage("Error escribiendo el archivo de configuración: %s.", Logger.LogMessageType.WARNING, exception.getMessage());
        }
    }

    /**
     * @return Valor de la opción.
     */
    public T getValue() {
        return this.value;
    }

    /**
     * Establece un valor de la opción de juego. No las guarda en el archivo, para eso hay que utlizar #save().
     * @param value Nuevo valor que va a tener la opción de juego.
     * @see Setting#save()
     */
    public void setValue(Object value) {
        try {
            this.value = (T) value;
        } catch (ClassCastException exception) {
            Logger.sendMessage("No se ha podido castear el valor %s de la opción %s: %s.", Logger.LogMessageType.WARNING, value, this.ID, exception.getMessage());
        }
    }
}
