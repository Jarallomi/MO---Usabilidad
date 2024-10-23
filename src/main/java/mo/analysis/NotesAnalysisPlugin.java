package mo.analysis;

import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.organization.Configuration;
import mo.organization.ProjectOrganization;
import mo.organization.StagePlugin;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import mo.analysis.NotesConfigDialog;
import mo.analysis.NotesAnalysisConfig;
import java.util.logging.Level;
import java.util.logging.Logger;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;

@Extension(
    xtends = {
        @Extends(extensionPointId = "mo.analysis.AnalysisProvider")
    }
)
public class NotesAnalysisPlugin implements AnalysisProvider {

    private final static String PLUGIN_NAME = "Notes plugin";
    private List<Configuration> configurations; 
    public final static Logger logger = Logger.getLogger(NotesAnalysisPlugin.class.getName());

    public NotesAnalysisPlugin() {
    	configurations = new ArrayList<>();
    }

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public Configuration initNewConfiguration(ProjectOrganization organization) {
    	NotesConfigDialog d = new NotesConfigDialog();
        
        if (d.showDialog()) {
            NotesAnalysisConfig c = new NotesAnalysisConfig();
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
                NotesAnalysisPlugin mc = new NotesAnalysisPlugin();
                XElement root = XIO.readUTF(new FileInputStream(file));
                XElement[] pathsX = root.getElements("path");
                for (XElement pathX : pathsX) {
                    String path = pathX.getString();
                    NotesAnalysisConfig c = new NotesAnalysisConfig();
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
        File file = new File(parent, "notes-analysis.xml");
        if (!file.isFile()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        XElement root = new XElement("analysis");
        for (Configuration config : configurations) {
            File p = new File(parent, "notes-analysis");
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