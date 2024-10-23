package mo.core.ui.menubar;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;

@Extension(
        xtends = {
            @Extends (
                  extensionPointId = "mo.core.ui.menubar.IMenuBarItemNO!"
            )
        }
)
public class IMenuBarItemImpl implements IMenuBarItemProvider {

    @Override
    public JMenuItem getItem() {
        return new JMenu("Hola");
    }

    @Override
    public int getRelativePosition() {
        return -1;
    }

    @Override
    public String getRelativeTo() {
        return "window";
    }
    
}
