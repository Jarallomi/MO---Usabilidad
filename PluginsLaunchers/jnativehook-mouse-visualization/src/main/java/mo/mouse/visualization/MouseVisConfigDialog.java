package mo.mouse.visualization;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mo.organization.ProjectOrganization;

public class MouseVisConfigDialog extends Application {

    private Label errorLabel;
    private TextField nameField;
    private Button accept;
    private Button cancel;
    private boolean accepted = false;
    private ProjectOrganization org;
    
    /*
    //test to try different lenguages on the tab
    Locale idiom = new Locale("en", "EN");
    ResourceBundle dialogBundle = ResourceBundle.getBundle("properties/principal", idiom);
    */
    
    ResourceBundle dialogBundle = ResourceBundle.getBundle("properties/principal");


    public MouseVisConfigDialog() {}

    public MouseVisConfigDialog(ProjectOrganization organization) {
        this.org = organization;
    }

    public boolean showDialog(Stage parentStage) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(dialogBundle.getString("title"));
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(parentStage);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20)); 
        grid.setVgap(10); 
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: #d6cfcf;");
        grid.setAlignment(Pos.CENTER);

        
        Label label = new Label(dialogBundle.getString("configuration_n"));
        
        
        nameField = new TextField();
        nameField.textProperty().addListener((observable, oldValue, newValue) -> updateState());
        
        
        errorLabel = new Label();
        errorLabel.setTextFill(javafx.scene.paint.Color.RED);

        
        accept = new Button(dialogBundle.getString("accept"));
        accept.setStyle("-fx-background-color: #b4eda6; -fx-text-fill: black;");
        accept.setDisable(true);
        accept.setOnAction(e -> {
            accepted = true;
            dialogStage.close();
        });
        

        
        cancel = new Button(dialogBundle.getString("cancel"));
        cancel.setStyle("-fx-background-color: #ea908a; -fx-text-fill: black;");
        cancel.setOnAction(e -> {
            accepted = false;
            dialogStage.close();
        });
        
        
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
        dialogStage.setScene(scene);
        dialogStage.setResizable(true); 
        dialogStage.showAndWait();

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

    @Override
    public void start(Stage primaryStage) {
        ProjectOrganization o = new ProjectOrganization("C:\\MO-Autoactualizado\\build\\libs\\newproject");
        MouseVisConfigDialog dialog = new MouseVisConfigDialog(o);
        boolean result = dialog.showDialog(primaryStage);
        System.out.println("Dialog accepted: " + result);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
