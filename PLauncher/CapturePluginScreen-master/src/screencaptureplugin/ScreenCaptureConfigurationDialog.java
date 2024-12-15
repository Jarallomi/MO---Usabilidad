package screencaptureplugin;

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

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ScreenCaptureConfigurationDialog extends Stage {

    private Label errorLabel;
    private TextField nameField;
    private Spinner<Integer> sFPS;
    private ComboBox<String> cbDIM;
    private ComboBox<String> cbPantalla;
    private Button accept;
    private Button cancel;
    
    private int selectedWidth;
    private int selectedHeight;


    private boolean accepted = false;
    private int fps_option;
    private int dim_option;
    private int pantalla_option;
    private final ProjectOrganization org;
    ResourceBundle dialogBundle = ResourceBundle.getBundle("properties/principal");

    public ScreenCaptureConfigurationDialog(ProjectOrganization organization) {
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

        Label nameLabel = new Label(dialogBundle.getString("configuration_n"));
        grid.add(nameLabel, 0, 0);

        nameField = new TextField();
        nameField.setMaxWidth(270);
        nameField.textProperty().addListener((observable, oldValue, newValue) -> updateState());
        grid.add(nameField, 1, 0);

        Label fpsLabel = new Label("FPS: ");
        grid.add(fpsLabel, 0, 1);

        sFPS = new Spinner<>(5, 45, 5, 1);
        sFPS.setEditable(true);
        sFPS.setMaxWidth(270);
        grid.add(sFPS, 1, 1);

        Label dimLabel = new Label("Dimension: ");
        grid.add(dimLabel, 0, 2);

        cbDIM = new ComboBox<>();
        cbDIM.setMaxWidth(270);
        grid.add(cbDIM, 1, 2);

        Label screenLabel = new Label(dialogBundle.getString("screen"));
        grid.add(screenLabel, 0, 3);

        cbPantalla = new ComboBox<>();
        cbPantalla.getItems().addAll(getScreenOptions());
        cbPantalla.setMaxWidth(270);
        cbPantalla.getSelectionModel().selectFirst();
        cbPantalla.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> updateScreenDimensions(newVal.intValue()));
        grid.add(cbPantalla, 1, 3);

        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        GridPane.setColumnSpan(errorLabel, 2);
        grid.add(errorLabel, 0, 4);

        accept = new Button(dialogBundle.getString("accept"));
        accept.setStyle("-fx-background-color: #b4eda6; -fx-text-fill: black;");
        accept.setDisable(true);
        accept.setOnAction(e -> {
            accepted = true;
            fps_option = sFPS.getValue();
            dim_option = cbDIM.getSelectionModel().getSelectedIndex();
            pantalla_option = cbPantalla.getSelectionModel().getSelectedIndex();
            close();
        });

        cancel = new Button(dialogBundle.getString("cancel"));
        cancel.setStyle("-fx-background-color: #ea908a; -fx-text-fill: black;");
        cancel.setOnAction(e -> {
            accepted = false;
            close();
        });

        HBox buttonBox = new HBox(10, accept, cancel);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);
        grid.add(buttonBox, 0, 5);

        Scene scene = new Scene(grid, 330, 230);
        setScene(scene);

        updateScreenDimensions(cbPantalla.getSelectionModel().getSelectedIndex());
    }
    
    private void updateScreenDimensions(int screenIndex) {
        cbDIM.getItems().clear();
        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

        if (devices.length == 0) {
            return;
        }

        if (screenIndex == 0 && devices.length > 1) { // Pantalla extendida
            cbDIM.getItems().add("1920x1080");
            selectedWidth = 1920;
            selectedHeight = 1080;
            cbDIM.getSelectionModel().selectFirst();
        } else if (screenIndex > 0 && screenIndex <= devices.length) {
            GraphicsDevice selectedDevice = devices[screenIndex - 1];
            Rectangle bounds = selectedDevice.getDefaultConfiguration().getBounds();
            int width = bounds.width;
            int height = bounds.height;

            cbDIM.getItems().add(width + "x" + height);
            cbDIM.getItems().add("1366x768");
            cbDIM.getItems().add("1280x720");
            cbDIM.getItems().add("1024x768");
            cbDIM.getItems().add("800x600");

            cbDIM.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                String[] dimensions = newVal.split("x");
                selectedWidth = Integer.parseInt(dimensions[0]);
                selectedHeight = Integer.parseInt(dimensions[1]);
            });

            cbDIM.getSelectionModel().selectFirst();
        }
    }

    
    public int getSelectedWidth() {
        String dimension = cbDIM.getValue();
        return Integer.parseInt(dimension.split("x")[0]);
    }

    public int getSelectedHeight() {
        String dimension = cbDIM.getValue();
        return Integer.parseInt(dimension.split("x")[1]);
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

    private List<String> getScreenOptions() {
        List<String> screens = new ArrayList<>();
        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

        if (devices.length > 1) {
            screens.add(dialogBundle.getString("extended")); // Opción para pantallas extendidas
        }
        for (int i = 0; i < devices.length; i++) {
            screens.add(dialogBundle.getString("screen_option") + " " + (i + 1));
        }
        return screens;
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

    public int getFpsOption() {
        return fps_option;
    }

    public int getDimensionOption() {
        return dim_option;
    }

    public int getScreenOption() {
        return pantalla_option;
    }
}
