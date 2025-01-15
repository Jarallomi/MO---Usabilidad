package audiocaptureplugin;

import javafx.application.Application;
import javafx.stage.Stage;

public class TestAudioCaptureConfigurationDialog extends Application {

    @Override
    public void start(Stage primaryStage) {
        AudioCaptureConfigurationDialog dialog = new AudioCaptureConfigurationDialog(primaryStage);

        
        boolean accepted = dialog.isAccepted();

        if (accepted) {
            System.out.println("Configuración aceptada:");
            System.out.println("Nombre de configuración: " + dialog.getConfigurationName());
            System.out.println("Micrófono seleccionado: " + dialog.getSelectedMic());
            System.out.println("Tasa de muestreo: " + dialog.getSampleRate());
        } else {
            System.out.println("Configuración cancelada por el usuario.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
