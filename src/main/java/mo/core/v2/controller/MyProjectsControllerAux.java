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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import mo.core.filemanagement.project.Project;
import mo.core.v2.model.Organization;
import mo.organization.ProjectOrganization;
import mo.organization.StageModule;
import mo.organization.StagePlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Francisco
 */
public class MyProjectsControllerAux {
    private final String CONTROLLER_KEY = "controller";
    
    @Inject
    public Injector injector;
    @Inject 
    Organization model;
    private List<String> projectsPath = new ArrayList<>();
    private List<ProjectOrganization> projectsOrgs = new ArrayList<>();
    
    private void getPathOfFiles(){
        try {
            File file = new File("files.xml");
            int numProjects=0;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();
            NodeList nlist = document.getDocumentElement().getChildNodes();
            int aux;
            for(aux=0; aux<nlist.getLength(); aux++){
                System.out.println("aux: " + aux);
                Node node = nlist.item(aux);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element elem = (Element) node;
                    numProjects = elem.getElementsByTagName("project").getLength();
                    break;
                }
            }
            System.out.println("numProjects: " + numProjects + "------------------------------------");
            for(int i=0; i<numProjects; i++){
                System.out.println("i: " + i);
                Node node = nlist.item(aux);
                Element elem = (Element) node;
                projectsPath.add(elem.getElementsByTagName("project").item(i).getChildNodes().item(0).getNodeValue());
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(MyProjectsControllerAux.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<ProjectOrganization> getDataByPath(){
        getPathOfFiles();
        for(String path : projectsPath){
            Project p = new Project(path);
            ProjectOrganization po = new ProjectOrganization(p);
            int aux = 0;
            System.out.println("PO: " + po.getLocation().getName());
            for(StageModule sm : po.getStages()){
                for(StagePlugin pl : sm.getPlugins()){
                    aux = pl.getConfigurations().size();
                    System.out.println("pl: " + pl.getConfigurations().size());
                }
                System.out.println("Aux: " + aux);
            }
            projectsOrgs.add(po);
        }
        return projectsOrgs;
    }
}
