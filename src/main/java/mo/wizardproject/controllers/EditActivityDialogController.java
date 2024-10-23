/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.wizardproject.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

/**
 * FXML Controller class
 *
 * @author Francisco
 */
public class EditActivityDialogController implements Initializable {

    @FXML
    private RadioButton closeTrue;
    @FXML
    private ToggleGroup closeToFinish;
    @FXML
    private RadioButton closeFalse;
    @FXML
    private TextField nameField;
    @FXML
    private TextField pathField;
    @FXML
    private TextField startMessageField;
    @FXML
    private TextField endMessageField;
    @FXML
    private Spinner<?> SpinnerTime;
    @FXML
    private Label labelOrder;
    @FXML
    private Label processNameLabel;
    @FXML
    private TextField processNameField;
    @FXML
    private Label warning;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void setVisibleProcessName(ActionEvent event) {
    }

    @FXML
    private void FileChooser(ActionEvent event) {
    }

    @FXML
    private void handleOk(ActionEvent event) {
    }

    @FXML
    private void handleCancel(ActionEvent event) {
    }
    
}
