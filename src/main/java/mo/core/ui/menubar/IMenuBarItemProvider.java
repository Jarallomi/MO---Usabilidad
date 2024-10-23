package mo.core.ui.menubar;

import javax.swing.JMenuItem;

import mo.core.plugin.ExtensionPoint;

@ExtensionPoint
public interface IMenuBarItemProvider {
    JMenuItem getItem();
    int getRelativePosition();
    String getRelativeTo();
}
