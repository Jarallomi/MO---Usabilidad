package mo.core.preferences;

//import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.File;
import java.util.HashSet;
import java.util.Locale;

@XStreamAlias("preferences")
public class AppPreferencesWrapper {
    
    @XStreamAlias("projects")
    private HashSet<AppProjectPreferencesWrapper> openedProjects;
    
    @XStreamAlias("plugins")
    private HashSet<String> pluginFolders;
    
    private int frameX = 450;
    
    private int frameY = 200;
    
    private int frameWidth = 300;
    
    private int frameHeight = 300;
    
    private String server = "http://mo-plugin-repository.herokuapp.com";
    
    private String localeCountry = Locale.getDefault().getCountry();
    
    private String localeLanguage = Locale.getDefault().getLanguage();
    
    private String localeVariant = Locale.getDefault().getVariant();
    //@XStreamAlias("")
    
    public AppPreferencesWrapper(){
    }
    
    public HashSet<AppProjectPreferencesWrapper> getOpenedProjects(){
        if (openedProjects == null)
            openedProjects = new HashSet<>();
        
        return this.openedProjects;
    }
    
    public void addOpenedProject(String projectAbsPath){
        if (openedProjects == null)
            openedProjects = new HashSet<>();
        
        if (!projectIsInOpenedProjects(projectAbsPath))
            openedProjects.add(new AppProjectPreferencesWrapper(projectAbsPath));
    }
    
    private boolean projectIsInOpenedProjects(String projectAbsPath) {
        for (AppProjectPreferencesWrapper openedProject : openedProjects) {
            if (openedProject.getLocation().equals(projectAbsPath))
                return true;
        }
        return false;
    }
    
    public void removeOpenedProject(String projectAbsPath){
        if (openedProjects == null)
            openedProjects = new HashSet<>();
        
        AppProjectPreferencesWrapper projectToRemove = null;
        
        for (AppProjectPreferencesWrapper project : openedProjects) {
            if (project.getLocation().compareTo(projectAbsPath) == 0)
                projectToRemove = project;
        }
        
        openedProjects.remove(projectToRemove);
    }

    public HashSet<String> getPluginFolders() {
        return pluginFolders;
    }

    public void setPluginFolders(HashSet<String> pluginFolders) {
        this.pluginFolders = pluginFolders;
    }

    public void addPluginFolder(String folder){
        if (pluginFolders==null)
            pluginFolders = new HashSet<>();
        
        if (!pluginFolders.contains(folder))
            pluginFolders.add(folder);
    }

    public int getFrameX() {
        return frameX;
    }

    public void setFrameX(int frameX) {
        this.frameX = frameX;
    }

    public int getFrameY() {
        return frameY;
    }

    public void setFrameY(int frameY) {
        this.frameY = frameY;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }

    public String getLocaleCountry() {
        return localeCountry;
    }

    public String getLocaleLanguage() {
        return localeLanguage;
    }

    public void setLocaleCountry(String localeCountry) {
        this.localeCountry = localeCountry;
    }

    public void setLocaleLanguage(String localeLanguage) {
        this.localeLanguage = localeLanguage;
    }

    public void setLocaleVariant(String localeVariant) {
        this.localeVariant = localeVariant;
    }

    public String getLocaleVariant() {
        return localeVariant;
    }
    
    public void setServer(String s){
        this.server = s;
    }
    
    public String getServer(){
        return this.server;
    }
     
    
    public static void main(String [] args){
        XStream x = new XStream();
        
        x.processAnnotations(AppPreferencesWrapper.class);
        AppPreferencesWrapper a = new AppPreferencesWrapper();
        a.addOpenedProject("ruta");
        File f = new File("");
        a.addOpenedProject(f.getAbsolutePath() );
        a.removeOpenedProject("ruta");
    }

    @Override
    public String toString() {
        String result = String.format("(%s, %s) %sx%s ", frameX, frameY, frameHeight, frameWidth);
        result += openedProjects;
        return result; //To change body of generated methods, choose Tools | Templates.
    }
    
    
}