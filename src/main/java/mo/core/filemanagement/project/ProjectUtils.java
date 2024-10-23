package mo.core.filemanagement.project;

import java.io.File;
import java.io.FilenameFilter;

public class ProjectUtils {
    
    public static boolean isProjectFolder(File file) {
        if (file.isDirectory()) {
            FilenameFilter filter = 
                    (File dir, String name) -> name.equals("moproject.xml");
            
            String[] list = file.list(filter);
            
            return (list != null) && (list.length == 1);
        }
        return false;
    }
    
}
