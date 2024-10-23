package mo.organization;

import java.io.File;
import java.util.List;

public interface StagePlugin {
    String getName();
    Configuration initNewConfiguration(ProjectOrganization organization);
    List<Configuration> getConfigurations();
    StagePlugin fromFile(File file);
    File toFile(File parent);
}
