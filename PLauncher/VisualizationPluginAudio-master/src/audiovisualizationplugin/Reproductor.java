package audiovisualizationplugin;

import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class Reproductor {

    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private Pane view;
    private boolean isPlaying = false;
    private double velocidad = 1;
    private double deltaT;
    public long end;
    private static final Logger logger = Logger.getLogger(Reproductor.class.getName());

    public Reproductor(File file) {
        try {
            Media media = new Media(file.toURI().toURL().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaView = new MediaView(mediaPlayer);
            view = new Pane(mediaView);
            mediaPlayer.setOnReady(() -> end = (long) media.getDuration().toMillis());
            mediaPlayer.setOnEndOfMedia(() -> {
                stop(); // Detén el MediaPlayer y reinicia
            });

        } catch (MalformedURLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        
    }

    public Pane getView() {
        return view;
    }

    public void play(long millis, long end, boolean sync) {
        if (millis <= end) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED || mediaPlayer.getStatus() == MediaPlayer.Status.READY) {
                mediaPlayer.seek(Duration.millis(millis)); // Reinicia desde el tiempo especificado
            }
            iniciarReproduccionNormal(); // Llama a iniciarReproduccionNormal para empezar
        }
    }


    public void stop() {
        isPlaying = false;
        velocidad = 1;
        mediaPlayer.stop();
        mediaPlayer.seek(Duration.ZERO); // Reinicia el MediaPlayer al inicio
    }



    public void pause() {
        isPlaying = false;
        mediaPlayer.pause();
    }

    public void current(long millis) {
        mediaPlayer.seek(Duration.millis(millis));
    }

    public long time() {
        return (long) mediaPlayer.getCurrentTime().toMillis();
    }

    public long duration() {
        return (long) mediaPlayer.getTotalDuration().toMillis();
    }

    public MediaPlayer.Status getStatus() {
        return mediaPlayer.getStatus();
    }

    private void ajustarVelocidad() {
        if ((int) deltaT > 0 && velocidad >= 0.1) {
            velocidad -= 0.02;
            mediaPlayer.setRate(velocidad);
        } else if ((int) deltaT < 0 && velocidad <= 1.1) {
            velocidad += 0.04;
            mediaPlayer.setRate(velocidad);
        }
    }

    private void iniciarReproduccionNormal() {
        isPlaying = true;
        velocidad = 1;
        mediaPlayer.setRate(velocidad);
        if (mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            mediaPlayer.play(); // Inicia la reproducción solo si no está reproduciendo
        }
    }

}
