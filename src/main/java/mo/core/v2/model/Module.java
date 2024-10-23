/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.core.v2.model;

import com.google.inject.AbstractModule;
import java.util.ArrayList;
import java.util.List;
import mo.analysis.AnalysisProvider;
import mo.capture.CaptureProvider;
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;
import mo.organization.Configuration;
import mo.organization.Participant;
import mo.organization.StageModule;
import mo.organization.StagePlugin;
import mo.visualization.VisualizationProvider;


/**
 *
 * @author Francisco
 */
public class Module extends AbstractModule{
    
    @Override
    protected void configure() {

        List<Participant> participants = new ArrayList<>();
        List<StagePluginV2> plugins = new ArrayList<>();
        List<StagePluginV2> plugins2 = new ArrayList<>();
        List<StagePluginV2> plugins3 = new ArrayList<>();
        List<Configuration> configurations = new ArrayList<>();
        List<StageModuleV2> stages = new ArrayList<>();
        List<StageModuleV2> stages2 = new ArrayList<>();
        List<StageModuleV2> stages3 = new ArrayList<>();
        addCapturePlugins(plugins);
        addAnalysisPlugins(plugins2);
        addVisualizationPlugins(plugins3);
        List<Plugin> stagePlugins = PluginRegistry.getInstance().getPluginData().getPluginsFor("mo.organization.StageModule");
        StageModule nodeProvider0 = (StageModule) stagePlugins.get(1).getNewInstance();
        String Aux0 = nodeProvider0.getName();
        StageModuleV2 StageCapture = new StageModuleV2(Aux0, plugins);
        System.out.println("captura agregado :S");
        stages.add(StageCapture);
        System.out.println("captura agregado :S2");
        StageModule nodeProvider = (StageModule) stagePlugins.get(0).getNewInstance();
        String Aux = nodeProvider.getName();
        StageModuleV2 StageAnalysis = new StageModuleV2(Aux, plugins2);
        stages2.add(StageAnalysis);
        StageModule nodeProvider2 = (StageModule) stagePlugins.get(2).getNewInstance();
        String Aux2 = nodeProvider2.getName();
        StageModuleV2 StageVisualization = new StageModuleV2(Aux2, plugins3);
        stages3.add(StageVisualization);
        StagePlugin pluginSelected = null;
        Configuration configurationSelected = null;
        Integer type = new Integer(0);
        Organization model = new Organization(participants,plugins,plugins2,plugins3,configurations,pluginSelected,configurationSelected,stages,stages2,stages3);
        bind(Organization.class).toInstance(model);
    }
    
    public List<StagePluginV2> addCapturePlugins(List<StagePluginV2> plugins){
        for(Plugin plugin : PluginRegistry.getInstance().getPluginData().getPluginsFor("mo.capture.CaptureProvider")){
            List<ConfigurationV2> configurations = new ArrayList<>();
            CaptureProvider c = (CaptureProvider) plugin.getNewInstance();
            plugins.add(new StagePluginV2(c.getName(), configurations));
        }
        return plugins;
    }
    
    public List<StagePluginV2> addAnalysisPlugins(List<StagePluginV2> plugins2){
        for(Plugin plugin : PluginRegistry.getInstance().getPluginData().getPluginsFor("mo.analysis.AnalysisProvider")){
            List<ConfigurationV2> configurations = new ArrayList<>();
            AnalysisProvider a = (AnalysisProvider) plugin.getNewInstance();
            plugins2.add(new StagePluginV2(a.getName(), configurations));
        }
        return plugins2;
    }
    
    public List<StagePluginV2> addVisualizationPlugins(List<StagePluginV2> plugins){
        for(Plugin plugin : PluginRegistry.getInstance().getPluginData().getPluginsFor("mo.visualization.VisualizationProvider")){
            List<ConfigurationV2> configurations = new ArrayList<>();
            VisualizationProvider v = (VisualizationProvider) plugin.getNewInstance();
            plugins.add(new StagePluginV2(v.getName(), configurations));
        }
        return plugins;
    }
}
