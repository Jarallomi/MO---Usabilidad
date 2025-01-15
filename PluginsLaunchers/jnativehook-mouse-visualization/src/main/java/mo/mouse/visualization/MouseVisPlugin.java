package mo.mouse.visualization;

import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.Stage;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.organization.Configuration;
import mo.organization.ProjectOrganization;
import mo.organization.StagePlugin;
import mo.visualization.VisualizationProvider;

@Extension(
        xtends = {
            @Extends(extensionPointId = "mo.visualization.VisualizationProvider")
        }
)
public class MouseVisPlugin implements VisualizationProvider {
    
    private final static String PLUGIN_NAME = "Mouse Visualization";
    
    private List<Configuration> configurations;
    
    private static final Logger logger = Logger.getLogger(MouseVisPlugin.class.getName());

    public MouseVisPlugin() {
        configurations = new ArrayList<>();
    }
    
    

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public Configuration initNewConfiguration(ProjectOrganization organization) {
        MouseVisConfigDialog d = new MouseVisConfigDialog(organization);
        
        Stage stage = new Stage();
        if (d.showDialog(stage)) {
            MouseVisConfiguration c = new MouseVisConfiguration();
            c.setId(d.getConfigurationName());
            configurations.add(c);
            return c;
        }
        
        return null;
    }

    @Override
    public List<Configuration> getConfigurations() {
        return configurations;
    }

    @Override
    public StagePlugin fromFile(File file) {
        if (file.isFile()) {
            try {
                MouseVisPlugin mc = new MouseVisPlugin();
                XElement root = XIO.readUTF(new FileInputStream(file));
                XElement[] pathsX = root.getElements("path");
                for (XElement pathX : pathsX) {
                    String path = pathX.getString();
                    MouseVisConfiguration c = new MouseVisConfiguration();
                    Configuration config = c.fromFile(new File(file.getParentFile(), path));
                    if (config != null) {
                        mc.configurations.add(config);
                    }
                }
                return mc;
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    @Override
    public File toFile(File parent) {
        File file = new File(parent, "mouse-visualization.xml");
        if (!file.isFile()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        XElement root = new XElement("vis");
        for (Configuration config : configurations) {
            File p = new File(parent, "mouse-visualization");
            p.mkdirs();
            File f = config.toFile(p);

            XElement path = new XElement("path");
            Path parentPath = parent.toPath();
            Path configPath = f.toPath();
            path.setString(parentPath.relativize(configPath).toString());
            root.addElement(path);
        }
        try {
            XIO.writeUTF(root, new FileOutputStream(file));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return file;
    }
    
}
