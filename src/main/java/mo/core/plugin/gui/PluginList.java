package mo.core.plugin.gui;

import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;
import mo.core.plugin.IUpdatable;

/**
 *
 * @author felo
 */


public class PluginList extends JSplitPane implements IUpdatable {    
        
    class PluginTreeNode extends DefaultMutableTreeNode{
    
        private Plugin plugin;

        PluginTreeNode(Plugin plugin){
            super(plugin.getName());
            this.plugin = plugin;        
        }

        public Plugin getPlugin(){
            return this.plugin;
        }
    }

 
    
    private Plugin focusedPlugin = null;
    
    
    public PluginList(){
        
        super(JSplitPane.HORIZONTAL_SPLIT, new JPanel(), new JPanel());      
        
        PluginRegistry.getInstance().subscribePluginsChanges(this);

        showList();        
    }
    
    
    private void expandTree(JTree tree){        
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }        
    }
    
    
    private void showPluginInfo(Plugin plugin_){
        
        // check if plugin actually exists
        List<Plugin> plugins = PluginRegistry.getInstance().getPluginData().getPlugins();
        Plugin plugin = null;
        for(Plugin p : plugins){
            if(p.equals(plugin_)){
                plugin = p;
                break;
            }
        }

        this.setRightComponent(new PluginInfo(plugin, this));
        
    }
    
    
    private void showPluginError(Plugin plugin){
        
        this.setRightComponent(new PluginError(plugin, this));
        
    }
    
    
    private void showList(){
        
        List<Plugin> plugins = PluginRegistry.getInstance().getPluginData().getPlugins();
        
        
        DefaultMutableTreeNode allPlugins = new DefaultMutableTreeNode("Installed Plugins");
        DefaultMutableTreeNode hardCodedPlugins = new DefaultMutableTreeNode("MO");
        DefaultMutableTreeNode dynamicPlugins = new DefaultMutableTreeNode("Third party");
        
        for(Plugin p : plugins){
            
            PluginTreeNode node = new PluginTreeNode(p);
           
            if(p.isThirdParty()){
               dynamicPlugins.add(node);
            } else {
                hardCodedPlugins.add(node);
            }                     
        }
        
        JTree tree = new JTree(allPlugins);
        JScrollPane scroll = new JScrollPane(tree);
        
        
        tree.addTreeSelectionListener(new TreeSelectionListener(){
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                
                
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();
                if (node == null || !(node instanceof PluginTreeNode)) return;
                PluginTreeNode nodeData = (PluginTreeNode) node;
                
                focusedPlugin = nodeData.getPlugin();
                
                if(nodeData.getPlugin().sanityCheck()){
                    
                    
                    showPluginInfo(focusedPlugin);
                    
                    
                } else {
                    
                    showPluginError(focusedPlugin);
                    
                }
                
            }
        });
        
 
        this.setLeftComponent(scroll);
        
        if(!dynamicPlugins.isLeaf())
            allPlugins.add(dynamicPlugins); 

        if(!hardCodedPlugins.isLeaf())
            allPlugins.add(hardCodedPlugins);
        
        
       
        tree.addMouseListener(new UpdateRightClick(this));
        
            
        expandTree(tree);
        this.getLeftComponent().revalidate();
        
    }

    @Override
    public void update() {
        showList();
        if(focusedPlugin != null && !PluginRegistry.getInstance().getPluginData().pluginIsRegistered(focusedPlugin)){
            this.setRightComponent(new JPanel());
        }       
        
    }
    
}
