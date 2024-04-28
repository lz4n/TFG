package sounds;

public enum SoundEffect {
   BACKGROUND_MUSIC("assets/music/prueba.wav"),
    SOUND_TEST("assets/music/punch.wav")
    ;

    private final String fileName;

    SoundEffect(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
