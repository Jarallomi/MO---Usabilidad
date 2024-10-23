/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.core.v2.controller;

import com.google.inject.Inject;
import com.google.inject.Injector;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import mo.core.v2.model.CommunicationManagementV2;
import mo.core.v2.model.LanguageV2;

/**
 * FXML Controller class
 *
 * @author Francisco
 */
public class SettingsController implements Initializable {
    private final String CONTROLLER_KEY = "controller";

    private AnchorPane centerPane;
    @Inject
    public Injector injector;
    @FXML
    private BorderPane pane;
    @FXML
    private Text textSettings;
    @FXML
    private Button languageButton;
    @FXML
    private Button serverConfigBtn;
    
    private Pane paneLanguage;
    private Pane paneServer;
    private Text languageText;
    private Text serverText;
    
    private CommunicationManagementV2 cmv2;
    private LanguageV2 lv2;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void languageClick(MouseEvent event) {
        lv2 = new LanguageV2();
        lv2.actionPerformed();
    }

    @FXML
    private void configServerClick(MouseEvent event) {
        cmv2 = new CommunicationManagementV2();
        cmv2.newConnection(new ActionEvent(this, 1, "test"));
    }
}