package mo.core.ui.dockables;

import mo.core.plugin.ExtensionPoint;

@ExtensionPoint
public interface IDockableElementProvider {
    public DockableElement getElement();
    public String getDockableGroup();
}
