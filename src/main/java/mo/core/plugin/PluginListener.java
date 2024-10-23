package mo.core.plugin;

public interface PluginListener {
    
    /**
     * Invoked when a new plugin is added.
     */
    void pluginAdded(Plugin plugin);
    
    /**
     * Invoked when a plugin with the same mayor version is added.
     * Examples: 0.1.1 to 0.1.2, 5.0.1 to 5.1.0 
     */
    void pluginUpdated(Plugin plugin);
    
    /**
     * Invoked when a plugin is removed.
     */
    void pluginRemoved(Plugin plugin);
}
