package mo.core.plugin;

public class Dependency {
    private String id;
    private String version;
    private boolean present;
    
    private ExtPoint extensionPoint;

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

    public boolean isPresent() {
        return present;
    }

    public void setIsPresent(boolean isPresent) {
        this.present = isPresent;
    }
    
    public void setExtensionPoint(ExtPoint x) {
        this.extensionPoint = x;
    }
    
    public ExtPoint getExtensionPoint() {
        return this.extensionPoint;
    }
}
