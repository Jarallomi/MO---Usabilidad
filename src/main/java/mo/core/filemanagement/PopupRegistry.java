package mo.core.filemanagement;

import java.awt.Component;
import java.awt.Container;
import mo.core.filemanagement.project.ProjectOptionProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;
import static mo.core.filemanagement.project.ProjectUtils.isProjectFolder;

public final class PopupRegistry {
    private static PopupRegistry popupRegistry;
    
    private final List<JMenuItem> projectOptions;
    
    private HashMap <String, List<JMenuItem>> options; //by extension

    
    private PopupRegistry() {
        options = new HashMap<>();
        projectOptions = new ArrayList<>();
        
        for (Plugin p : PluginRegistry.getInstance().getPluginData().getPluginsFor("mo.core.filemanagement.PopupOptionProvider")) {
            PopupOptionProvider option = (PopupOptionProvider) p.getInstance();
            addPopupOptionFor(option.getPopupItem(), option.getExtension());
        }
        
        for (Plugin p : PluginRegistry.getInstance().getPluginData().getPluginsFor("mo.core.filemanagement.project.ProjectOptionProvider")) {
            addPopupOptionForProjects(((ProjectOptionProvider) p.getInstance()).getOption());
        }
    }
    
    public void addPopupOptionForProjects(JMenuItem item) {
        projectOptions.add(item);
    }
    
    private void printJMenuItem(JMenuItem i, String indent) {
        for (MenuElement subElement : i.getSubElements()) {
  
        }
    }
    
    public void addPopupOptionFor(JMenuItem itemToAdd, String fileExtension) {
        if (!options.containsKey(fileExtension))
            options.put(fileExtension, new ArrayList());
        
        List<JMenuItem> items = options.get(fileExtension);
        
        if (items != null) {
            items.add(itemToAdd);
        }
    }
    
    private void putPropertyInChildrenComponents(MenuElement[] menus, Object key, Object value) {
        for (MenuElement menu : menus) {
            if (menu instanceof JComponent) {

                ((JMenuItem) menu).putClientProperty(key, value);
                putPropertyInChildrenComponents(menu.getSubElements(), key, value);
            }
        }
    }
    
    private void printTree(Component c, String indent) {

        if (c instanceof Container) {
            for (Component component : ((Container) c).getComponents()) {
                printTree(component, indent+" ");
            }
        }
    }
    
    private void printSubElements(Object o, String indent) {
        if (o instanceof MenuElement) {

            MenuElement e = (MenuElement) o;
            for (MenuElement subElement : e.getSubElements()) {
                printSubElements(subElement, indent+" ");
            }
        }
    }
    
    private void setFileProperty(Object object, Object key, Object value) {
        
        if (object instanceof JComponent) {
            ((JComponent) object).putClientProperty(key, value);
        }
        
        if (object instanceof MenuElement) {
            MenuElement e = (MenuElement) object;
            for (MenuElement subElement : e.getSubElements()) {
                setFileProperty(subElement, key, value);
            }
        }
    }
    
    JPopupMenu getPopupFor(File file) {
        JPopupMenu popup = new JPopupMenu();

        if (isProjectFolder(file)) {
            addOptionsToPopup(popup, projectOptions);
        } else if (file.isDirectory()) {
            addOptionsToPopup(popup, options.get(null));
        } else {
            String extension = "";
            if (file.getName().contains(".")) {
                extension = file.getName().substring(file.getName().lastIndexOf(".")+1);
            }
            addOptionsToPopup(popup, options.get(extension));
        }
        
        addOptionsToPopup(popup, options.get("/"));
        
        setFileProperty(popup, "file", file);
        return popup;
    }
    
    private void addOptionsToPopup(JPopupMenu popup, List<JMenuItem> options) {
        if (popup != null && options != null) {
            for (JMenuItem option : options) {
                popup.add(option);
            }
        }
    }
    
    public static PopupRegistry getInstance() {
        if (popupRegistry == null) {
            popupRegistry = new PopupRegistry();
        }
        
        return popupRegistry;
    }
}
