package mo.mouse.visualization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.JFXPanel;
import mo.organization.Configuration;
import mo.visualization.Playable;
import mo.visualization.VisualizableConfiguration;

public class MouseVisConfiguration implements VisualizableConfiguration {
    
    private String id;
    private List<String> compatibleCreators;
    private List<File> files;
    private MousePlayer player;
    

    public MouseVisConfiguration() {
        compatibleCreators = new ArrayList<>();
        compatibleCreators.add("mo.mouse.capture.MouseRecorder");
        files = new ArrayList<>();
    }

    public MouseVisConfiguration(String id) {
        this(); 
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public File toFile(File parent) {
        File f = new File(parent, "mouse-visualization_" + id + ".xml");
        try {
            f.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(MouseVisConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
        return f;
    }

    @Override
    public Configuration fromFile(File file) {
        String fileName = file.getName();

        if (fileName.contains("_") && fileName.contains(".")) {
            String name = fileName.substring(fileName.indexOf("_") + 1, fileName.lastIndexOf("."));
            return new MouseVisConfiguration(name);
        }
        return null;
    }

    @Override
    public List<String> getCompatibleCreators() {
        return compatibleCreators;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void addFile(File file) {
        if (!files.contains(file)) {
            this.files.add(file);
        }
    }

    @Override
    public void removeFile(File file) {
        files.remove(file);
    }

    private void ensurePlayerCreated() {
        if (player == null && !files.isEmpty()) {
            player = new MousePlayer(files.get(0));
        }
    }

    @Override
    public Playable getPlayer() {
        ensurePlayerCreated();
        return player;
    }
}
