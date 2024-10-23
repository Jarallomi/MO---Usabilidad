package mo.core.plugin;

import com.github.zafarkhaja.semver.Version;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author felo
 */
public class PluginData {
    
    private final List<Plugin> plugins;

    private final List<ExtPoint> extensionPoints;
    
    private final HashMap<String, List<PluginListener>> pluginListeners;
    
    private final static Logger logger
            = Logger.getLogger(PluginRegistry.class.getName());
    
    PluginData(){
        
        plugins = new ArrayList<>();
        extensionPoints = new ArrayList<>();
        pluginListeners = new HashMap<>();
    }
    
    public void addPluginListener(String extensionPointId, PluginListener listener) {
        if (!pluginListeners.containsKey(extensionPointId)) {
            ArrayList<PluginListener> xpListeners = new ArrayList<>();
            xpListeners.add(listener);
            pluginListeners.put(extensionPointId, xpListeners);
        } else {
            pluginListeners.get(extensionPointId).add(listener);
        }       
    }
    
    public Plugin getPluginByPath(String path){        
        for(Plugin plugin : plugins){      
            
            if(plugin == null) continue;
            if(plugin.getPath() == null) continue;
           
            if(path.equals(plugin.getPath().toString())){
                return plugin;
            }
        }
        return null;        
    }

    public void removePluginListener(String extensionPointId, PluginListener listener) {
        if (pluginListeners.containsKey(extensionPointId)) {
            List l = pluginListeners.get(extensionPointId);
            if (l.contains(listener)) {
                l.remove(l);
            }
        }
    }
    
    public void checkDependencies() {
        for (Plugin plugin : plugins) {
            for (Dependency dependency : plugin.getDependencies()) {
                for (ExtPoint extensioPoint : extensionPoints) {
                    if (dependency.getId().equals(extensioPoint.getId())) {
                        dependency.setExtensionPoint(extensioPoint);
                        dependency.setIsPresent(true);
                        
                        addPluginToExtensionPoint(extensioPoint, plugin);
                        break;
                    }
                }
            }
        }
    }
    
    private void addPluginToExtensionPoint(ExtPoint x, Plugin p) {
        for (Plugin plugin : x.getPlugins()) {
            if (plugin.getId().equals(p.getId())) {
                return;
            }
        }
        x.addPlugin(p);
    }
    
    public void addExtensionPoint(ExtPoint extPoint) {
        for (ExtPoint extensionPoint : extensionPoints) {
            if (extensionPoint.getId().equals(extPoint.getId())) {
                return;
            }
        }
        extensionPoints.add(extPoint);
    }    
    
    
    public List<ExtPoint> getExtPoints() {
        return extensionPoints;
    }
    
    public Object getPluginInstance(String pluginId) {
        for (Plugin plugin : plugins) {
            if (pluginId.equals(plugin.getId())) {
                return plugin.getInstance();
            }
        }
        return null;
    }

    public Plugin getPlugin(String pluginId) {
        for (Plugin plugin : plugins) {
            if (pluginId.equals(plugin.getId())) {
                return plugin;
            }
        }
        return null;
    }
        
    
    private void unregisterPlugin(String pluginId, String version){
        
        Plugin plugin = null;
        
        for(Plugin p : plugins){
            if(p.getId().equals(pluginId) && p.getVersion().equals(version)){
                plugin = p;
                break;
            }
        }
        
        if(plugin == null){
            logger.log(Level.INFO, "Plugin with Id <{0}> (version " + version + ") not found. It cannot be removed.", pluginId);
            return;
        }
        
        for(Dependency dependency : plugin.getDependencies()){           
            ExtPoint extpt = dependency.getExtensionPoint();           
            extpt.removePlugin(plugin);
        }
        
        plugins.remove(plugin);        
    }
    
    public void unregisterPlugin(Plugin plugin){
        if(plugin == null)
            return;        
        unregisterPlugin(plugin.getId(), plugin.getVersion());
    }
    
    public void unregisterPlugin(String pluginId){       
        
        List<Plugin> sameId = new ArrayList<>();
        
        for(Plugin p : plugins){
            if(p.getId().equals(pluginId)){
                sameId.add(p);                
            }
        }
        
        
        for(Plugin p : sameId){
            System.out.println(p.getId());
            unregisterPlugin(p);
        }
        
    }
    
    public List<Plugin> getPlugins() {
        return this.plugins;
    }
    
    public List<Plugin> getPluginsFor(String extensionPointId) {
        return getPluginsFor(extensionPointId, ">=0.0.0");
    }

    public List<Plugin> getPluginsFor(String extensionPointId, String version) {
        List<Plugin> result = new ArrayList<>();
        
        String xpId = null;
        for (ExtPoint extensionPoint : extensionPoints) {
            if (extensionPoint.getId().equals(extensionPointId)) {
                Version v = Version.valueOf(extensionPoint.getVersion());
                if (v.satisfies(version)) {
                    xpId = extensionPoint.getId();
                }
            }
        }
        
        if (xpId == null) {
            logger.log(Level.INFO, 
                    "Id for extension point <{0}> not found", 
                    extensionPointId );
            return result;
        }
            
        
        for (Plugin plugin : plugins) {
            for (Dependency dependency : plugin.getDependencies()) {
                if (dependency.getId().equals(extensionPointId)) {
                    result.add(plugin);
                    break;
                }
            }
        }

        return result;
    }
    
    
    public boolean pluginIsRegistered(Plugin plugin, String version){
        for(Plugin p : plugins){
            if(p.equals(plugin)){                
                Version v = Version.valueOf(p.getVersion());
                if(v.satisfies(version)){
                    return true;
                }                
            }            
        }
        return false;
    }
    
    public boolean pluginIsRegistered(Plugin plugin){  
        
        
        String version = plugin.getVersion();
        if(version == null || version.length() == 0){
            version = ">=0.0.0";
        }
        
        return pluginIsRegistered(plugin, version);
    }
    
    
    public void addPlugin(Plugin plugin) {
        int toReplace = -1;
        String newId = plugin.getId();
        Version newVersion = Version.valueOf(plugin.getVersion());

        for (int i = 0; i < plugins.size(); i++) {

            Version v = Version.valueOf(plugins.get(i).getVersion());

            if (newId.equals(plugins.get(i).getId())
                    && newVersion.getMajorVersion() == v.getMajorVersion()
                    && newVersion.getMinorVersion() == v.getMinorVersion()) {
                toReplace = i;

            }
        }

        if (toReplace > -1) {
            plugins.set(toReplace, plugin);
            for (Dependency dependency : plugin.getDependencies()) {
                if (pluginListeners.containsKey(dependency.getId())) {
                    for (PluginListener pluginListener : pluginListeners.get(dependency.getId())) {
                        pluginListener.pluginUpdated(plugin);
                    }
                }
            }
        } else {
            plugins.add(plugin);
            for (Dependency dependency : plugin.getDependencies()) {
                if (pluginListeners.containsKey(dependency.getId())) {
                    for (PluginListener pluginListener : pluginListeners.get(dependency.getId())) {
                        pluginListener.pluginAdded(plugin);
                    }
                }
            }
        }

    }
    
    
}
