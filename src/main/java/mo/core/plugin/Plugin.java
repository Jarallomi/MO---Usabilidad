package mo.core.plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Plugin {
    
    public final static Logger LOGGER = Logger.getLogger(Plugin.class.getName());

    private String id;//classname
    private String version;
    
    private String name;
    private String description;
    
    private Object instance;

    private List<Dependency> dependencies;
    
    private Boolean thirdParty = null;
    
    private Path path;
    
    private String website;
    
    private String author;
    
    private String contact;
    
    private Class<?> clazz;
    
    public Plugin(){
        
    }
    
    public boolean isThirdParty(){
        
        if(this.thirdParty != null){
            return this.thirdParty;
        }
        
        for(Package p : Package.getPackages()) {
            if(p.getName().startsWith("mo.")){            
                // p.getName() is a MO package
                if(getId().startsWith(p.getName()+".")){
                    this.thirdParty = false;
                    return this.thirdParty;
                }
            }
        }        
        this.thirdParty = true;
        return this.thirdParty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setWebsite(String website){
        this.website = website;
    }
    
    public String getWebsite(){
        return this.website;
    }

    public String getVersion() {
        return version;
    }
    
    public String getAuthor(){
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getContact(){
        return this.contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Dependency> getDependencies() {
        if (dependencies == null)
            dependencies = new ArrayList<>();
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }
    
    public void addDependency(Dependency dependecy){
        if (dependencies == null)
            dependencies = new ArrayList<>();
        this.dependencies.add(dependecy);
    }
    
    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        System.out.println("set path de plugin ("+this.getName()+"): " + path.toString());
        this.path = path;
    }
    
    public void setPath(String pathStr){
        if(pathStr == null){
            this.path = null;
        } else {
            this.path = Paths.get(pathStr);
        }        
    }
    
    
    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
    
    public Object getInstance() {
        if (instance == null) {
            try {
                instance = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return instance;
    }
    
    public Object getNewInstance() {
        Object newInstance = null;
        
        try {
            newInstance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        
        return newInstance;
    }
    
    
    
    public boolean sanityCheck(){
        
        if(getDependencies() == null) return false;
        
        for(Dependency dep : getDependencies()){
        
            if(dep == null) return false;
            
            if(dep.getExtensionPoint() == null) return false;
        
        }        
        return true;
    }
    
    
    
    @Override
    public boolean equals(Object o){        
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Plugin)) return false;        
        return this.id == ((Plugin) o).id && this.version == ((Plugin) o).version;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.id);
        hash = 67 * hash + Objects.hashCode(this.version);
        return hash;
    }

    
    
    @Override
    public String toString(){
        String result = "";
        result += "id: " + id + ", ";
        result += "version: " + version + ", ";
        result += "name: " + name + ", ";
        result += "description: " + description + ", ";
        result += "path: " + path + ", ";
        result += "clazz: " + clazz + ", ";
        result += "dependencies: [";
        for (Dependency dep : getDependencies()) {
            result += dep.getId() + " " + dep.getVersion() + 
                    " " + dep.isPresent() + ", ";
        }
        result = result.substring(0, result.length()-2);
        result += "]";
        return result;
    }

}
