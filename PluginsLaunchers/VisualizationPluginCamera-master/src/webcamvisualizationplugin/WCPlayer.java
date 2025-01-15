package webcamvisualizationplugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import mo.visualization.Playable;

public class WCPlayer implements Playable {

    private long start;
    private long end;
    private boolean isPlaying = false;
    private PanelCamara wcpanel; //CAMBIAR ESTO CUANDO SE QUIERA PASAR DE JAVAFX A SWING Y VICEVERSA
    private MediaPlayer mediaPlayer;
    private static final Logger logger = Logger.getLogger(WCPlayer.class.getName());

    /*public WCPlayer(File file, String id) {
        Platform.runLater(() -> initialize(file, id));
    }

    private void initialize(File file, String id) {
        wcpanel = new PanelCamara(file); //CAMBIAR ESTO CUANDO SE QUIERA PASAR DE JAVAFX A SWING Y VICEVERSA
        mediaPlayer = wcpanel.getMP();

        String path = file.getAbsolutePath();
        String path2 = path.substring(0, path.lastIndexOf(".")) + "-temp.txt";

        try (BufferedReader b = new BufferedReader(new FileReader(path2))) {
            start = Long.parseLong(b.readLine());
            end = Long.parseLong(b.readLine());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error al leer tiempos de inicio y fin", ex);
        }

        Stage stage = new Stage();
        stage.setTitle(id);
        stage.setScene(new Scene(wcpanel, 800, 600));
        stage.show();
    } */
    
    public WCPlayer(File file, String id) {
        initialize(file, id); 
    }

    private void initialize(File file, String id) {
        wcpanel = new PanelCamara(file);
        mediaPlayer = wcpanel.getMP();

        String path = file.getAbsolutePath();
        String path2 = path.substring(0, path.lastIndexOf(".")) + "-temp.txt";

        try (BufferedReader b = new BufferedReader(new FileReader(path2))) {
            start = Long.parseLong(b.readLine());
            end = Long.parseLong(b.readLine());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error al leer tiempos de inicio y fin", ex);
        }
    }
    
    public PanelCamara getPaneNode() {
        return wcpanel; 
    }

    @Override
    public void pause() {
        if (isPlaying && mediaPlayer != null) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    @Override
    public void seek(long desiredMillis) {
        if (mediaPlayer != null) {
            Platform.runLater(() -> {
                if (desiredMillis >= start && desiredMillis <= end) {
                    mediaPlayer.seek(Duration.millis(desiredMillis - start));
                } else if (desiredMillis < start) {
                    mediaPlayer.seek(Duration.ZERO);
                }
            });
        }
    }

    @Override
    public long getStart() {
        return start;
    }

    @Override
    public long getEnd() {
        return end;
    }

    @Override
    public void play(long millis) {
        if (mediaPlayer != null) {
            Platform.runLater(() -> {
                if (!isPlaying) {
                    mediaPlayer.seek(Duration.millis(millis - start)); // Posicionar en el tiempo inicial
                    mediaPlayer.play();
                    isPlaying = true;
                }
            });
        }
    }

    @Override
    public void stop() {
        if (isPlaying && mediaPlayer != null) {
            mediaPlayer.stop();
            isPlaying = false;
        }
    }
    
    public void sync(boolean bln) {
        boolean isSync = bln;
    }
}
