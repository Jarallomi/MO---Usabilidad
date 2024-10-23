package mo.core.filemanagement;

import mo.core.filemanagement.project.Project;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import mo.core.I18n;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.IDockableElementProvider;
import mo.core.ui.dockables.StartupDockableProvider;
import mo.core.ui.dockables.StorableDockable;
import static mo.core.Utils.getBaseFolder;

@Extension(
        xtends = {
            @Extends(extensionPointId = "mo.core.ui.dockables.IDockableElementProvider"),
            @Extends(extensionPointId = "mo.core.ui.dockables.StartupDockableProvider")
        }
)
public class FilesPane extends DockableElement implements IDockableElementProvider, StorableDockable, StartupDockableProvider {

    private final static Logger logger = Logger.getLogger(FilesPane.class.getName());
    private final FilesTreeModel filesTreeModel;
    private final JTree filesTree;
    private I18n i18n;
    public FilesPane() {
        i18n = new I18n(FilesPane.class);
        setTitleText(i18n.s("FilesPane.files"));
        filesTreeModel = new FilesTreeModel();
        
        JPopupMenu popup = new JPopupMenu();
        JMenuItem mi = new JMenuItem("Insert a children");
        mi.setActionCommand("insert");
        popup.add(mi);
        mi = new JMenuItem("Remove this node");
        mi.setActionCommand("remove");
        popup.add(mi);
        popup.setOpaque(true);
        popup.setLightWeightPopupEnabled(true);
        
        filesTree = new JTree(filesTreeModel) {
            @Override
            public String convertValueToText(Object value, boolean selected,
                    boolean expanded, boolean leaf, int row, boolean hasFocus) {
                return super.convertValueToText(((File) value).getName(), selected, expanded, leaf, row, hasFocus);
            }
        };
        
        filesTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        filesTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {
                
                if (SwingUtilities.isRightMouseButton(event) /*&& event.isPopupTrigger()*/) {
                    int row = filesTree.getRowForLocation(event.getX(), event.getY());
                    
                    if (row == -1 || row == 0) {
                        return;
                    }
                    
                    
                    
                    filesTree.setSelectionRow(row);
                    File selected = (File) filesTree.getLastSelectedPathComponent();
                    
                    JPopupMenu pop = PopupRegistry.getInstance().getPopupFor(selected);

                    if (pop != null) {
                        pop.show((JComponent) event.getSource(),
                                event.getX(), event.getY());
                    }
                }
            }
        });
        JScrollPane filesScrollPane = new JScrollPane(filesTree);

        //filesTree.setRootVisible(false); //TODO why is not working when there are no files to show
        filesTree.setShowsRootHandles(true);
        
        add(filesScrollPane);
    }
    
    @Override
    public DockableElement getElement() {
        return this;
    }
    
    public JTree getFilesTree() {
        return filesTree;
    }
    
    public FilesTreeModel getFilesTreeModel() {
        return filesTreeModel;
    }
    
    void addFile(File file) {
        if (file.exists()) {
            filesTreeModel.addFile(file);
            
            if (!filesTree.isExpanded(0)) 
                filesTree.expandRow(0);
            
        } else {
        }
    }
    
    public void closeFile(File file) {
        if (file.exists()) {
            if (filesTreeModel.contains(file)) {
                filesTreeModel.removeFile(file);
            }
        }
    }
    
    @Override
    public String getDockableGroup() {
        return null;
    }
    
    @Override
    public File dockableToFile() {
        File result = null;
        try {
            result = new File(getBaseFolder(), "files.xml");
            result.createNewFile();
            
            XElement xmlContent = new XElement("filesPane");
            XElement openedProjects = new XElement("openedProjects");
            
            for (File file : filesTreeModel.getFiles()) {
                XElement project = new XElement("project");
                project.setString(file.getAbsolutePath());
                openedProjects.addElement(project);
            }
            
            xmlContent.addElement(openedProjects);
            
            XIO.writeUTF(xmlContent, new FileOutputStream(result));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    @Override
    public DockableElement dockableFromFile(File file) {
        FilesPane d = new FilesPane();
        if (file.exists()) {
            try (InputStream in = new FileInputStream(file)) {
                FileRegistry.getInstance().setFilesPane(d);
                
                XElement x = XIO.readUTF(in);

                XElement openedProjects = x.getElement("openedProjects");
                for (XElement project : openedProjects.getElements("project")) {
                   
                    File p = new File(project.getString());
                    if (p.exists()){
                    //d.addFile(new File(project.getString()));
                        FileRegistry.getInstance().addOpenedProject(new Project(project.getString()));
                    }
                }

                
                return d;
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        } else {
            logger.log(Level.INFO, "File <"+file.getAbsolutePath()+"> does not exists");
        }
        return d;
    }

    @Override
    public DockableElement getDockable() {
        return dockableFromFile(new File(getBaseFolder(), "files.xml"));
    }
}
