package audiocaptureplugin;

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
import javafx.stage.Window;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import java.util.ResourceBundle;

public class AudioCaptureConfigurationDialog extends Stage {

    private final Label errorLabel;
    private final TextField nameField;
    private final ComboBox<String> cbMic;
    private final Spinner<Integer> sSR;
    private final Button accept;
    private final Button cancel;

    private boolean accepted = false;
    private int op_mic;
    private int SR;
    
    /*
    //test to try different lenguages on the tab
    Locale idiom = new Locale("en", "EN");
    ResourceBundle dialogBundle = ResourceBundle.getBundle("properties/principal", idiom);
    */
    
    ResourceBundle dialogBundle = ResourceBundle.getBundle("properties/principal");

    public AudioCaptureConfigurationDialog(Window owner) {
        setTitle(dialogBundle.getString("title"));
        
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15, 15, 10, 15));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: #d6cfcf;");
        grid.setAlignment(Pos.CENTER);

        Label nameLabel = new Label(dialogBundle.getString("configuration_n"));
        

        nameField = new TextField();
        nameField.setMaxWidth(270);
        nameField.textProperty().addListener((observable, oldValue, newValue) -> updateState());
        

        Label micLabel = new Label(dialogBundle.getString("select_d"));
        

        cbMic = new ComboBox<>();
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfos) {
            Mixer m = AudioSystem.getMixer(info);
            Line.Info[] lineInfos = m.getTargetLineInfo();
            if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(TargetDataLine.class)) {
                cbMic.getItems().add(info.getName());
            }
        }
        if (!cbMic.getItems().isEmpty()) cbMic.getSelectionModel().selectFirst();
        cbMic.setMaxWidth(270);
        

        Label sampleRateLabel = new Label(dialogBundle.getString("select_sr"));
        

        sSR = new Spinner<>(4000, 120000, 8000, 250);
        sSR.setEditable(true);
        sSR.setMaxWidth(270);
        

        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        GridPane.setColumnSpan(errorLabel, 2);
        

        accept = new Button(dialogBundle.getString("accept"));
        accept.setStyle("-fx-background-color: #b4eda6; -fx-text-fill: black;");
        accept.setDisable(true);
        accept.setOnAction(e -> {
            accepted = true;
            op_mic = cbMic.getSelectionModel().getSelectedIndex();
            SR = sSR.getValue();
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
        grid.add(micLabel, 0, 1);
        grid.add(nameField, 1, 0);
        grid.add(cbMic, 1, 1);
        grid.add(sampleRateLabel, 0, 2);
        grid.add(sSR, 1, 2);
        grid.add(errorLabel, 0, 3);
        grid.add(acceptBox, 0, 4);
        grid.add(cancelBox, 1, 4);

        Scene scene = new Scene(grid, 460, 200);
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

    public int getSelectedMic() {
        return op_mic;
    }

    public int getSampleRate() {
        return SR;
    }
}
