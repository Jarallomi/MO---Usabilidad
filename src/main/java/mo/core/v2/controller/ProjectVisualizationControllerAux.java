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
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;
import mo.core.v2.model.ConfigurationV2;
import mo.core.v2.model.Organization;
import mo.core.v2.model.StagePluginV2;
import mo.organization.Configuration;
import mo.organization.StageModule;
import mo.organization.StagePlugin;
import mo.visualization.VisualizationProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Francisco
 */
public class ProjectVisualizationControllerAux {
    private final String CONTROLLER_KEY = "controller";
    
    private final String pluginType = "mo.visualization.VisualizationProvider";
    @Inject
    public Injector injector;
    @Inject
    Organization model;
    private int row;
    ObservableList<String> ObservablePlugins = FXCollections.observableArrayList();
    List<VisualizationProvider> visualization = new ArrayList<>();
    List<String> visualizationAux = new ArrayList<>();
    String path/*Path del la crapeta del proyecto*/, 
            pathStage/*Path de la carpeta del stage (Capture)*/, 
            pathConfig/*Path de la crapeta de la configuracion*/,
            pathAux;
    File pathConfigXml, pathXml;
    StagePlugin pluginAux = null;

    public ProjectVisualizationControllerAux(Organization aux){
        this.model = aux;
    }
    
    public void init(){
        path = model.getOrg().getLocation().getAbsolutePath();
        List<Plugin> stagePlugins = PluginRegistry.getInstance().getPluginData().getPluginsFor("mo.organization.StageModule");
        for(Plugin stagePlugin : stagePlugins){
            StageModule nodeProvider = (StageModule) stagePlugin.getNewInstance();
            if(nodeProvider.getName().equals(model.getVisualizationStage().getName())){
                model.setMOCaptureStage(nodeProvider);
                break;
            }
        }
    }
    
    public void initVisualizationStage(){
        ObservablePlugins.clear();
        for(Plugin plugin : PluginRegistry.getInstance().getPluginData().getPluginsFor(pluginType)){
            for(StagePluginV2 sp: model.getCaptureStage().getPlugins()){
                VisualizationProvider v = (VisualizationProvider) plugin.getNewInstance();
                if(v != null){
                    if(ObservablePlugins.isEmpty()){
                        ObservablePlugins.add(v.getName());
                        visualization.add(v);
                    }
                    if(!ObservablePlugins.get(ObservablePlugins.size()-1).equals(v.getName())){
                        ObservablePlugins.add(v.getName());
                        visualization.add(v);
                    }
                }
            }
        }
        model.setObservablePlugins(ObservablePlugins);
        model.setVisualization(visualization);
    }
    
    public String addConfiguration(){
        String SelectedPlugin = model.getPluginSelected().getName();
        for(Plugin plugin : PluginRegistry.getInstance().getPluginData().getPluginsFor(pluginType)){
            for(StagePlugin sp : model.getMOCaptureStage().getPlugins()){
                VisualizationProvider v = (VisualizationProvider) plugin.getNewInstance();
                if(v != null){
                    if(v.getName().equals(SelectedPlugin) && SelectedPlugin.equals(sp.getName())){
                        Configuration config = v.initNewConfiguration(model.getOrg());
                        if(config != null){
                            model.getConfigurationsV().add(config);
                            String aux = saveConfiguration(SelectedPlugin, config.getId());
                            model.getOrg().store(); 
                            sp.getConfigurations().add(config);                           
                            for(StageModule sm : model.getOrg().getStages()){
                                if(sm.getName().equals(model.getStages3().get(0).getName())){
                                    for(int i=0; i<sm.getPlugins().size(); i++){
                                        if(sm.getPlugins().get(i).getName().equals(SelectedPlugin)){
                                            sm.getPlugins().get(i).getConfigurations().add(config);
                                        }
                                    }
                                }
                            }
                            fileOfVisualization(config, sp.getName());
                            writeInVisu(model.getPluginSelected().toString());
                            return aux;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public String saveConfiguration(String SelectedPlugin, String configId){
        for(StagePluginV2 spv : model.getVisualizationStage().getPlugins()){
            if(spv.getName().equals(SelectedPlugin)){
                ConfigurationV2 config = new ConfigurationV2(configId);
                spv.addConfiguration(config);
                Pair<String,String> configAux = new Pair<>(config.getName(),spv.getName());
                model.getConfigVisualizations().add(configAux);
                return spv.getName()+" ("+config.getName()+")";
            }
        }
        return null;
    }
    
    private void createFolder(){
        pathStage = path + "\\visualization";
        File folder = new File(pathStage);
        folder.mkdir();
    }
    
    private void fileOfVisualization(Configuration config, String name){
        createFolder();
        String nameAux = name.toLowerCase();
        String[] aux = nameAux.split(" ");
        pathConfig = pathStage+"\\"+aux[0]+"-visualization";
        pathConfigXml = new File(pathConfig);
        
        pathConfigXml.mkdirs();
        pathXml = config.toFile(pathConfigXml);
        String pathAuxString = pathStage + "\\" + aux[0].toLowerCase()+"-visualization.xml";
        fileOfVisualizations(aux[0]);       
    }
    
    private void fileOfVisualizations(String name){
        try {
            File file = new File(pathStage + "\\" + name.toLowerCase()+"-visualization.xml");
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
                Element rootElement = doc.createElement("vis");
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
        } catch (ParserConfigurationException | TransformerException | SAXException | IOException  ex) {
            Logger.getLogger(ProjectCapturesControllerAux.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void writeInVisu(String plugin){
        String pathAux = pathConfigXml.getName()+"\\"+pathXml.getName();
        File file = new File(path + "\\"+ "visualization.xml");
        if(file.exists()){
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
}
