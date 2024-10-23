package mo.core.ui.dockables;

import java.io.File;

public interface StorableDockable {
    File dockableToFile();
    DockableElement dockableFromFile(File file);
}
