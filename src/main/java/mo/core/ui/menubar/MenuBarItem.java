package mo.core.ui.menubar;

import javax.swing.JMenuItem;

public class MenuBarItem {
    private JMenuItem menuItem;
    private int location;
    private String relativeTo;

    public MenuBarItem(JMenuItem menuItem, int location, String relativeTo) {
        this.menuItem = menuItem;
        this.location = location;
        this.relativeTo = relativeTo;
    }
    
    public JMenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(JMenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public String getRelativeTo() {
        return relativeTo;
    }

    public void setRelativeTo(String relativeTo) {
        this.relativeTo = relativeTo;
    }
    
    
}
