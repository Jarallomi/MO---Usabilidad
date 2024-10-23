package mo.core.plugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import mo.core.plugin.Dependency;
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;

/**
 *
 * @author felo
 */
class PluginInfo extends JPanel {
    
    private final Plugin plugin;    
    
    private final PluginList treeList;
    
    private JPanel getInformationTab(){
      
        TupleList tuples = new TupleList();
        
        tuples.addTuple("Name", plugin.getName());
        
        if(plugin.isThirdParty() && plugin.getAuthor() != null && plugin.getAuthor().length() > 0) 
            tuples.addTuple("Author", plugin.getAuthor());
        
        if(plugin.isThirdParty() && plugin.getContact() != null && plugin.getContact().length() > 0) 
            tuples.addTuple("Contact", plugin.getContact());
        
        if(plugin.isThirdParty() && plugin.getWebsite() != null && plugin.getWebsite().length() > 0)
            tuples.addTuple("Website", new Link(plugin.getWebsite()));
        
        tuples.addTuple("Version", plugin.getVersion());
        tuples.addTuple("Id", plugin.getId());
        
        if(plugin.isThirdParty()){
            
            Path fullPath = plugin.getPath();            
            
            Path relative = PluginRegistry.getInstance().getPluginsFolder().relativize(fullPath);            
            
            String simplifiedPath = relative.toString();
            
            // Show only relative path
            JLabel path = new JLabel(simplifiedPath);
            
            // Set tooltip to show the full path
            path.setToolTipText(plugin.getPath().toString());
            tuples.addTuple("File", path);
        }
        
        tuples.addTuple("Source", plugin.isThirdParty()? "Third party" : "Built-in into MO");
        
        if(plugin.isThirdParty() && plugin.getDescription() != null && plugin.getDescription().length() > 0)
            tuples.addScrollText("Description", plugin.getDescription());          
        
        return tuples;
   
    }   
    
    
    
    private JPanel getOperations(){
        
        TupleList tuples = new TupleList();
        
        JButton uninstallBtn = new JButton("Uninstall");
        
        uninstallBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!plugin.isThirdParty())
                    return;

       
                int dialogResult = JOptionPane.showConfirmDialog (null, "Remove " + plugin.getName() + " permanently?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(dialogResult == JOptionPane.YES_OPTION){                                 
                    
                    System.out.println("ACA HAY QUE VER SI EL PLUGIN ESTA SIENDO USADO");
                    if(false){                        
                        JOptionPane.showMessageDialog(null, "Plugin is being used. Please stop recording data and try again.", "Error", JOptionPane.ERROR_MESSAGE);                    
                    }
                    
                    String success = PluginRegistry.getInstance().uninstallPlugin(plugin);
                    
                    if(success == PluginRegistry.PLUGIN_DELETED_OK){                        
                        System.out.println("Update plugin tree");
                        
                    }
                    else {                        
                        
                        String extraMsg = "";
                        if(success == PluginRegistry.FILE_CANNOT_BE_DELETED) extraMsg = "Unknown cause.";
                        if(success == PluginRegistry.FILE_IS_DIRECTORY) extraMsg = "File is a directory.";
                        if(success == PluginRegistry.FILE_NOT_FOUND) extraMsg = "File not found.";
                        if(success == PluginRegistry.PLUGIN_NOT_THIRD_PARTY_PLUGIN) extraMsg = "File is not a third party plugin.";
                        if(success == PluginRegistry.PLUGIN_BEING_USED) extraMsg = "Plugin is being used, stop recording and try again.";
 
                        JOptionPane.showMessageDialog(null, "Plugin couldn't be removed. " + extraMsg, "Error", JOptionPane.ERROR_MESSAGE);                    
                    }                                       
                    
                }
                
                treeList.update();
            }
        });
        
        
        
        tuples.addTuple("Uninstall plugin", uninstallBtn);
        
        
        
        return tuples;
    }
    

    
    private JPanel getAdvanced(){ 
        
        TupleList tuples = new TupleList();
        
        String[] depColumns = {"Id", "Version"};
        String[][] depRows = new String[plugin.getDependencies().size()][];
        
        
        for(int i=0; i<plugin.getDependencies().size(); i++){            
            Dependency dep = plugin.getDependencies().get(i);            
            depRows[i] = new String[]{dep.getId(),dep.getVersion()};
        }
        
        String[] extColumns = {"Name", "Id", "Version"};
        String[][] extRows = new String[plugin.getDependencies().size()][];        
        
        
        for(int i=0; i<plugin.getDependencies().size(); i++){            
            Dependency dep = plugin.getDependencies().get(i);         
            
            extRows[i] = new String[]{
                dep.getExtensionPoint().getName(),
                dep.getExtensionPoint().getId(),
                dep.getExtensionPoint().getVersion()};
        }

        JTable depTable = new JTable(depRows, depColumns);
        JTable extTable = new JTable(extRows, extColumns);
        
        depTable.setEnabled(false);
        extTable.setEnabled(false);
        
        tuples.addTuple("Dependencies", depTable);
        tuples.addTuple("Extension points", extTable);
                              
        return tuples;        
    }
    

    public PluginInfo(Plugin p, PluginList treeList) {
        
        this.treeList = treeList;
        this.plugin = p;

        
        Title pluginTitle = new Title(plugin.getName());
        
        JLabel pluginVersion = new JLabel("v" + plugin.getVersion(), SwingConstants.LEFT);
        JPanel top = new JPanel();
        

        top.add(pluginTitle);
        top.add(pluginVersion);        
        
        
        
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Information", new JScrollPane(getInformationTab()));
        
        
        if(plugin.isThirdParty()){
            tabbedPane.addTab("Operations", new JScrollPane(getOperations()));
        }
        
        tabbedPane.addTab("Advanced", new JScrollPane(getAdvanced()));
        
	

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);

        add(tabbedPane, BorderLayout.CENTER);
        
        
        
        

    }


}
