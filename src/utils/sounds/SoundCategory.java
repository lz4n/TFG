package utils.sounds;

public enum SoundCategory {
    MUSIC,
    SOUND_EFFECTS;

    private float volume = 1f;

    public void setVolume(float volume) {
        this.volume = Math.clamp(volume, 0f, 1f);
    }

    public float getVolume() {
        return this.volume;
    }
}
