package game.utils.sounds;

import game.Main;
import game.utils.Logger;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

public enum SoundEffect {
    BACKGROUND_MUSIC(true, "resources/sounds/background.wav"),
    UI_EFFECT_BUTTON_CLICK(false, "resources/sounds/ui_effects/button_click.wav"),
    WORLD_EFFECT_PLACE_STONE(false, "resources/sounds/world_effects/palce_stone.wav"),
    WORLD_EFFECT_PLACE_BUILDING(false, "resources/sounds/world_effects/place_building_1.wav", "resources/sounds/world_effects/place_building_2.wav", "resources/sounds/world_effects/place_building_3.wav"),
    WORLD_EFFECT_PLACE_TERRAIN(false, "resources/sounds/world_effects/place_terrain_1.wav", "resources/sounds/world_effects/place_terrain_2.wav"),
    WORLD_EFFECT_PLACE_GRASS(false, "resources/sounds/world_effects/place_grass_1.wav", "resources/sounds/world_effects/place_grass_2.wav", "resources/sounds/world_effects/place_grass_3.wav");

    private final boolean IS_BACKGROUND_MUSIC;
    private final String[] FILES;
    private static Clip musicClip;

    /**
     * Constructor para inicializar un efecto de sonido con sus archivos de audio correspondientes.
     *
     * @param isBackgroundMusic Indica si el efecto de sonido es música de fondo.
     * @param files             Archivos de audio asociados al efecto de sonido.
     */
    SoundEffect(boolean isBackgroundMusic, String... files) {
        this.IS_BACKGROUND_MUSIC = isBackgroundMusic;
        this.FILES = files;
    }

    /**
     * Reproduce el efecto de sonido utilizando el volumen de la categoría especificada.
     *
     * @param category La categoría de sonido a la cual se ajusta el volumen.
     * @return El clip de audio que se está reproduciendo.
     */
    public Clip play(SoundCategory category) {
        Clip audioClip = null;

        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream(this.FILES[Main.RANDOM.nextInt(this.FILES.length)])));

            DataLine.Info info = new DataLine.Info(Clip.class, audioInputStream.getFormat());
            audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioInputStream);
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException exception) {
            Logger.sendMessage("Error cargando el clip de audio %s: %s.", Logger.LogMessageType.FATAL, this.toString(), exception.getMessage());
        }

        ((FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN)).setValue((float) (Math.log(category.getVolume()) / Math.log(10.0) * 20.0));
        if (this.IS_BACKGROUND_MUSIC) {
            audioClip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            audioClip.setFramePosition(0);
            audioClip.start();
        }

        if (this.IS_BACKGROUND_MUSIC) {
            SoundEffect.musicClip = audioClip;
        }

        return audioClip;
    }

    /**
     * Actualiza el volumen de la música de fondo utilizando el volumen actual de la categoría de música.
     */
    public static void updateMusicVolume() {
        ((FloatControl) SoundEffect.musicClip.getControl(FloatControl.Type.MASTER_GAIN)).setValue((float) (Math.log(SoundCategory.MUSIC.getVolume()) / Math.log(10.0) * 20.0));
    }
}
