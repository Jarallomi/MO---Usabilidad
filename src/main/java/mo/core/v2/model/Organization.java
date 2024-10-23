/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.core.v2.model;

import com.google.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import mo.analysis.AnalysisProvider;
import mo.capture.CaptureProvider;
import mo.core.filemanagement.project.Project;
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;
import mo.organization.Configuration;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;
import mo.organization.StageModule;
import mo.organization.StagePlugin;
import mo.visualization.VisualizationProvider;

/**
 *
 * @author Francisco
 */
public class Organization {
    Project project;
    File fileProject;
    int type;
    public int newProyect;
    List<Participant> participants = new ArrayList<>();
    Participant pSelected = null;
    //List<StageModule> stages;
    StageModule MOCaptureStage;
    List<StagePluginV2> plugins;
    List<StagePluginV2> plugins2;
    List<StagePluginV2> plugins3;
    List<Configuration> configurationsC;
    List<Configuration> configurationsA;
    List<Configuration> configurationsV;
    StagePlugin pluginSelected;
    List<Configuration> configurationSelected;
    ProjectOrganization org;
    Boolean restoreBoolean = false;
    List<CaptureProvider> captures;
    List<AnalysisProvider> analysis;
    List<VisualizationProvider> visualization;
    List<Pair<String,String>> configCaptures = new ArrayList<Pair<String,String>>();
    List<Pair<String,String>> configAnalysis = new ArrayList<Pair<String,String>>();
    List<Pair<String,String>> configVisualizations = new ArrayList<Pair<String,String>>();
    String config;
    ObservableList<String> ObservablePlugins = FXCollections.observableArrayList();
    List<StageModuleV2> stages;
    List<StageModuleV2> stages2;
    List<StageModuleV2> stages3;
    File file;

    @Inject
    public Organization(List<Participant> participants, List<StagePluginV2> plugins, List<StagePluginV2> plugins2, List<StagePluginV2> plugins3, List<Configuration> configurations, StagePlugin pluginSelected, Configuration configurationSelected, List<StageModuleV2> stages, List<StageModuleV2> stages2, List<StageModuleV2> stages3) {
        this.fileProject = fileProject;
        this.plugins = plugins;
        this.plugins2 = plugins2;
        this.plugins3 = plugins3;
        //this.configurations = configurations;
        this.pluginSelected = pluginSelected;
        captures = new ArrayList<>();
        analysis = new ArrayList<>();
        visualization = new ArrayList<>();
        this.stages = stages;
        this.stages2 = stages2;
        this.stages3 = stages3;
        configurationsC = new ArrayList<>();
        configurationsA = new ArrayList<>();
        configurationsV = new ArrayList<>();
        this.configurationSelected = new ArrayList<>();
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    
    public File getFileProject() {
        return fileProject;
    }

    public void setFileProject(File fileProject) {
        this.fileProject = fileProject;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public Participant getParticipantById(String id){
        for(Participant p : participants){
            if(p.id.equals(id)){
                return p;
            }
        }
        return null;
    }
    
    public Participant getpSelected() {
        return pSelected;
    }

    public void setpSelected(Participant pSelected) {
        this.pSelected = pSelected;
    }

    public List<Pair<String, String>> getConfigCaptures() {
        return configCaptures;
    }

    public void setConfigCaptures(List<Pair<String, String>> configCaptures) {
        this.configCaptures = configCaptures;
    }

    public List<Pair<String, String>> getConfigAnalysis() {
        return configAnalysis;
    }

    public void setConfigAnalysis(List<Pair<String, String>> configAnalysis) {
        this.configAnalysis = configAnalysis;
    }

    public List<Pair<String, String>> getConfigVisualizations() {
        return configVisualizations;
    }

    public void setConfigVisualizations(List<Pair<String, String>> configVisualizations) {
        this.configVisualizations = configVisualizations;
    }
    
    
    
    /*public List<Participant> getParticipantsNoUsed() {
        return participantsNoUsed;
    }

    public void setParticipantsNoUsed(List<Participant> participantsNoUsed) {
        this.participantsNoUsed = participantsNoUsed;
    }

    public List<Participant> getParticipantsAll() {
        return participantsAll;
    }

    public void setParticipantsAll(List<Participant> participantsAll) {
        this.participantsAll = participantsAll;
    }*/

    public List<StagePluginV2> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<StagePluginV2> plugins) {
        this.plugins = plugins;
    }

    public List<StagePluginV2> getPlugins2() {
        return plugins2;
    }

    public void setPlugins2(List<StagePluginV2> plugins2) {
        this.plugins2 = plugins2;
    }

    public List<StagePluginV2> getPlugins3() {
        return plugins3;
    }

    public void setPlugins3(List<StagePluginV2> plugins3) {
        this.plugins3 = plugins3;
    }

    public List<Configuration> getConfigurationsC() {
        return configurationsC;
    }

    public void setConfigurationsC(List<Configuration> configurationsC) {
        this.configurationsC = configurationsC;
    }

    public List<Configuration> getConfigurationsA() {
        return configurationsA;
    }

    public void setConfigurationsA(List<Configuration> configurationsA) {
        this.configurationsA = configurationsA;
    }

    public List<Configuration> getConfigurationsV() {
        return configurationsV;
    }

    public void setConfigurationsV(List<Configuration> configurationsV) {
        this.configurationsV = configurationsV;
    }

    

    public StagePlugin getPluginSelected() {
        return pluginSelected;
    }

    public void setPluginSelected(StagePlugin pluginSelected) {
        this.pluginSelected = pluginSelected;
    }

    public List<Configuration> getConfigurationSelected() {
        return configurationSelected;
    }

    public void setConfigurationSelected(List<Configuration> configurationSelected) {
        this.configurationSelected = configurationSelected;
    }

    public ProjectOrganization getOrg() {
        return org;
    }

    public void setOrg(ProjectOrganization org) {
        this.org = org;
        setData();
        pairOfConfig();
    }

    public Boolean getRestoreBoolean() {
        return restoreBoolean;
    }

    public void setRestoreBoolean(Boolean restoreBoolean) {
        this.restoreBoolean = restoreBoolean;
    }

    public List<StageModuleV2> getStageModules(){
        return stages;
    }
    
    public StageModuleV2 getCaptureStage(){
        return stages.get(0);
    }
    
    public void setStageModules(List<StageModuleV2> stages){
        this.stages = stages;
    }

    public List<StageModuleV2> getStages2() {
        return stages2;
    }

    public StageModuleV2 getAnalysisStage(){
        return stages2.get(0);
    }
    
    public void setStages2(List<StageModuleV2> stages2) {
        this.stages2 = stages2;
    }

    public List<StageModuleV2> getStages3() {
        return stages3;
    }

    public StageModuleV2 getVisualizationStage(){
        return stages3.get(0);
    }
    
    public void setStages3(List<StageModuleV2> stages3) {
        this.stages3 = stages3;
    }
    
    
    
    public StageModule getMOCaptureStage() {
        return MOCaptureStage;
    }

    public void setMOCaptureStage(StageModule stageModule) {
        this.MOCaptureStage = stageModule;
    }

    public List<CaptureProvider> getCaptures() {
        return captures;
    }

    public void setCaptures(List<CaptureProvider> captures) {
        this.captures = captures;
    }

    public List<AnalysisProvider> getAnalysis() {
        return analysis;
    }

    public void setAnalysis(List<AnalysisProvider> analysis) {
        this.analysis = analysis;
    }

    public List<VisualizationProvider> getVisualization() {
        return visualization;
    }

    public void setVisualization(List<VisualizationProvider> visualization) {
        this.visualization = visualization;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
    
    public ObservableList<String> getObservablePlugins() {
        return ObservablePlugins;
    }

    public void setObservablePlugins(ObservableList<String> ObservablePlugins) {
        this.ObservablePlugins = ObservablePlugins;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
    
    public void getProviderFromOrg(){
        for(int i=0; i<this.org.getStages().get(0).getPlugins().size(); i++){
            if(!org.getStages().get(0).getPlugins().get(i).getConfigurations().isEmpty()){
                for(Plugin plugin : PluginRegistry.getInstance().getPluginData().getPluginsFor("mo.capture.CaptureProvider")){
                    CaptureProvider c = (CaptureProvider) plugin.getNewInstance();
                    if(org.getStages().get(0).getPlugins().get(i).getName().equals(c.getName())){
                        for(int j=0; j<org.getStages().get(0).getPlugins().get(i).getConfigurations().size();j++){
                            saveConfiguration(org.getStages().get(0).getName(),c.getName(),org.getStages().get(0).getPlugins().get(i).getConfigurations().get(j).getId());
                        }
                    }
                }
            }
        }      
    }
    
    public void saveConfiguration(String stage,String SelectedPlugin, String configId){
        for(StagePluginV2 spv: this.getCaptureStage().getPlugins()){
            if(spv.getName().equals(SelectedPlugin)){
                ConfigurationV2 config = new ConfigurationV2(configId);
                spv.addConfiguration(config);
                Pair<String,String> configAux = new Pair<>(config.getName(),spv.getName());
                if(stage.equals(this.stages.get(0).name)){
                    this.getConfigCaptures().add(configAux);
                }
                else if(stage.equals(this.stages2.get(0).name)){
                    this.getConfigAnalysis().add(configAux);
                }
                else if(stage.equals(this.stages3.get(0).name)){
                    this.getConfigVisualizations().add(configAux);
                }
                
            }
        }
    }
    
    public void setConfigInOrg(Configuration config , StagePlugin plugin){
        plugin.getConfigurations().add(config);
    }
    
    public void setData(){
        participants = org.getParticipants();
        this.configurationsC = new ArrayList<>();
        this.configurationsA = new ArrayList<>();
        this.configurationsV = new ArrayList<>();
        for(StageModule sm : org.getStages()){
            if(sm.getName().equals(this.stages.get(0).name)){
                for(StagePlugin p : sm.getPlugins()){
                    if(!p.getConfigurations().isEmpty()){
                        for(Configuration c : p.getConfigurations()){
                            this.configurationsC.add(c);
                        }
                    }
                }
            }
            if(sm.getName().equals(this.stages2.get(0).name)){
                for(StagePlugin p : sm.getPlugins()){
                    if(!p.getConfigurations().isEmpty()){
                        for(Configuration c : p.getConfigurations()){
                            this.configurationsA.add(c);
                        }
                    }
                }
            }
            if(sm.getName().equals(this.stages3.get(0).name)){
                System.out.println("in visu");
                for(StagePlugin p : sm.getPlugins()){
                    System.out.println("empty: " + p.getConfigurations().isEmpty());
                    if(!p.getConfigurations().isEmpty()){
                        for(Configuration c : p.getConfigurations()){
                            this.configurationsV.add(c);
                        }
                    }
                }
            }
            
        }
    }
    
    public void pairOfConfig(){
        this.configCaptures = new ArrayList<>();
        this.configAnalysis = new ArrayList<>();
        this.configVisualizations = new ArrayList<>();
        for(StageModule m : org.getStages()){
            for(StagePlugin p : m.getPlugins()){
                if(!p.getConfigurations().isEmpty()){
                    for(Configuration c : p.getConfigurations()){
                        if(m.getName().equals("Captura")){
                            this.configCaptures.add(new Pair<String,String>(c.getId(), p.getName()));
                        }
                        if(m.getName().equals(stages2.get(0).getName())){
                            System.out.println(stages2.get(0).getName());
                            System.out.println(c.getId() +", " + p.getName());
                            this.configAnalysis.add(new Pair<String,String>(c.getId(), p.getName()));
                        }
                        if(m.getName().equals(stages3.get(0).getName())){
                            this.configVisualizations.add(new Pair<String,String>(c.getId(), p.getName()));
                        }
                    }
                }
            }
        }
        System.out.println("--------------------------------");
    }
    
    public StagePlugin getNotesPlugin(){
        for(StageModule sm : org.getStages()){
            if(sm.getName().equals(stages2.get(0).name)){
                for (StagePlugin plugin : sm.getPlugins()) {
                    if(plugin.getName().equals("Notes plugin")){
                        return plugin;
                    }
                }
                return null;
            }
        }
        return null;
    }
}



