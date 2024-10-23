package mo.core.filemanagement;

import javax.swing.JMenuItem;
import mo.core.plugin.ExtensionPoint;

@ExtensionPoint
public interface PopupOptionProvider {
    JMenuItem getPopupItem();
    
    /**
     * @return "/" for all file types, null for folders, extension for specific files
     * examples: "", "pdf", "txt", etc.
     */
    String getExtension();
}
