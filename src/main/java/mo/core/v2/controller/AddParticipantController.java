/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.core.v2.controller;

import com.google.inject.Inject;
import com.google.inject.Injector;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mo.core.v2.model.Organization;
import mo.organization.Participant;

/**
 * FXML Controller class
 *
 * @author Francisco
 */
public class AddParticipantController implements Initializable {

    @FXML
    private Text addParticipantText;
    @FXML
    private Text idLabel;
    @FXML
    private TextField idText;
    @FXML
    private TextField nameText;
    @FXML
    private Text nameLabel;
    @FXML
    private DatePicker calendar;
    @FXML
    private Text dateLabel;
    @FXML
    private Text noteLabel;
    @FXML
    private TextArea noteText;
    @FXML
    private Button addButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Text alert;
    @FXML
    private Text alertLabel;
    private int numParticipant;
    private boolean edit = false;
    @Inject
    public Injector injector;
    @Inject
    Organization model;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        init();
    }    
    
    public void init(){
        if(model.getpSelected() != null){
            edit = true;
            Participant p = model.getpSelected();
            addButton.setText("Edit");
            idText.setText(p.id);
            nameText.setText(p.name);
            calendar.setValue(p.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            noteText.setText(p.notes);
            idText.disableProperty().set(edit);
            calendar.disableProperty().set(edit);
            
        }
        else{
            alert.setText("");
            numParticipant = model.getParticipants().size();
            int numAux = numParticipant + 1;
            //idText.setText(String.valueOf(numAux));
            calendar.setValue(LocalDate.now());
        }
        
        
    }    

    @FXML
    private void addClick(MouseEvent event) {
        if(!edit){
            if(!model.getParticipants().isEmpty()){
                for(Participant p : model.getParticipants()){
                    if(p.id.equals(idText.getText())){
                        alertLabel.setText("The id must be unique");
                        break;
                    }
                    else {
                        if(idText.getText().toCharArray().length<=0){
                            //alert.setText("*");
                            alertLabel.setText("you must write a id");
                            break;
                        }
                        else{
                            createParticipant();
                            break;
                        }
                    }
                }
            }
            else{
                if(nameText.getText().toCharArray().length<=0){
                    alertLabel.setText("you must write a name");
                }
                else{
                    createParticipant(); 
                }
            }
        }
        else{
            model.getpSelected().name = nameText.getText();
            model.getpSelected().notes = noteText.getText();
        }
        cancelClick(event);
    }

    @FXML
    private void cancelClick(MouseEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    private void createParticipant(){
        Participant p = new Participant();
        p.id = idText.getText();
        p.name = nameText.getText();
        p.notes = noteText.getText();
        p.isLocked = false;
        Instant instant = Instant.from(calendar.getValue().atStartOfDay(ZoneId.systemDefault()));
        Date dateAux = Date.from(instant);
        p.date = dateAux;
        p.folder = "/participant-"+p.id;
        //model.getParticipants().add(p);
        model.getOrg().getParticipants().add(p);
        model.getOrg().store();
    }
}
