package mo.core.ui.menubar;

import javax.swing.JMenuItem;

public interface MenuItem {
    public JMenuItem getItem();
    public int getPosition();
    public String getRelativeTo();
}
