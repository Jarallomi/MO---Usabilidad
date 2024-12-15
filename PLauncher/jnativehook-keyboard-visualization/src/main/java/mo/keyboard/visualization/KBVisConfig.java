package mo.keyboard.visualization;

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

public class KBVisConfig implements VisualizableConfiguration {

    
    private final String[] creators = {"mo.keyboard.capture.KeyboardRecorder"};
    
    private List<File> files;
    private String id;
    private KeyboardPlayer player;
    
    private static final Logger logger = Logger.getLogger(KBVisConfig.class.getName());
    private boolean stopped;

    public KBVisConfig() {
        files = new ArrayList<>();
    }
    
    public KBVisConfig(String id) {
        this(); 
        this.id = id;
    }
    
  
    public void setId(String id) {
        this.id = id;
    }
    
    @Override
    public List<String> getCompatibleCreators() {
        return Arrays.asList(creators);
    }

    @Override
    public void addFile(File file) {
        if (!files.contains(file)) {
            files.add(file);
        }
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

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public File toFile(File parent) {
        File f = new File(parent, "keyboard-visualization_"+id+".xml");
        try {
            f.createNewFile();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return f;
    }

    @Override
    public Configuration fromFile(File file) {
        String fileName = file.getName();

        if (fileName.contains("_") && fileName.contains(".")) {
            String name = fileName.substring(fileName.indexOf("_")+1, fileName.lastIndexOf("."));
            KBVisConfig config = new KBVisConfig();
            config.id = name;
            return config;
        }
        return null;
    }

    private void ensurePlayerCreated() {
        if (player == null && !files.isEmpty()) {
            player = new KeyboardPlayer(files.get(0));
        }
    }

    @Override
    public Playable getPlayer() {
        ensurePlayerCreated();
        return player;
    }
}
