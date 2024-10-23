package mo.analysis;

import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.organization.StageModule;
import mo.organization.StagePlugin;
import bibliothek.util.xml.XElement;
import java.util.ArrayList;
import java.util.List;
import mo.core.I18n;
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import bibliothek.util.xml.XAttribute;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mo.organization.ProjectOrganization;
import mo.organization.StageAction;
import mo.analysis.AnalyzeAction;

@Extension(
    xtends = {
        @Extends(
                extensionPointId = "mo.organization.StageModule"
        )
    }
)
public class AnalysisStage implements StageModule {
    private List<StagePlugin> plugins;
	private String CODENAME = "analysis";
    private I18n i18n;
    private ProjectOrganization organization;
    private static final Logger logger = Logger.getLogger(AnalysisStage.class.getName());
    private List<StageAction> actions;

    public AnalysisStage() {
        i18n = new I18n(AnalysisStage.class);
        
        plugins = new ArrayList<>();
        actions = new ArrayList<>();
        
        List<Plugin> listaDePlugins = PluginRegistry.getInstance().getPluginsFor("mo.analysis.AnalysisProvider");
        for (Plugin plugin : PluginRegistry.getInstance().getPluginsFor("mo.analysis.AnalysisProvider")) {
            AnalysisProvider p = (AnalysisProvider) plugin.getNewInstance();
            plugins.add(p);
        }

        AnalyzeAction aa = new AnalyzeAction();
        IndividualAnalisisAction a2 = new IndividualAnalisisAction();
        actions.add(aa);
        actions.add(a2);
    }

	@Override
    public String getCodeName() {
        return CODENAME;
    }

    @Override
    public String getName() {
        return i18n.s("AnalysisStage.analysis");
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
                AnalysisStage anStage = new AnalysisStage();
                for (XElement pluginX : pluginsX) {
                    String clazzStr = pluginX.getAttribute("class").getString();
                    String path = pluginX.getElement("path").getString();
                    File ff = new File(file.getParentFile(), path);
                    Class<?> clazz = PluginRegistry.getInstance().getClassForName(clazzStr);
                    Object o = clazz.newInstance();

                    Method method = clazz.getDeclaredMethod("fromFile", File.class);

                    AnalysisProvider p = (AnalysisProvider) method.invoke(o, ff);
                    if (p != null) {
                        anStage.addOrReplaceStagePlugin(p);
                    }
                }

                return anStage;
            } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    @Override
    public File toFile(File parent) {
        try {
            File anStageFile = new File(parent, "analysis.xml");
            anStageFile.createNewFile();
            
            XElement root = new XElement("analysis");
            XElement name = new XElement("name");
            name.setString(getName());

            
            root.addElement(name);
            
            for (StagePlugin plugin : plugins) {
                if ( !plugin.getConfigurations().isEmpty()) {
                    File p = new File(parent, "analysis");
                    if (!p.isDirectory()) {
                        p.mkdirs();
                    }
                    File f = plugin.toFile(p);
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
                        root.addElement(pluginX);
                    }
                }
            }
            
            XIO.writeUTF(root, new FileOutputStream(anStageFile));
            
            return anStageFile;
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
}