package mo.core;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import mo.core.ui.dockables.DockablesRegistry;
import mo.core.ui.menubar.MenuBar;
import static mo.core.Utils.getBaseFolder;
import mo.core.filemanagement.FileRegistry;
import mo.core.filemanagement.FilesPane;

public class MainWindow extends JFrame {

    public MainWindow() {
        //setLookAndFeel();
    }

    public void createAndShowGUI() {
        
        setTitle("Multimodal Observer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(MenuBar.getInstance());
        
        DockablesRegistry dr = DockablesRegistry.getInstance();
        dr.setJFrame(this);
        add(dr.getControl().getContentArea());
        
        File f = new File(getBaseFolder(), "files.xml");
        if (!f.exists()) {
            FilesPane pane = new FilesPane();
            FileRegistry.getInstance().setFilesPane(pane);
            dr.addAppWideDockable(pane);
        }

        dr.loadDockablesFromFile(new File(Utils.getBaseFolder(), "dockables.xml"));
        
        //FileRegistry.getInstance().setFilesPane((FilesPane) de);

        pack();
        setVisible(true);
        setVisible(false);
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | 
                IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainWindow.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}
