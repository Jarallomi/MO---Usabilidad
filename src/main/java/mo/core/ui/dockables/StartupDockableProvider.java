package mo.core.ui.dockables;

import mo.core.plugin.ExtensionPoint;

@ExtensionPoint
public interface StartupDockableProvider {
    DockableElement getDockable();
}
