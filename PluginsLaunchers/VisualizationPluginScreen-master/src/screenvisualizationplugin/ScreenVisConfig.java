package screenvisualizationplugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mo.organization.Configuration;
import mo.visualization.Playable;
import mo.visualization.VisualizableConfiguration;

public class ScreenVisConfig implements VisualizableConfiguration {

        
    private String id;
    ScreenPlayer player;
    List<File> files;
    private final String[] creators = {"screencaptureplugin.ScreenRecorder"};
    

    public ScreenVisConfig() {
        files = new ArrayList<>();
    }
    
    public ScreenVisConfig(String id) {
        this(); 
        this.id = id;
    }
    
   @Override
    public String getId() {
        return id;
    }

    @Override
    public File toFile(File parent) {
        File f = new File(parent, "screen-visualization_"+id+".xml");
        try {
            f.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(ScreenVisConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        return f;
    }

    @Override
    public Configuration fromFile(File file) {
        String fileName = file.getName();

        if (fileName.contains("_") && fileName.contains(".")) {
            String name = fileName.substring(fileName.indexOf("_")+1, fileName.lastIndexOf("."));
            ScreenVisConfig config = new ScreenVisConfig();
            config.id = name;
            return config;
        }
        return null;
    }

    @Override
    public List<String> getCompatibleCreators() {
       return Arrays.asList(creators);
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    private void ensurePlayerCreated() {
        if (player == null && !files.isEmpty()) {
            player = new ScreenPlayer(files.get(0),id);
        }
    }

    @Override
    public void addFile(File file) {
        if ( !files.contains(file) ) {
            this.files.add(file);
            player = new ScreenPlayer(files.get(files.size()-1),id);
        }
    }

    @Override
    public Playable getPlayer() {
        ensurePlayerCreated();
        return player;
    }
    
    @Override
    public void removeFile(File file) {
        File toRemove = null;
        
        for (File f : files) {
            if (f.equals(file)) {
                toRemove = f;
            }
        }
        
        if (toRemove != null) {
            files.remove(toRemove);
        }
    }
    
}
