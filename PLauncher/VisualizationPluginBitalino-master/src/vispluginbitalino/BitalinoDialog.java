package vispluginbitalino;

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

public class BitalinoDialog extends Stage {

    private Label errorLabel;
    private TextField nameField;
    private Button accept;
    private Button cancel;
    private RadioButton EMG, ECG, EDA;
    private ToggleGroup sensorGroup;
    private boolean accepted = false;
    public int sensor_rec;
    private ProjectOrganization org;

    ResourceBundle dialogBundle = ResourceBundle.getBundle("properties/principal");

    public BitalinoDialog(ProjectOrganization organization) {
        setTitle(dialogBundle.getString("title"));
        initModality(Modality.APPLICATION_MODAL);
        org = organization;

        initUI();
    }

    private void initUI() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: #d6cfcf;");

        Label label = new Label(dialogBundle.getString("configuration_n"));
        grid.add(label, 0, 0);

        nameField = new TextField();
        nameField.setMaxWidth(200);
        nameField.textProperty().addListener((observable, oldValue, newValue) -> updateState());
        grid.add(nameField, 1, 0, 2, 1);

        Label sensores = new Label(dialogBundle.getString("sens"));
        grid.add(sensores, 0, 1);

        // Crear RadioButtons con un ToggleGroup
        sensorGroup = new ToggleGroup();
        ECG = new RadioButton("ECG");
        EMG = new RadioButton("EMG");
        EDA = new RadioButton("EDA");

        ECG.setToggleGroup(sensorGroup);
        EMG.setToggleGroup(sensorGroup);
        EDA.setToggleGroup(sensorGroup);

        HBox sensorBox = new HBox(10, ECG, EMG, EDA);
        sensorBox.setAlignment(Pos.CENTER_LEFT);
        grid.add(sensorBox, 1, 1, 2, 1);

        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        grid.add(errorLabel, 0, 3, 4, 1);

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

        grid.add(acceptBox, 0, 4);
        grid.add(cancelBox, 2, 4);

        Scene scene = new Scene(grid, 400, 200);
        setScene(scene);
    }

    private void handleAccept() {
        accepted = true;
        sensor_rec = 0;

        // Obtener el sensor seleccionado
        if (ECG.isSelected()) {
            sensor_rec = 1;
        }
        if (EMG.isSelected()) {
            sensor_rec = 2;
        }
        if (EDA.isSelected()) {
            sensor_rec = 3;
        }

        close();
    }

    private void updateState() {
        if (nameField.getText().isEmpty()) {
            errorLabel.setText(dialogBundle.getString("name"));
            accept.setDisable(true);
        } else if (sensorGroup.getSelectedToggle() == null) { // Ningún sensor seleccionado
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

    public String getConfigurationName() {
        return nameField.getText();
    }

    public int getSensorRec() {
        return sensor_rec;
    }
}
