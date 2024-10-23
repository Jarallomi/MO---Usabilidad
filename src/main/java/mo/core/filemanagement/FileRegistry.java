package mo.core.filemanagement;

import mo.core.filemanagement.project.Project;
import java.io.File;
import java.util.TreeMap;
import mo.core.ui.dockables.DockablesRegistry;

public class FileRegistry {

    private static FileRegistry fileRegistry;

    private final TreeMap<String, String> openedFiles; //<path, name>

    private FilesPane filesPane;

    private FileRegistry() {
        openedFiles = new TreeMap<>();
    }

    public TreeMap<String, String> getOpenedFiles() {
        return openedFiles;
    }

    public synchronized static FileRegistry getInstance() {
        if (fileRegistry == null) {
            fileRegistry = new FileRegistry();
        }
        return fileRegistry;
    }

    public void addOpenedProject(Project project) {

        File f = project.getFolder();
        if (!openedFiles.containsKey(f.getAbsolutePath())) {
            openedFiles.put(
                    f.getAbsolutePath(),
                    f.getName());

            filesPane.addFile(new File(f.getAbsolutePath()));

            File docks = new File(f, "dockables.xml");
            if (docks.exists()) {
                DockablesRegistry.getInstance().loadDockablesFromFile(new File(f, "dockables.xml"));
            }
        }
    }
    
    public void closeProject(File projectFolder) {
        if (openedFiles.containsKey(projectFolder.getAbsolutePath())) {
            openedFiles.remove(projectFolder.getAbsolutePath());
            filesPane.closeFile(projectFolder);
        }
    }

    public FilesPane getFilesPane() {
        return filesPane;
    }

    public void setFilesPane(FilesPane filesPane) {
        this.filesPane = filesPane;
    }

    public void unWatch(File folder) {
        filesPane.getFilesTreeModel().getDirectoryWatcher().removeDirectory(folder.toPath());
    }
}
