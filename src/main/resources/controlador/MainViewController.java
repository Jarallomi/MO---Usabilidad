/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author Francisco
 */
public class MainViewController implements Initializable {

    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ImageView moIcon;
    @FXML
    private Button newProjectBnt;
    @FXML
    private Button projectsBtn;
    @FXML
    private Button exitBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void newProjectClick(MouseEvent event) {
    }

    @FXML
    private void projectsClick(MouseEvent event) {
    }

    @FXML
    private void exitClick(MouseEvent event) {
    }
    
}
