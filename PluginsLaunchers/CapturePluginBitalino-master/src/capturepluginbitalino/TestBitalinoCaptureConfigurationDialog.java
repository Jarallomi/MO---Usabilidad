package capturepluginbitalino;

import javafx.application.Application;
import javafx.stage.Stage;
import mo.organization.ProjectOrganization;

public class TestBitalinoCaptureConfigurationDialog extends Application {

    @Override
    public void start(Stage primaryStage) {
        ProjectOrganization organization = new ProjectOrganization("C:\\Users\\Jarallo\\Documents\\NetBeansProjects\\PluginLauncher\\build\\libs\\DefaultProject");

        BitalinoCaptureConfigurationDialog dialog = new BitalinoCaptureConfigurationDialog(organization);

        boolean accepted = dialog.showDialog();

        if (accepted) {
            System.out.println("Configuración aceptada:");
            System.out.println("Nombre de configuración: " + dialog.getConfigurationName());
            System.out.println("Sensor seleccionado: " + dialog.getSensorRec());
            System.out.println("Tasa de muestreo seleccionada: " + dialog.getSR());
        } else {
            System.out.println("Diálogo cancelado.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
