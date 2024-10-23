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
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import mo.core.MultimodalObserver;
import static mo.core.Utils.getBaseFolder;
import mo.core.filemanagement.FileRegistry;
import mo.core.filemanagement.project.Project;
import mo.core.preferences.AppPreferencesWrapper;
import mo.core.preferences.PreferencesManager;
import mo.core.v2.model.Organization;
import mo.organization.ProjectOrganization;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * FXML Controller class
 *
 * @author Francisco
 */
public class NewProjectController implements Initializable {

    @FXML
    private GridPane NewProjectNameGridPane;
    @FXML
    private Text newProjectLabel;
    @FXML
    private TextField nameText;
    @FXML
    private TextField locationLabel;
    @FXML
    private Text projectNameLabel;
    @FXML
    private Text projectLocationLabel;
    @FXML
    private Button createButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Text alertLabel;
    @FXML
    private Button searchButton;
    @Inject
    public Injector injector;
    @Inject 
    Organization model;
    private boolean exist;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        init();
        nameText.addEventHandler(KeyEvent.ANY, eventHandlerTextField);
        File proj = new File(nameText.getText());
        exist = proj.exists();
        if(exist){
            alertLabel.setText("A project with this name already exists");
            createButton.disableProperty().set(exist);
        }
        else{
            alertLabel.setText("");
            createButton.disableProperty().set(exist);
        }
        
    }    

    EventHandler<KeyEvent> eventHandlerTextField = new EventHandler<KeyEvent>() { 
         @Override 
         public void handle(KeyEvent event) { 
            writeName(); 
         }           
      };    
    
    
    private void writeName() {
        File proj = new File(nameText.getText());
        exist = proj.exists();
        if(exist){
            alertLabel.setText("A project with this name already exists");
            createButton.disableProperty().set(exist);
        }
        else{
            alertLabel.setText("");
            createButton.disableProperty().set(exist);
        }
    }

    @FXML
    private void createProject(MouseEvent event) {
        model.newProyect = 1;
        String path = locationLabel.getText();
        String name = nameText.getText();
        File proj = new File(path+"/"+name);
        if(proj.mkdir()){
            Project p = new Project(path+"/"+name);
            saveProjectInAppPreferences(p);
            saveProjectInFiles(p);
            FileRegistry.getInstance().addOpenedProject(p);
            ProjectOrganization org = new ProjectOrganization(name);
            model.setProject(p);
            model.setOrg(org);
            model.setFileProject(proj);
            model.getOrg().store();
            cancel(event);
        }
    }

    @FXML
    private void cancel(MouseEvent event) {
        model.newProyect = -1;
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void DirectoryChooser(MouseEvent event) {
        final DirectoryChooser dirchooser = new DirectoryChooser();
        File file = dirchooser.showDialog(null);
        if(file!=null){
            locationLabel.setText(file.getAbsolutePath());
        }
    }

    private void init(){
        locationLabel.setText(getBaseFolder());
        nameText.setText("newproject");
        File proj = new File(locationLabel.getText()+nameText.getText());
        exist = proj.exists();
        if(exist){
            alertLabel.setText("A project with this name already exists");
            createButton.disableProperty().set(exist);
        }
    }
    
    private void saveProjectInAppPreferences(Project project){
        PreferencesManager pm = new PreferencesManager();
        AppPreferencesWrapper app = (AppPreferencesWrapper) pm.loadOrCreate(AppPreferencesWrapper.class,new File(MultimodalObserver.APP_PREFERENCES_FILE));
        app.addOpenedProject(project.getFolder().getAbsolutePath());
        pm.save(app, new File(MultimodalObserver.APP_PREFERENCES_FILE));
    } 
    
    private void saveProjectInFiles(Project project){
        try{
            File file = new File(getBaseFolder(), "files.xml");
            if(file.exists()){
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document document = db.parse(file);
                document.getDocumentElement().normalize();
                NodeList nlist = document.getDocumentElement().getChildNodes();
                for(int i=0; i<nlist.getLength(); i++){
                    Node node = nlist.item(i);
                    if(node.getNodeType() == Node.ELEMENT_NODE){
                        Element elem = (Element) node;
                        elem.getElementsByTagName("openedProjects");
                        System.out.println("Tag: " + elem.getTagName());
                        Element elem2 = document.createElement("project");
                        elem2.setTextContent(project.getFolder().getAbsolutePath());
                        node.appendChild(elem2);
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        DOMSource source = new DOMSource(document);
                        StreamResult result = new StreamResult(file);
                        transformer.transform(source, result);
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
            Logger.getLogger(NewProjectController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
