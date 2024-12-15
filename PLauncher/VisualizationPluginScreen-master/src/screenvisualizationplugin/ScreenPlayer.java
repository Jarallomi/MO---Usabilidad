package screenvisualizationplugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import mo.visualization.Playable;

public class ScreenPlayer implements Playable {

    private long start;
    private long end;
    private boolean isSync = false;
    private boolean isPlaying = false;
    private static Panel screenPanel;
    private String path;
    private MediaPlayer mediaPlayer;
    private String id;

    private static final Logger logger = Logger.getLogger(ScreenPlayer.class.getName());

    /*public ScreenPlayer(File file, String id) {
        this.id = id;
        Platform.runLater(() -> initialize(file));
    }

    private void initialize(File file) {
        screenPanel = new Panel(file);
        mediaPlayer = screenPanel.getMP();
        path = file.getAbsolutePath();
        String path2 = path.substring(0, path.lastIndexOf(".")) + "-temp.txt";
        String cadena;
        
        try (FileReader f = new FileReader(path2); BufferedReader b = new BufferedReader(f)) {
            if ((cadena = b.readLine()) != null) {
                start = Long.parseLong(cadena);
            }
            if ((cadena = b.readLine()) != null) {
                end = Long.parseLong(cadena);
            }
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        Stage stage = new Stage();
        stage.setTitle(id);
        stage.setScene(new Scene(screenPanel, 800, 600)); 
        stage.show();
    }*/
    
    public ScreenPlayer(File file, String id) {
        initialize(file); // Llamada directa al método de inicialización
    }

    private void initialize(File file) {
        screenPanel = new Panel(file); // Instancia el panel principal
        mediaPlayer = screenPanel.getMP(); // Obtiene el MediaPlayer desde el panel
        path = file.getAbsolutePath();
        String path2 = path.substring(0, path.lastIndexOf(".")) + "-temp.txt";

        try (FileReader f = new FileReader(path2); BufferedReader b = new BufferedReader(f)) {
            start = Long.parseLong(b.readLine());
            end = Long.parseLong(b.readLine());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error al leer tiempos de inicio y fin", ex);
        }
    }
    
    public Panel getPaneNode() {
        return screenPanel;
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
                if (millis >= start && millis <= end) {
                    if (isSync) {
                        if ((millis - start) % 90 == 0) {
                            if ((millis - start) < mediaPlayer.getCurrentTime().toMillis()) {
                                if (isPlaying) {
                                    mediaPlayer.pause();
                                    isPlaying = false;
                                }
                            } else {
                                if (!isPlaying) {
                                    mediaPlayer.play();
                                    isPlaying = true;
                                }
                            }
                        }
                    } else {
                        if (!isPlaying) {
                            mediaPlayer.play();
                            isPlaying = true;
                        }
                    }
                } else {
                    mediaPlayer.stop();
                }
            });
        } else {
            logger.warning("MediaPlayer is not initialized yet.");
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
        isSync = bln;
    }
}
