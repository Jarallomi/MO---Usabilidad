package mo.visualization;

import bibliothek.util.xml.XAttribute;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mo.core.I18n;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;
import mo.organization.ProjectOrganization;
import mo.organization.StageAction;
import mo.organization.StagePlugin;
import mo.organization.StageModule;

@Extension(
    xtends = {
        @Extends(
                extensionPointId = "mo.organization.StageModule"
        )
    }
)
public class VisualizationStage implements StageModule {
    
    private List<StagePlugin> plugins;
    private List<StageAction> actions;
    private ProjectOrganization organization;
    private I18n i18n;
    
    private static final String CODENAME = "visualization";
    
    private static final Logger logger 
            = Logger.getLogger(VisualizationStage.class.getName());

    public VisualizationStage() {
        i18n = new I18n(VisualizationStage.class);
        
        plugins = new ArrayList<>();
        actions = new ArrayList<>();
        
        for (Plugin plugin : PluginRegistry.getInstance().getPluginData().getPluginsFor("mo.visualization.VisualizationProvider")) {
            VisualizationProvider p = (VisualizationProvider) plugin.getNewInstance();
            plugins.add(p);
        }
        
        VisualizeAction va = new VisualizeAction();
        actions.add(va);
        
        //TODO make actions plugables
        //for (Plugin plugin : PluginRegistry.getInstance().getPluginsFor("mo.visualization."))
    }
    
    @Override
    public String getCodeName() {
        return CODENAME;
    }

    @Override
    public String getName() {
        return i18n.s("VisualizationStage.visualization");
    }

    @Override
    public List<StagePlugin> getPlugins() {
        return plugins;
    }

    @Override
    public StageModule fromFile(File file) {
        if (file.exists()) {
            try {
                XElement root = XIO.readUTF(new FileInputStream(file));
                XElement[] pluginsX = root.getElements("plugin");
                VisualizationStage visStage = new VisualizationStage();
                for (XElement pluginX : pluginsX) {
                    String clazzStr = pluginX.getAttribute("class").getString();
                    String path = pluginX.getElement("path").getString();
                    File ff = new File(file.getParentFile(), path);
                    Class<?> clazz = PluginRegistry.getInstance().getClassForName(clazzStr);
                    Object o = clazz.newInstance();

                    Method method = clazz.getDeclaredMethod("fromFile", File.class);

                    VisualizationProvider p 
                            = (VisualizationProvider) method.invoke(o, ff);
                    if (p != null) {

                        visStage.addOrReplaceStagePlugin(p);
                    }
                }

                return visStage;
            } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    
    private void addOrReplaceStagePlugin(StagePlugin p) {
        ArrayList<StagePlugin> pluginsToReplace = new ArrayList<>();
        for (StagePlugin plugin : plugins) {
            if (plugin.getName().equals(p.getName())) {
                pluginsToReplace.add(plugin);
            }
        }
        plugins.removeAll(pluginsToReplace);
        plugins.add(p);
    }

    @Override
    public File toFile(File parent) {
        try {
            File visStageFile = new File(parent, "visualization.xml");
            visStageFile.createNewFile();
            
            XElement xElem = new XElement("visualization");
            XElement name = new XElement("name");
            name.setString(getName());
            xElem.addElement(name);
            
            for (StagePlugin plugin : plugins) {
                if ( !plugin.getConfigurations().isEmpty()) {
                    File p = new File(parent, "visualization");
                    if (!p.isDirectory()) {
                        p.mkdirs();
                    }
                    File f = plugin.toFile(p);
                    System.out.println("*****F: " + f.getAbsolutePath());
                    if (f != null) {
                        XElement pluginX = new XElement("plugin");
                        XAttribute clazz = new XAttribute("class");
                        clazz.setString(plugin.getClass().getName());
                        pluginX.addAttribute(clazz);
                        Path filePath = parent.toPath();
                        Path selfPath = f.toPath();
                        Path relative = filePath.relativize(selfPath);
                        XElement path = new XElement("path");
                        path.setString(relative.toString());
                        pluginX.addElement(path);
                        xElem.addElement(pluginX);
                    }
                }
            }
            
            XIO.writeUTF(xElem, new FileOutputStream(visStageFile));
            
            return visStageFile;
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void setOrganization(ProjectOrganization org) {
        this.organization = org;
    }

    @Override
    public List<StageAction> getActions() {
        return actions;
    }
    
}
