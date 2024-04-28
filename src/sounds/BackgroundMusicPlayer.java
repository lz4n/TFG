package sounds;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class BackgroundMusicPlayer {

    private Clip clip;
    private FloatControl volumeControl;

    public BackgroundMusicPlayer() {
        /*carga el archivo de audio
         *crea un flujo de audio
         *obtiene el control de volumen
         * */
        try {

            File audioFile = new File(SoundEffect.BACKGROUND_MUSIC.getFileName());
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioStream);
            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
   // para reproducir la música en bucle
    public void play() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    // detener la reproducción de la música o reiniciarla si ya se detuvo
    public void stop() {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            } else {
                clip.setFramePosition(0);
                clip.start();
            }
        }
    }


    //  para Aumentar el volumen.
    public void increaseVolume(float incrementAmount) {
        if (volumeControl != null) {
            float currentVolume = volumeControl.getValue();
            float maxVolume = volumeControl.getMaximum();
            float newVolume = Math.min(currentVolume + incrementAmount, maxVolume);
            volumeControl.setValue(newVolume);
        }
    }
    // Método para disminuir el volumen
    public void decreaseVolume(float decrementAmount) {
        if (volumeControl != null) {
            float currentVolume = volumeControl.getValue();
            float minVolume = volumeControl.getMinimum();
            float newVolume = Math.max(currentVolume - decrementAmount, minVolume);
            volumeControl.setValue(newVolume);
        }
    }

}
