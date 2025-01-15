package vispluginbitalino;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.util.logging.Logger;
import javafx.scene.Node;
import mo.visualization.Playable;

public class BitalinoPlayer implements Playable {

    private long start;
    private long end;
    private Panel ap;
    private static final Logger logger = Logger.getLogger(BitalinoPlayer.class.getName());

    public BitalinoPlayer(File file, int sensor, String id) {
        ap = new Panel(file, sensor);
        start = ap.start;
        end = ap.end;
        
        /*Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle(id);
            stage.setScene(new Scene(ap, 802, 540));
            stage.show();
        });*/
    }

    @Override
    public void pause() {
        ap.pause();
    }
    
    public Node getPaneNode(){
        return ap;
    }

    @Override
    public void seek(long desiredMillis) {
        ap.play((int) (desiredMillis - start));
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
        ap.play((int) (millis - start));
    }
    
    @Override
    public void stop() {
        ap.stop();
    }

    public void sync(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
