package mo.organization;

import java.io.File;
import java.util.List;
import mo.core.plugin.ExtensionPoint;

@ExtensionPoint
public interface StageModule {
    String getCodeName();
    String getName();
    List<StagePlugin> getPlugins();
    StageModule fromFile(File file);
    File toFile(File parent);
    void setOrganization(ProjectOrganization org);
    List<StageAction> getActions();
}
