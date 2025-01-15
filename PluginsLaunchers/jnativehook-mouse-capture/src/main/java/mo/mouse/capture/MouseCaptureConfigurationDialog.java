package mo.mouse.capture;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import mo.organization.ProjectOrganization;

public class MouseCaptureConfigurationDialog extends Stage {

    private final Label errorLabel;
    private final TextField nameField;
    private final Button accept;
    private final Button cancel;
    
    /*
    //test to try different lenguages on the tab
    Locale idiom = new Locale("en", "EN");
    ResourceBundle dialogBundle = ResourceBundle.getBundle("properties/principal", idiom);
    */
    
    ResourceBundle dialogBundle = ResourceBundle.getBundle("properties/principal");

    private final ProjectOrganization org;
    private boolean accepted = false;

    public MouseCaptureConfigurationDialog(ProjectOrganization organization, Window owner) {
        this.org = organization;

        setTitle(dialogBundle.getString("title"));
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: #d6cfcf;");
        grid.setAlignment(Pos.CENTER);

        
        Label label = new Label(dialogBundle.getString("configuration_n"));
        
        
        nameField = new TextField();
        nameField.setMaxWidth(200); 
        nameField.textProperty().addListener((observable, oldValue, newValue) -> updateState());
      
        errorLabel = new Label();
        errorLabel.setTextFill(javafx.scene.paint.Color.RED);
      
        accept = new Button(dialogBundle.getString("accept"));
        accept.setStyle("-fx-background-color: #b4eda6; -fx-text-fill: black;");
        accept.setDisable(true);
        accept.setOnAction(e -> {
            accepted = true;
            close();
        });
        accept.setMaxWidth(100); 

        
        cancel = new Button(dialogBundle.getString("cancel"));
        cancel.setStyle("-fx-background-color: #ea908a; -fx-text-fill: black;");
        cancel.setOnAction(e -> {
            accepted = false;
            close();
        });
        cancel.setMaxWidth(100); 
           
        HBox acceptBox = new HBox(accept);
        acceptBox.setAlignment(Pos.CENTER_LEFT);
        
        HBox cancelBox = new HBox(cancel);
        cancelBox.setAlignment(Pos.CENTER_RIGHT);
        
        grid.add(label, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(errorLabel, 0, 1, 2, 1);
        grid.add(acceptBox, 0, 2); 
        grid.add(cancelBox, 1, 2);

        
        Scene scene = new Scene(grid, 350, 160); 
        setScene(scene);
    }

    public boolean showDialog() {
        showAndWait();
        return accepted;
    }

    private void updateState() {
        if (nameField.getText().isEmpty()) {
            errorLabel.setText(dialogBundle.getString("name"));
            accept.setDisable(true);
        } else {
            errorLabel.setText("");
            accept.setDisable(false);
        }
    }

    public String getConfigurationName() {
        return nameField.getText();
    }

    public static void main(String[] args) {
        javafx.application.Application.launch(TestMouseCapture.class);
    }
}
