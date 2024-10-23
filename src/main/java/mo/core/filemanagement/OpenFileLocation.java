package mo.core.filemanagement;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import mo.core.I18n;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;

@Extension(
    xtends = {
        @Extends(
            extensionPointId = "mo.core.filemanagement.PopupOptionProvider"
        )
    }
)
public class OpenFileLocation implements PopupOptionProvider {

    private JMenuItem item;
    private I18n i18n;

    public OpenFileLocation() {
        i18n = new I18n(OpenFileLocation.class);
        item = new JMenuItem(i18n.s("OpenFileLocation.open"));
        item.setName("openLocation");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = (File) item.getClientProperty("file");
                if (file != null) {
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop != null) {
                        try {
                            desktop.open(file.getParentFile());
                        } catch (IOException ex) {
                            Logger.getLogger(OpenFileLocation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
    }
    
    
    
    @Override
    public JMenuItem getPopupItem() {
        return item;
    }

    @Override
    public String getExtension() {
        return "/";
    }
    
}
