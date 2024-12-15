package webcamvisualizationplugin;

import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class PanelCamaraSwing extends JFXPanel {

    private File MEDIA_URL;
    private Media media;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    public long end;

    public PanelCamaraSwing(File file) {
        Group root = new Group();
        setLayout(null); 
        MEDIA_URL = file;

        try {
            media = new Media(MEDIA_URL.toURI().toURL().toString());
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnReady(() -> end = (long) media.getDuration().toMillis());

            mediaView = new MediaView(mediaPlayer);
            DoubleProperty width = mediaView.fitWidthProperty();
            DoubleProperty height = mediaView.fitHeightProperty();
            width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
            height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

            root.getChildren().add(mediaView);
            Scene scene = new Scene(root, 800, 600); 
            setScene(scene);

        } catch (MalformedURLException ex) {
            Logger.getLogger(PanelCamara.class.getName()).log(Level.SEVERE, "Error al cargar el archivo multimedia", ex);
        }
    }

    public MediaPlayer getMP() {
        return mediaPlayer;
    }
}
