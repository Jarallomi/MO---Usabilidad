package webcamvisualizationplugin;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mo.organization.ProjectOrganization;


public class WCDialog extends Stage {

    private Label errorLabel;
    private TextField nameField;
    private Button accept;
    private boolean accepted = false;
    private ProjectOrganization org;
    ResourceBundle dialogBundle = ResourceBundle.getBundle("properties/principal");

    public WCDialog(ProjectOrganization organization) {
        setTitle(dialogBundle.getString("title"));
        initModality(Modality.APPLICATION_MODAL);
        org = organization;

        initUI();
    }

    private void initUI() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(15);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: #d6cfcf;");

        Label label = new Label(dialogBundle.getString("configuration_n"));
        nameField = new TextField();
        nameField.textProperty().addListener((observable, oldValue, newValue) -> updateState());

        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        accept = new Button(dialogBundle.getString("accept"));
        accept.setStyle("-fx-background-color: #b4eda6; -fx-text-fill: black;");
        accept.setDisable(true);
        accept.setOnAction(e -> {
            accepted = true;
            close();
        });

        Button cancel = new Button(dialogBundle.getString("cancel"));
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
        grid.add(nameField, 1, 0);
        grid.add(errorLabel, 0, 1, 2, 1);
        grid.add(acceptBox, 0, 2);
        grid.add(cancelBox, 1, 2);

        Scene scene = new Scene(grid, 350, 150);
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
}
