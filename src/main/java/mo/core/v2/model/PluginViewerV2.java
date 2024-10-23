/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.core.v2.model;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.util.Enumeration;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import mo.core.plugin.Dependency;
import mo.core.plugin.ExtPoint;
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;
import mo.core.plugin.gui.LocalPluginInstaller;
import mo.core.plugin.gui.PluginList;
import mo.core.plugin.gui.RemotePluginInstaller;
import mo.core.ui.GridBConstraints;
import mo.core.ui.Utils;
import static mo.core.ui.menubar.MenuItemLocations.UNDER;

/**
 *
 * @author Francisco
 */
public class PluginViewerV2 extends JPanel {
    
    JTree pluginsTree, extPointsTree;
    LocalPluginInstaller localInstaller;
    RemotePluginInstaller remoteInstaller;
    PluginList pluginList;
    JTabbedPane tabbedPane = new JTabbedPane();
    public JPanel mainPanel, buttonsPanel;
    PluginViewerV2.PluginCellRenderer renderer = new PluginViewerV2.PluginCellRenderer();
    private JScrollPane pluginsScrollPane, extPointScrollPane;
    
    public PluginViewerV2(){
        // Plugin list tab
        pluginList = new PluginList();
        tabbedPane.addTab("Installed plugins", pluginList);
        
        // Plugin installer tab
        localInstaller = new LocalPluginInstaller();
        remoteInstaller = new RemotePluginInstaller();
        JTabbedPane installer = new JTabbedPane();
        installer.addTab("Local", new JScrollPane(localInstaller));
        installer.addTab("Download", new JScrollPane(remoteInstaller));
        tabbedPane.addTab("Get plugins", installer);

        // Plugins tab
        populatePluginsTree();
        pluginsScrollPane = new JScrollPane(pluginsTree);
        tabbedPane.addTab("Plugins", pluginsScrollPane);
        pluginsTree.expandRow(2);
        
        // Extension points tab
        populateExtensionPointTree();
        extPointScrollPane = new JScrollPane(extPointsTree);
        tabbedPane.addTab("Extension Points", extPointScrollPane);
        
        tabbedPane.addChangeListener((ChangeEvent e) -> {
            /* Event - tab change */
            pluginList.update();
            
        });

        
        TreeNode r = (TreeNode) pluginsTree.getModel().getRoot();
        expandAll(pluginsTree, new TreePath(r));
        r = (TreeNode) extPointsTree.getModel().getRoot();
        expandAll(extPointsTree, new TreePath(r));

        
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout()); 
        
        GridBConstraints c = new GridBConstraints();
        c.f(GridBConstraints.HORIZONTAL).gx(0).gy(0).wx(1).wy(0.1);
        
       
        c.f(GridBConstraints.BOTH);
        mainPanel.add(tabbedPane, c.gy(1).wy(1));
    }
    
    private void populateExtensionPointTree() {
        extPointsTree = new JTree(new DefaultMutableTreeNode("Extension Points"));
        extPointsTree.setCellRenderer(renderer);
        DefaultTreeModel modelX = (DefaultTreeModel) extPointsTree.getModel();
        List<ExtPoint> extensionPoints = PluginRegistry.getInstance().getPluginData().getExtPoints();
        DefaultMutableTreeNode rootX = (DefaultMutableTreeNode) modelX.getRoot();
        for (ExtPoint extPoint : extensionPoints) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(extPoint);
            rootX.add(node);
            for (Plugin plugin : extPoint.getPlugins()) {
                DefaultMutableTreeNode d = new DefaultMutableTreeNode(plugin);
                node.add(d);
            }
        }
    }
    
    private void populatePluginsTree() {
        pluginsTree = new JTree(new DefaultMutableTreeNode("Plugins"));
        pluginsTree.setCellRenderer(renderer);

        DefaultTreeModel model = (DefaultTreeModel) pluginsTree.getModel();
        List<Plugin> plugins = PluginRegistry.getInstance().getPluginData().getPlugins();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        for (Plugin plugin : plugins) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(plugin);
            root.add(node);
            for (Dependency dependency : plugin.getDependencies()) {
                DefaultMutableTreeNode d = new DefaultMutableTreeNode(dependency);
                node.add(d);
            }
        }
        pluginsTree.updateUI();
    }
    
    //OJO
    public int getRelativePosition() {
        return UNDER;
    }

    //OJO
    public String getRelativeTo() {
        return "plugins";
    }
    
    private void expandAll(JTree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path);
            }
        }
        tree.expandPath(parent);
        // tree.collapsePath(parent);
    }
    
    private static class PluginCellRenderer implements TreeCellRenderer {

        private final DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

        private Icon interfaceIcon = Utils.createImageIcon("images/interface.png", getClass());
        
        private Icon pluginIcon = Utils.createImageIcon("images/plugin.png", getClass());

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            DefaultTreeCellRenderer returnValue = (DefaultTreeCellRenderer) defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
                Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                if (userObject instanceof Plugin) {
                    returnValue = getTreeCellRendererComponentForPlugin(returnValue, (Plugin) userObject);
                } else if (userObject instanceof ExtPoint) {
                    returnValue = getTreeCellRendererComponentForExtPoint(returnValue, (ExtPoint) userObject);
                } else if (userObject instanceof Dependency) {
                    Dependency d = (Dependency) userObject;
                    if (d.isPresent()) {
                        returnValue = getTreeCellRendererComponentForExtPoint(returnValue, d.getExtensionPoint());
                    } else {
                        returnValue = getTreeCellRendererComponentForDependency((DefaultTreeCellRenderer) (new DefaultTreeCellRenderer()).getTreeCellRendererComponent(tree, value, leaf, expanded, leaf, row, hasFocus), d);
                    }
                }
            }

            return returnValue;
        }

        private DefaultTreeCellRenderer getTreeCellRendererComponentForPlugin(DefaultTreeCellRenderer c, Plugin o) {
            c.setText(o.getName());
            c.setIcon(pluginIcon);
            return c;
        }

        private DefaultTreeCellRenderer getTreeCellRendererComponentForExtPoint(DefaultTreeCellRenderer c, ExtPoint o) {
            c.setText(o.getName());
            c.setIcon(interfaceIcon);
            return c;
        }

        private DefaultTreeCellRenderer getTreeCellRendererComponentForDependency(DefaultTreeCellRenderer c, Dependency o) {
            c.setForeground(Color.red);
            c.setBackgroundSelectionColor(Color.white);
            c.setText(o.getId());
            return c;
        }
    }
}
