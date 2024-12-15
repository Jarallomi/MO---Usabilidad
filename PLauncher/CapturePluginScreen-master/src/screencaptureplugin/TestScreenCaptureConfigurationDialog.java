package screencaptureplugin;

import javafx.application.Application;
import javafx.stage.Stage;
import mo.organization.ProjectOrganization;

public class TestScreenCaptureConfigurationDialog extends Application {

    @Override
    public void start(Stage primaryStage) {
        ProjectOrganization organization = new ProjectOrganization("C:\\MO-Autoactualizado\\build\\libs\\newproject");

        ScreenCaptureConfigurationDialog dialog = new ScreenCaptureConfigurationDialog(organization);

        boolean accepted = dialog.showDialog();

        if (accepted) {
            System.out.println("Configuración aceptada:");
            System.out.println("Nombre de configuración: " + dialog.getConfigurationName());
            System.out.println("FPS seleccionados: " + dialog.getFpsOption());
            System.out.println("Dimensión seleccionada: " + dialog.getDimensionOption());
            System.out.println("Pantalla seleccionada: " + dialog.getScreenOption());
        } else {
            System.out.println("Diálogo cancelado.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
