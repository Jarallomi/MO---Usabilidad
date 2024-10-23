package mo.core.filemanagement;

import java.io.File;
import javax.swing.JPopupMenu;

public class FilePopupMenu extends JPopupMenu {
    File file;


    FilePopupMenu() {
        //setLightWeightPopupEnabled(false);
    }
    
    public void setFile(File file) {
        this.file = file;
    }
    
    public File getFile() {
        return file;
    }
}
