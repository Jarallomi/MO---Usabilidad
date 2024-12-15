package webcamvisualizationplugin;

import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class PanelCamara extends BorderPane {

    private File MEDIA_URL;
    private Media media;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    public long end;

    public PanelCamara(File file) {
        pane p = new pane(file);
        setCenter(p);
    }

    public class pane extends Group {

        public pane(File file) {
            MEDIA_URL = file;

            try {
                media = new Media(MEDIA_URL.toURI().toURL().toString());
            } catch (MalformedURLException ex) {
                Logger.getLogger(PanelCamara.class.getName()).log(Level.SEVERE, "Error al cargar el archivo multimedia", ex);
            }

            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnReady(() -> end = (long) media.getDuration().toMillis());

            mediaView = new MediaView(mediaPlayer);
            getChildren().add(mediaView);

            mediaView.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    mediaView.fitWidthProperty().bind(newScene.widthProperty());
                    mediaView.fitHeightProperty().bind(newScene.heightProperty());
                }
            });
        }
    }

    public MediaPlayer getMP() {
        return mediaPlayer;
    }
}
