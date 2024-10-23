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
import mo.capture.CaptureProvider;
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;
import mo.core.v2.model.ConfigurationV2;
import mo.core.v2.model.Organization;
import mo.core.v2.model.StagePluginV2;
import mo.organization.Configuration;
import mo.organization.StageModule;
import mo.organization.StagePlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Francisco
 */
public class ProjectCapturesControllerAux {
    private final String CONTROLLER_KEY = "controller";
    
    @Inject
    Injector injector;
    Organization model;
    
    ObservableList<String> ObservablePlugins = FXCollections.observableArrayList();
    List<CaptureProvider> captures = new ArrayList<>();
    List<String> capturesAux = new ArrayList<>();
    String path/*Path del la crapeta del proyecto*/, 
            pathStage/*Path de la carpeta del stage (Capture)*/, 
            pathConfig;/*Path de la crapeta de la configuracion*/
    File pathConfigXml, pathXml;
    
    public ProjectCapturesControllerAux(Organization aux){
        this.model = aux;
    }
    
    public void init(){
        path = model.getOrg().getLocation().getAbsolutePath();
        List<Plugin> stagePlugins = PluginRegistry.getInstance().getPluginData().getPluginsFor("mo.organization.StageModule");
        for(Plugin stagePlugin : stagePlugins){
            StageModule nodeProvider = (StageModule) stagePlugin.getNewInstance();
            if(nodeProvider.getName().equals(model.getCaptureStage().getName())){
                model.setMOCaptureStage(nodeProvider);
                break;
            }
        }
    }
    
    public void initCaptureStage(){
        ObservablePlugins.clear();
        for(Plugin plugin : PluginRegistry.getInstance().getPluginData().getPluginsFor("mo.capture.CaptureProvider")){
            for(StagePluginV2 sp : model.getCaptureStage().getPlugins()){
                CaptureProvider c = (CaptureProvider) plugin.getNewInstance();
                if(c != null){
                    if(ObservablePlugins.isEmpty()){
                        ObservablePlugins.add(c.getName());
                        captures.add(c);
                    }
                    if(!ObservablePlugins.get(ObservablePlugins.size()-1).equals(c.getName())){
                        ObservablePlugins.add(c.getName());
                        captures.add(c);
                    }
                }
            }
        }
        model.setObservablePlugins(ObservablePlugins);
        model.setCaptures(captures);
    }
    
    public String addConfiguration(){
        String SelectedPlugin = model.getPluginSelected().getName();
        for(Plugin plugin : PluginRegistry.getInstance().getPluginData().getPluginsFor("mo.capture.CaptureProvider")){
            for(StagePlugin sp: model.getMOCaptureStage().getPlugins()){
                CaptureProvider c = (CaptureProvider) plugin.getNewInstance();
                if(c != null){
                    if(c.getName().equals(SelectedPlugin) && SelectedPlugin.equals(sp.getName())){
                        Configuration config = c.initNewConfiguration(model.getOrg());
                        if(config != null){
                            String aux = saveConfiguration(SelectedPlugin, config.getId());
                            model.getConfigurationsC().add(config);
                            sp.getConfigurations().add(config);
                            //model.setConfigInOrg(config, sp);
                            model.getOrg().store();
                            for(StageModule sm : model.getOrg().getStages()){
                                if(sm.getName().equals("Captura")){
                                    for(int i=0; i<sm.getPlugins().size(); i++){
                                        if(sm.getPlugins().get(i).getName().equals(SelectedPlugin)){
                                            sm.getPlugins().get(i).getConfigurations().add(config);
                                        }
                                    }
                                }
                            }
                            fileOfCapture(config, sp.getName());
                            return aux;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public String saveConfiguration(String SelectedPlugin, String configId){
        for(StagePluginV2 spv: model.getCaptureStage().getPlugins()){
            if(spv.getName().equals(SelectedPlugin)){
                ConfigurationV2 config = new ConfigurationV2(configId);
                spv.addConfiguration(config);
                Pair<String,String> configAux = new Pair<>(config.getName(),spv.getName());
                model.getConfigCaptures().add(configAux);
                return spv.getName()+" ("+config.getName()+")";
                
            }
        }
        return null;
    }
    
    private void createFolder(){
        pathStage = path + "\\capture";
        File folder = new File(pathStage);
        folder.mkdir();
    }
    
    private void fileOfCapture(Configuration config, String name){
        createFolder();
        String nameAux = name.toLowerCase();
        pathConfig = pathStage+"\\"+nameAux+"-capture";
        pathConfigXml = new File(pathConfig);
        
        pathConfigXml.mkdirs();
        pathXml = config.toFile(pathConfigXml);
        fileOfCaptures(name);
    }
    
    private void fileOfCaptures(String name){
        try {
            File file = new File(pathStage + "\\" + name.toLowerCase()+"-capture.xml");
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
                Element rootElement = doc.createElement("captures");
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
}
