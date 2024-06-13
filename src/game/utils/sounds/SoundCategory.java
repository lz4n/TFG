package game.utils.sounds;

import game.utils.Setting;

/**
 * Enumeración que define las categorías de sonido utilizadas en la aplicación.
 * Cada categoría tiene un volumen específico que puede ser ajustado.
 */
public enum SoundCategory {

    MUSIC(Setting.MUSIC_VOLUME.getValue()),
    SOUND_EFFECTS(Setting.SOUND_EFFECTS_VOLUME.getValue());

    private static float generalVolume = Setting.GENERAL_VOLUME.getValue();

    /**
     * Obtiene el volumen general que afecta a todas las categorías de sonido.
     *
     * @return El volumen general actual.
     */
    public static float getGeneralVolume() {
        return SoundCategory.generalVolume;
    }

    /**
     * Establece el volumen general que afecta a todas las categorías de sonido.
     *
     * @param generalVolume El nuevo volumen general.
     */
    public static void setGeneralVolume(float generalVolume) {
        SoundCategory.generalVolume = generalVolume;
    }

    private float volume;

    /**
     * Constructor privado para inicializar una categoría de sonido con un volumen específico.
     *
     * @param volume El volumen inicial de la categoría.
     */
    SoundCategory(float volume) {
        this.volume = volume;
    }

    /**
     * Establece el volumen específico para esta categoría de sonido.
     *
     * @param volume El nuevo volumen específico.
     */
    public void setVolume(float volume) {
        this.volume = Math.clamp(volume, 0f, 1f);
    }

    /**
     * Establece el volumen específico para esta categoría de sonido.
     */
    public float getVolume() {
        return this.volume *SoundCategory.getGeneralVolume();
    }
}
