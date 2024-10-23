package mo.core.plugin;

public class VersionUtils {
    public static String semverize(String version) {
        String[] v = version.split("\\.");

        if (v.length == 1)
            version += ".0.0";
        else if (v.length == 2)
            version += ".0";
        
        return version;
    }
}
