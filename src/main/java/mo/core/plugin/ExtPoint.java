package mo.core.plugin;

import java.util.ArrayList;
import java.util.List;

public class ExtPoint {
    
    private String id;
    private String version;
    
    private String name;
    private String description;
    
    List<Plugin> plugins;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public void addPlugin(Plugin p) {
        if (plugins == null) {
            plugins = new ArrayList<>();
        }
        plugins.add(p);
    }
    
    
    public void removePlugin(Plugin p){
        plugins.remove(p);
    }
    
    public List<Plugin> getPlugins() {
        if (plugins == null) {
            plugins = new ArrayList<>();
        }
        return plugins;
    }
    
    @Override
    public String toString(){
        String result = "";
        result += "id: " + id + ", ";
        result += "version: " + version + ", ";
        result += "name: " + name + ", ";
        result += "description: " + description + ", ";
        return result;
    }
}
