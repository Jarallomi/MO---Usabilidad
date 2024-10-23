package mo.core.plugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import mo.core.plugin.Plugin;

/**
 *
 * @author felo
 */

class PluginError extends JPanel{
    
    public PluginError(Plugin plugin, PluginList treeList){
        
        JLabel pluginTitle = new JLabel(plugin.getName(), SwingConstants.LEFT);
        pluginTitle.setFont(new Font("", Font.BOLD, 20));
        Dimension d = pluginTitle.getPreferredSize();
        d.height = 25;
        pluginTitle.setPreferredSize(d); 
        
        JLabel pluginVersion = new JLabel("v" + plugin.getVersion(), SwingConstants.LEFT);
        JPanel top = new JPanel();

        top.add(pluginTitle);
        top.add(pluginVersion);        
        
        JPanel content = new JPanel();
        
        String errorMsg = "Plugin " + plugin.getName() + " (" + plugin.getId() + ") is corrupted.";
        
        content.add(new JLabel(errorMsg));        
        
        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);        
        add(content, BorderLayout.CENTER);

        
    } 
    
    
}
