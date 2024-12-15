package webcamcaptureplugin;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import mo.organization.ProjectOrganization;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class WebcamCaptureConfigurationDialog extends Stage {

    private final Label errorLabel;
    private final TextField nameField;
    private final ComboBox<String> cbCamera;
    private final Spinner<Integer> sFPS;
    private final ComboBox<String> cbDIM;
    private final Button accept;
    private final Button cancel;

    private boolean accepted = false;
    private int id_camera;
    private int fps_option;
    private int dim_option;
    private final ProjectOrganization org;

    private static final WebcamDiscoveryService discovery = Webcam.getDiscoveryService();
    private static final List<Webcam> wCam = Webcam.getWebcams();
    ResourceBundle dialogBundle = ResourceBundle.getBundle("properties/principal");

    public WebcamCaptureConfigurationDialog(Window owner, ProjectOrganization organization) {
        setTitle(dialogBundle.getString("title"));
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
        org = organization;

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: #d6cfcf;");

        Label nameLabel = new Label(dialogBundle.getString("configuration_n"));
        
        nameField = new TextField();
        nameField.setMaxWidth(270);
        nameField.textProperty().addListener((observable, oldValue, newValue) -> updateState());
        
        Label webcamLabel = new Label(dialogBundle.getString("device"));
        
        String[] cameras = wCam.stream().map(Webcam::getName).toArray(String[]::new);
        cbCamera = new ComboBox<>();
        cbCamera.getItems().addAll(cameras);
        cbCamera.setMaxWidth(270);
        cbCamera.getSelectionModel().selectFirst();
        
        discovery.stop();

        Label fpsLabel = new Label("FPS: ");
        
        sFPS = new Spinner<>(5, 45, 5, 1);
        sFPS.setEditable(true);
        sFPS.setMaxWidth(270);
        
        Label dimLabel = new Label(dialogBundle.getString("resolution"));
        
        String[] dimensions = {"176x144", "320x240", "640x480", "800x600", "1024x768", "1280x720", "1366x768", "1920x1080"};
        cbDIM = new ComboBox<>();
        cbDIM.getItems().addAll(dimensions);
        cbDIM.setMaxWidth(270);
        cbDIM.getSelectionModel().selectFirst();
      
        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        GridPane.setColumnSpan(errorLabel, 2);
        
        accept = new Button(dialogBundle.getString("accept"));
        accept.setStyle("-fx-background-color: #b4eda6; -fx-text-fill: black;");
        accept.setDisable(true);
        accept.setOnAction(e -> {
            accepted = true;
            id_camera = cbCamera.getSelectionModel().getSelectedIndex();
            fps_option = sFPS.getValue();
            dim_option = cbDIM.getSelectionModel().getSelectedIndex();
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
        
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(webcamLabel, 0, 1);
        grid.add(cbCamera, 1, 1);
        grid.add(fpsLabel, 0, 2);
        grid.add(sFPS, 1, 2);
        grid.add(dimLabel, 0, 3);
        grid.add(cbDIM, 1, 3);
        grid.add(errorLabel, 0, 4);
        grid.add(acceptBox, 0, 5);
        grid.add(cancelBox, 1, 5);

        Scene scene = new Scene(grid, 470, 230);
        setScene(scene);
        showAndWait();
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

    public boolean isAccepted() {
        return accepted;
    }

    public String getConfigurationName() {
        return nameField.getText();
    }

    public int getSelectedCamera() {
        return id_camera;
    }

    public int getFPSOption() {
        return fps_option;
    }

    public int getDimensionOption() {
        return dim_option;
    }
}
