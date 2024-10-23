package mo.core;

import java.io.File;

public class Utils {
    public static String getBaseFolder(){
        File baseFolder = 
                new File(
                        Utils.class.getProtectionDomain()
                                .getCodeSource().getLocation().getPath());
        baseFolder = new File(baseFolder.getParent());
        if (baseFolder.getPath().endsWith("classes")){
            baseFolder = new File (baseFolder.getParent());
        }
        return baseFolder.getPath();
    }
}
