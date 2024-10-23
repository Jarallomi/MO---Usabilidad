package mo.organization;

import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;
import mo.core.filemanagement.project.ProjectOptionProvider;

@Extension(
    xtends = {
        @Extends(
                extensionPointId = "mo.core.filemanagement.project.ProjectOptionProvider"
        )
    }
)
public class Organization implements ProjectOptionProvider {

    JMenu organizationMenu;

    public Organization() {
        organizationMenu = new JMenu("Organization");
        
        List<Plugin> plugins = PluginRegistry.getInstance()
                .getPluginData()
                .getPluginsFor("mo.organization.OrganizationVisualizationMenuItemProvider");
        
        for (Plugin plugin : plugins) {     
            JMenuItem item = ((OrganizationVisualizationMenuItemProvider) plugin.getNewInstance()).getMenuItem();
            organizationMenu.add(item);
        }
        
        
    }

    @Override
    public JMenu getOption() {
        return organizationMenu;
    }
}
