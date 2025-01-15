package screenvisualizationplugin;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.util.Duration;
import javafx.scene.layout.BorderPane;
import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Panel extends BorderPane {

    private File MEDIA_URL;
    private Media media;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private static final Logger logger = Logger.getLogger(Panel.class.getName());

    public Panel(File file) {
        MEDIA_URL = file;
        initializePlayer();
    }

    private void initializePlayer() {
        if (MEDIA_URL == null || !MEDIA_URL.exists()) {
            logger.log(Level.SEVERE, "Error initializing media player: File not found or is null");
            return;
        }

        try {
            media = new Media(MEDIA_URL.toURI().toURL().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaView = new MediaView(mediaPlayer);

            mediaPlayer.setOnReady(() -> {
                DoubleProperty width = mediaView.fitWidthProperty();
                DoubleProperty height = mediaView.fitHeightProperty();
                width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
                height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
            });

            setCenter(mediaView); 
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "Error initializing media player: Malformed URL", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error initializing media player", e);
        }
    }

    public MediaPlayer getMP() {
        return mediaPlayer;
    }
}
