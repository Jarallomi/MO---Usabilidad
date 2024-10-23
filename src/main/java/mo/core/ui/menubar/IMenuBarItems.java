package mo.core.ui.menubar;

import java.util.List;
import mo.core.plugin.ExtensionPoint;

@ExtensionPoint
public interface IMenuBarItems {
    List<MenuBarItem> getItems();
}
