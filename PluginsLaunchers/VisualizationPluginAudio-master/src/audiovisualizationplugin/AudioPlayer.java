package audiovisualizationplugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.DockablesRegistry;
import mo.visualization.Playable;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.media.MediaPlayer;
import static javafx.scene.media.MediaPlayer.Status.STOPPED;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AudioPlayer implements Playable {
    
    private Stage stage;
    private long start;
    private long end;
    private boolean isPlaying = false;
    private boolean isPaused = true;
    private Panel ap;
    private String path;
    private Reproductor r;
    private boolean isSync;
    ResourceBundle dialogBundle = ResourceBundle.getBundle("properties/principal");

    private static final Logger logger = Logger.getLogger(AudioPlayer.class.getName());

    public AudioPlayer(File file) {
        r = new Reproductor(file);
        ap = new Panel(file, r);
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
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        
        /*Platform.runLater(() -> {
          
            if (stage==null){
                
            DockableElement e = new DockableElement();
            e.setTitleText(dialogBundle.getString("title_player"));
            stage = new Stage();
            stage.setTitle(dialogBundle.getString("title_player"));
            stage.initStyle(StageStyle.DECORATED);  
            Scene scene = new Scene(ap, 700, 400);  
            stage.setScene(scene);
            stage.show();

            DockablesRegistry.getInstance().addAppWideDockable(e);
            }
        });*/

    }
    
    public Panel getPanel() {
        return ap;
    }
        
        
    public Node getPaneNode() {
        return ap;
    }


    @Override
    public void seek(long desiredMillis) {
        if (desiredMillis >= start && desiredMillis <= end) {
            r.current(desiredMillis - start);
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
    
    public MediaPlayer.Status getStatus() {
        return r.getStatus(); // Donde 'r' es la instancia de Reproductor
    }

    
    
    //Method play before the 2024 update, this work with the original version of MO in the whole thread of the application
    /*@Override
    public void play(long millis) {
        if(millis>=start && millis <end){
            new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if((millis-start)%50==0){
                            ap.play(millis-start,end-start);
                        }
                        if(isSync){
                            if(start-millis==0 ||r.getStatus()!=STOPPED){
                                r.play(millis-start,end-start,isSync);                                
                            }
                        }
                        if(!isSync && !isPlaying){
                            r.play(millis-start, end-start,isSync);
                        }
                        isPlaying=true;                    
                    }
                }).start();
        }
        else{
            r.stop();
            isPlaying=false;
        }
    }*/
    
    @Override
    public void play(long millis) {
        if (millis >= start && millis < end) {
            if (!isPlaying || r.getStatus() == MediaPlayer.Status.STOPPED || r.getStatus() == MediaPlayer.Status.READY) {
                new Thread(() -> {
                    ap.play(millis - start, end - start); 
                    r.play(millis - start, end - start, isSync); 
                    isPlaying = true;
                    isPaused = false;
                }).start();
            }
        } else {
            stop(); 
        }
    }

    @Override
    public void pause() {
        if (isPlaying) {
            ap.pause();
            isPlaying = false;
            isPaused = true;
            r.pause();
        }
    }

    @Override
    public void stop() {
        if (isPlaying) {
            ap.stop();
            r.stop();
            isPlaying = false;
        }
    }

    public void sync(boolean bln) {
        isSync = bln;
    }
}
