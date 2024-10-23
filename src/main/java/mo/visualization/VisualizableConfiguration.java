package mo.visualization;

import java.io.File;
import java.util.List;
import mo.organization.Configuration;

public interface VisualizableConfiguration extends Configuration {
    List<String> getCompatibleCreators();
    void addFile(File file);
    void removeFile(File file);
    Playable getPlayer();
}
