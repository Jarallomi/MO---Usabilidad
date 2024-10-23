package mo.core.filemanagement;

import javax.swing.JMenuItem;

public class PopupItem {
    JMenuItem item;
    int position;
    
    public JMenuItem getItem() {
        return item;
    }
    
    public int getPosition() {
        return position;
    }
    
    public String getPositionRelative() {
        return null;
    }
}
