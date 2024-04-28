package sounds;

public class SoundUtils {

    public static void playSoundEffect(String soundFilePath) {
        SoundEffectPlayer soundPlayer = new SoundEffectPlayer(soundFilePath);
        soundPlayer.play();

    }
}
