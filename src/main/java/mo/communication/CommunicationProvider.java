package mo.communication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import mo.core.plugin.ExtensionPoint;
import mo.organization.Configuration;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;

@ExtensionPoint
public interface CommunicationProvider {
    String getName();
    String getProtocolUsed();
    ArrayList<String> getDevices();
    Configuration initNewConfiguration(String ip,int port, String device);
    List<Configuration> getConfigurations();
    File toFile(File parent);
}
