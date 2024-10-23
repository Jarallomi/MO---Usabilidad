package mo.core.filemanagement.project;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JMenuItem;
import mo.core.I18n;
import mo.core.filemanagement.FileRegistry;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.core.ui.dockables.DockablesRegistry;

@Extension(
    xtends = {
        @Extends(
                extensionPointId = "mo.core.filemanagement.project.ProjectOptionProvider"
        )
    }
)
public class ProjectCloser implements ProjectOptionProvider {

    private final JMenuItem item;
    private I18n i18n;

    public ProjectCloser() {
        i18n = new I18n(ProjectCloser.class);
        
        item = new JMenuItem();
        item.setText(i18n.s("ProjectCloser.close"));
        item.setName("closeProject");
        
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                if (source instanceof JMenuItem) {
                    JMenuItem s = (JMenuItem) source;
                    Object fileObj = s.getClientProperty("file");
                    if (fileObj != null) {
                        File file = (File) fileObj;
                        DockablesRegistry.getInstance()
                                .closeDockableByGroup(file.getAbsolutePath());
                        FileRegistry.getInstance().closeProject(file);
                    }
                }
            }
        });
        
        
    }
    
    
    
    @Override
    public JMenuItem getOption() {
        return item;
    }
    
}
