package vispluginbitalino;

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

public class BitalinoVisConfig implements VisualizableConfiguration {

        
    private String id;
    private int sensor;
    BitalinoPlayer player;
    List<File> files;
    private final String[] creators1 = {"capturepluginbitalino.BitalinoRecorder0",
        "capturepluginbitalino.BitalinoRecorder3",
    "capturepluginbitalino.BitalinoRecorder4",
    "capturepluginbitalino.BitalinoRecorder6"};
    
    
    private final String[] creators2 = {"capturepluginbitalino.BitalinoRecorder1",
    "capturepluginbitalino.BitalinoRecorder3",
    "capturepluginbitalino.BitalinoRecorder5",
    "capturepluginbitalino.BitalinoRecorder6"};
    
    private final String[] creators3 = {"capturepluginbitalino.BitalinoRecorder2",
    "capturepluginbitalino.BitalinoRecorder4",
    "capturepluginbitalino.BitalinoRecorder5",
    "capturepluginbitalino.BitalinoRecorder6"};
    

    public BitalinoVisConfig() {
        files = new ArrayList<>();
    }
    
    public BitalinoVisConfig(String id) {
        this();
        this.id = id;
    }
    
   @Override
    public String getId() {
        return id;
    }

    @Override
    public File toFile(File parent) {
        File f = new File(parent, "bitalino-visualization_"+id+"-"+sensor+".xml");
        try {
            f.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(BitalinoVisConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        return f;
    }

    @Override
    public Configuration fromFile(File file) {
        String fileName = file.getName();

        if (fileName.contains("_") && fileName.contains(".")) {
            String name = fileName.substring(fileName.indexOf("_")+1, fileName.lastIndexOf("-"));
            String sen= fileName.substring(fileName.lastIndexOf("-")+1, fileName.lastIndexOf("."));
            BitalinoVisConfig config = new BitalinoVisConfig();
            config.id = name;
            config.sensor = Integer.parseInt(sen);
            return config;
        }
        return null;
    }

    @Override
    public List<String> getCompatibleCreators() {
        switch (sensor) {
            case 1:
                return Arrays.asList(creators1);
            case 2:
                return Arrays.asList(creators2);
            default:
                return Arrays.asList(creators3);            
        }
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public void setSensor(int sensor){
        this.sensor = sensor;
    }
    
    private void ensurePlayerCreated() {
        if (player == null && !files.isEmpty()) {
            player = new BitalinoPlayer(files.get(0),sensor,id);
        }
    }

    @Override
    public void addFile(File file) {
        if ( !files.contains(file) ) {
            this.files.add(file);
            player = new BitalinoPlayer(files.get(files.size()-1),sensor,id);
        }
    }

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
