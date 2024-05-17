package utils.sounds;

import utils.Setting;

public enum SoundCategory {

    MUSIC(Setting.MUSIC_VOLUME.getValue()),
    SOUND_EFFECTS(Setting.SOUND_EFFECTS_VOLUME.getValue());

    private static float generalVolume = Setting.GENERAL_VOLUME.getValue();

    public static float getGeneralVolume() {
        return SoundCategory.generalVolume;
    }

    public static void setGeneralVolume(float generalVolume) {
        SoundCategory.generalVolume = generalVolume;
    }

    private float volume = 1f;

    private SoundCategory(float volume) {
        this.volume = volume;
    }

    public void setVolume(float volume) {
        this.volume = Math.clamp(volume, 0f, 1f);
    }

    public float getRawVolume() {
        return this.volume;
    }

    public float getVolume() {
        return this.volume *SoundCategory.getGeneralVolume();
    }
}
