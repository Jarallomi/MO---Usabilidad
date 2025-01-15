package webcamvisualizationplugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.DockablesRegistry;
import mo.visualization.Playable;

public class WCPlayerSwing implements Playable{

    private long start;
    private long end;
    //private ArrayList<Long> frames = new ArrayList<Long>();
    private boolean isSync=false;
    private boolean isPlaying=false;
    private static PanelCamaraSwing wcpanel; //CAMBIAR ESTO CUANDO SE QUIERA PASAR DE JAVAFX A SWING Y VICEVERSA 
    private String path;
    private int cont=0;
    private MediaPlayer mediaPlayer;
    private String id;
    

    private static final Logger logger = Logger.getLogger(WCPlayer.class.getName());

    public WCPlayerSwing(File file, String id) {
            this.id = id;
            wcpanel = new PanelCamaraSwing(file); //CAMBIAR ESTO CUANDO SE QUIERA PASAR DE JAVAFX A SWING Y VICEVERSA 
            mediaPlayer = wcpanel.getMP();
            Platform.setImplicitExit(false);
            path = file.getAbsolutePath();
            String path2 =  path.substring(0,path.lastIndexOf(".")) + "-temp.txt";
            String cadena;
            FileReader f;
            FileReader f2;
            try {
                f = new FileReader(path2);
                BufferedReader b = new BufferedReader(f);
                try {
                    if((cadena=b.readLine())!=null){
                        start = Long.parseLong(cadena);
                    }if((cadena=b.readLine())!=null){
                        end = Long.parseLong(cadena);
                    }  
                    b.close();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
            
            DockableElement e = new DockableElement();
            e.setTitleText(id);
            e.add(wcpanel);
            DockablesRegistry.getInstance().addAppWideDockable(e);                
    }
    
    @Override
    public void pause() {
        if(isPlaying){          
            mediaPlayer.pause();   
            isPlaying=false;
        }
    }

    @Override
    public void seek(long desiredMillis) {
        if(desiredMillis>=start && desiredMillis<=end){      
            new Thread(new Runnable() {

            @Override
            public void run() {
                    mediaPlayer.seek(Duration.millis(desiredMillis-start));  
            }
        }).start();
        }
        else if(desiredMillis<start){
            new Thread(new Runnable() {

            @Override
            public void run() {
                    mediaPlayer.seek(Duration.millis(0));  
            }
        }).start();
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
    public void play(long millis){
        if(millis>=start && millis<=end){  
            if(isSync){
                if((millis-start)%90==0){
                    if((millis-start)<mediaPlayer.getCurrentTime().toMillis()){
                        if(isPlaying){                            
                            mediaPlayer.pause();
                            isPlaying=false;
                        }
                    }
                    else{
                        if(!isPlaying){
                            mediaPlayer.play();
                            isPlaying=true;
                        }
                    }
                }
            }
            else{
                if(!isPlaying){               
                    isPlaying=true;
                    mediaPlayer.play();
                }
            }
        }
        else{    
            mediaPlayer.stop();
        }
    }

    @Override
    public void stop() {
        if(isPlaying){
            cont=0; 
            mediaPlayer.stop();
            isPlaying=false;
        }
    }    

    public void sync(boolean bln) {
        isSync=bln;
    }
}
