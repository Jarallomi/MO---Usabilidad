/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.core.v2.controller;

import com.google.inject.Inject;
import com.google.inject.Injector;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import mo.analysis.AnalysisProvider;
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;
import mo.core.v2.model.ConfigurationV2;
import mo.core.v2.model.Organization;
import mo.core.v2.model.StagePluginV2;
import mo.organization.Configuration;
import mo.organization.StageModule;
import mo.organization.StagePlugin;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Francisco
 */
public class ProjectAnalysisControllerAux {
    private final String CONTROLLER_KEY = "controller";
    
    @Inject
    public Injector injector;
    @Inject
    public Organization model;
    private int row;
    private String pluginType = "mo.analysis.AnalysisProvider";
    private ObservableList<String> ObservablePlugins = FXCollections.observableArrayList();
    private List<AnalysisProvider> analysis = new ArrayList<AnalysisProvider>();
    private List<String> analysisAux = new ArrayList<String>();
    String path/*Path del la crapeta del proyecto*/, 
            pathStage/*Path de la carpeta del stage (Analysis)*/, 
            pathConfig;/*Path de la crapeta de la configuracion*/
    File pathConfigXml, pathXml;
    
    public ProjectAnalysisControllerAux(Organization aux){
        this.model = aux;
    }
    
    public void init(){
        path = model.getOrg().getLocation().getAbsolutePath();
        List<Plugin> stagePlugins = PluginRegistry.getInstance().getPluginData().getPluginsFor("mo.organization.StageModule");
        for(Plugin stagePlugin : stagePlugins){
            StageModule nodeProvider = (StageModule) stagePlugin.getNewInstance();
            if(nodeProvider.getName().equals(model.getAnalysisStage().getName())){
                model.setMOCaptureStage(nodeProvider);
                break;
            }
        }
    }
    
    public void initAnalysisStage(){
        ObservablePlugins.clear();
        for(Plugin plugin : PluginRegistry.getInstance().getPluginData().getPluginsFor(pluginType)){
            for(StagePluginV2 sp : model.getAnalysisStage().getPlugins()){
                AnalysisProvider a = (AnalysisProvider) plugin.getNewInstance();
                if(a != null){
                    if(ObservablePlugins.isEmpty()){
                        ObservablePlugins.add(a.getName());
                        analysis.add(a);
                    }
                    if(!ObservablePlugins.get(ObservablePlugins.size()-1).equals(a.getName())){
                        ObservablePlugins.add(a.getName());
                        analysis.add(a);
                    }
                }
            }
        }
        model.setObservablePlugins(ObservablePlugins);
        model.setAnalysis(analysis);
    }
    
    public String addConfiguration(){
	String SelectedPlugin = model.getPluginSelected().getName();
	for(Plugin plugin : PluginRegistry.getInstance().getPluginData().getPluginsFor(pluginType)){
            for(StagePlugin sp : model.getMOCaptureStage().getPlugins()){
                AnalysisProvider a = (AnalysisProvider) plugin.getNewInstance();
                if(a != null){
                    if(a.getName().equals(SelectedPlugin)&&SelectedPlugin.equals(sp.getName())){
                        Configuration config = a.initNewConfiguration(model.getOrg());
                        if(config != null){
                            model.getConfigurationsA().add(config);
                            String aux = saveConfiguration(SelectedPlugin, config.getId());
                            sp.getConfigurations().add(config);
                            model.getOrg().store();
                            for(StageModule sm : model.getOrg().getStages()){
                                if(sm.getName().equals(model.getStages2().get(0).getName())){
                                    for(int i=0; i<sm.getPlugins().size(); i++){
                                        if(sm.getPlugins().get(i).getName().equals(SelectedPlugin)){
                                            sm.getPlugins().get(i).getConfigurations().add(config);
                                        }
                                    }
                                }
                            }
                            fileOfAnalysis(config, sp.getName());
                            writeInAnalysis(model.getPluginSelected().toString());
                            return aux;
                        }
                    }
                }
            }
	}
        return null;
    }
    
    public String saveConfiguration(String SelectedPlugin, String configId){
	for(StagePluginV2 spv : model.getAnalysisStage().getPlugins()){
            if(spv.getName().equals(SelectedPlugin)){
                ConfigurationV2 config = new ConfigurationV2(configId);
                spv.addConfiguration(config);
                Pair<String,String> configAux = new Pair<>(config.getName(),spv.getName());
                model.getConfigAnalysis().add(configAux);
                return spv.getName()+" ("+config.getName()+")";
            }            
	}
        return null;
    }
    
    private void createFolder(){
        pathStage = path + "\\analysis";
        File folder = new File(pathStage);
        folder.mkdir();
    }

    private void fileOfAnalysis(Configuration config, String name){
        createFolder();
        String nameAux = name.toLowerCase();
        String[] aux = nameAux.split(" ");
        pathConfig = pathStage+"\\"+aux[0]+"-analysis";
        pathConfigXml = new File(pathConfig);
        
        pathConfigXml.mkdirs();
        pathXml = config.toFile(pathConfigXml);
        fileOfAnalysis2(aux[0]);
    }
    
    private void fileOfAnalysis2(String name){
        try{
            File file = new File(pathStage + "\\" + name.toLowerCase()+"-analysis.xml");
            if(file.exists()){
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(file);

                Element element1 = doc.createElement("path");
                element1.setTextContent(pathConfigXml.getName()+"\\"+pathXml.getName());
                Node nodeRoot = doc.getFirstChild();
                nodeRoot.appendChild(element1);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(file);

                transformer.transform(source, result);
            }
            else{
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                Document doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("analysis");
                doc.appendChild(rootElement);

                Element element1 = doc.createElement("path");
                element1.setTextContent(pathConfigXml.getName()+"\\"+pathXml.getName());
                rootElement.appendChild(element1);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(file);

                transformer.transform(source, result);
            }
        }catch (ParserConfigurationException | TransformerException | SAXException | IOException  ex) {
            Logger.getLogger(ProjectCapturesControllerAux.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void writeInAnalysis(String plugin){
        File file = new File(path + "\\"+ "analysis.xml");
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            Element element1 = doc.createElement("plugin");
            Attr clase = doc.createAttribute("class");
            clase.setValue(plugin.split("@")[0]);
            element1.setAttributeNode(clase);
            Element element2 = doc.createElement("path");
            element2.setTextContent(pathConfigXml.getName()+"\\"+pathXml.getName());
            element1.appendChild(element2);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer;
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);

            transformer.transform(source, result);
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
            Logger.getLogger(ProjectVisualizationControllerAux.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
