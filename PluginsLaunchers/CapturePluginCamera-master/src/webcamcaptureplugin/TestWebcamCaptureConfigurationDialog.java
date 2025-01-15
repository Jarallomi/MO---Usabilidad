package webcamcaptureplugin;

import javafx.application.Application;
import javafx.stage.Stage;
import mo.organization.ProjectOrganization;

public class TestWebcamCaptureConfigurationDialog extends Application {

    @Override
    public void start(Stage primaryStage) {
        ProjectOrganization organization = new ProjectOrganization("C:\\MO-Autoactualizado\\build\\libs\\newproject66");

        WebcamCaptureConfigurationDialog dialog = new WebcamCaptureConfigurationDialog(primaryStage, organization);
        
        boolean accepted = dialog.isAccepted();

        if (accepted) {
            System.out.println("Configuración aceptada:");
            System.out.println("Nombre de configuración: " + dialog.getConfigurationName());
            System.out.println("Cámara seleccionada: " + dialog.getSelectedCamera());
            System.out.println("FPS seleccionados: " + dialog.getFPSOption());
            System.out.println("Dimensión seleccionada: " + dialog.getDimensionOption());
        } else {
            System.out.println("Diálogo cancelado.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
