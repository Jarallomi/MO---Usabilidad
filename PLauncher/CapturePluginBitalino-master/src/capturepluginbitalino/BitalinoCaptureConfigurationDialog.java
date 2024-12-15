package capturepluginbitalino;

import java.util.Locale;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mo.organization.ProjectOrganization;
import java.util.ResourceBundle;

public class BitalinoCaptureConfigurationDialog extends Stage {

    private final Label errorLabel;
    private final Label srValue;
    private final Slider sSR;
    private final TextField nameField;
    private final Button accept;
    private final Button cancel;
    private final CheckBox EMG, ECG, EDA;
    private boolean accepted = false;
    private int sensor_rec;
    private int SR;
    private final ProjectOrganization org;
    
    /*
    //test to try different lenguages on the tab
    Locale idiom = new Locale("en", "EN");
    ResourceBundle dialogBundle = ResourceBundle.getBundle("properties/principal", idiom);
    */
    
    ResourceBundle dialogBundle = ResourceBundle.getBundle("properties/principal");

    public BitalinoCaptureConfigurationDialog(ProjectOrganization organization) {
        setTitle(dialogBundle.getString("title"));
        initModality(Modality.APPLICATION_MODAL);
        org = organization;

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: #d6cfcf;");

        Label label = new Label(dialogBundle.getString("configuration_n"));
        
        nameField = new TextField();
        nameField.setMaxWidth(200);
        nameField.textProperty().addListener((observable, oldValue, newValue) -> updateState());

        Label sensores = new Label(dialogBundle.getString("sens"));

        ECG = new CheckBox("ECG");
        EMG = new CheckBox("EMG");
        EDA = new CheckBox("EDA");
        HBox sensorBox = new HBox(10, ECG, EMG, EDA);

        Label samplerate = new Label(dialogBundle.getString("sr"));

        sSR = new Slider(0, 100, 100);
        srValue = new Label("100");
        sSR.valueProperty().addListener((observable, oldValue, newValue) -> srValue.setText(String.valueOf(newValue.intValue())));

        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        accept = new Button(dialogBundle.getString("accept"));
        accept.setStyle("-fx-background-color: #b4eda6; -fx-text-fill: black;");
        accept.setDisable(true);
        accept.setOnAction(e -> handleAccept());

        cancel = new Button(dialogBundle.getString("cancel"));
        cancel.setStyle("-fx-background-color: #ea908a; -fx-text-fill: black;");
        cancel.setOnAction(e -> {
            accepted = false;
            close();
        });
        
        HBox acceptBox = new HBox(accept);
        acceptBox.setAlignment(Pos.CENTER_LEFT);

        HBox cancelBox = new HBox(cancel);
        cancelBox.setAlignment(Pos.CENTER_RIGHT);
        
        grid.add(label, 0, 0);
        grid.add(nameField, 1, 0, 2, 1);
        grid.add(sensores, 0, 1);
        grid.add(sensorBox, 1, 1, 2, 1);
        grid.add(samplerate, 0, 2);
        grid.add(sSR, 1, 2, 2, 1);
        grid.add(srValue, 3, 2);
        grid.add(errorLabel, 0, 3, 4, 1);
        grid.add(acceptBox, 0, 4, 4, 1);
        grid.add(cancelBox, 1, 4, 4, 1);

        Scene scene = new Scene(grid, 400, 200);
        setScene(scene);
    }

    private void handleAccept() {
        accepted = true;
        sensor_rec = 0;
        if (ECG.isSelected()) sensor_rec += 1;
        if (EMG.isSelected()) sensor_rec += 10;
        if (EDA.isSelected()) sensor_rec += 100;
        SR = (int) sSR.getValue();
        close();
    }

    private void updateState() {
        if (nameField.getText().isEmpty()) {
            errorLabel.setText(dialogBundle.getString("name"));
            accept.setDisable(true);
        } else if (!ECG.isSelected() && !EMG.isSelected() && !EDA.isSelected()) {
            errorLabel.setText(dialogBundle.getString("sensor"));
            accept.setDisable(true);
        } else {
            errorLabel.setText("");
            accept.setDisable(false);
        }
    }

    public boolean showDialog() {
        showAndWait();
        return accepted;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getConfigurationName() {
        return nameField.getText();
    }

    public int getSensorRec() {
        return sensor_rec;
    }

    public int getSR() {
        return SR;
    }
}
