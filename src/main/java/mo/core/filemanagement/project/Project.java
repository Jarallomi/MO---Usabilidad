package mo.core.filemanagement.project;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import mo.organization.ProjectOrganization;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Project {
    private static final Logger LOGGER = Logger.getLogger(Project.class.getName());
    private final File folder;
    private ProjectOrganization organization;
    
    public Project(String rootFolder){
        this.folder = new File(rootFolder);
        
        File xml = new File(folder, "moproject.xml");
        if (!xml.exists()){
            createProjectXml();
        }
    }

    public Project(String[] nameAux) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public File getFolder() {
        return this.folder;
    }

    private void createProjectXml() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder buider;
        try {
            buider = factory.newDocumentBuilder();
            Document doc = buider.newDocument();
            Element mainRootElement = doc.createElement("project");
            
            doc.appendChild(mainRootElement);

 
            // output DOM XML to console 
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
            DOMSource source = new DOMSource(doc);
            StreamResult file = new StreamResult(new File(folder+"/moproject.xml"));
            transformer.transform(source, file);
 
        } catch (ParserConfigurationException | DOMException | IllegalArgumentException | TransformerException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }
}
