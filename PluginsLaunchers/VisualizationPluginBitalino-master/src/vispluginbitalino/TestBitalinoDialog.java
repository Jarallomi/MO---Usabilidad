package vispluginbitalino;

import javafx.application.Application;
import javafx.stage.Stage;
import mo.organization.ProjectOrganization;

public class TestBitalinoDialog extends Application {

    @Override
    public void start(Stage primaryStage) {
        ProjectOrganization organization = new ProjectOrganization("C:\\MO-Autoactualizado\\build\\libs\\newproject");

        BitalinoDialog dialog = new BitalinoDialog(organization);

        boolean accepted = dialog.showDialog();

        if (accepted) {
            System.out.println("Configuración aceptada:");
            System.out.println("Nombre de configuración: " + dialog.getConfigurationName());
            System.out.println("Sensor seleccionado: " + dialog.getSensorRec());
        } else {
            System.out.println("Diálogo cancelado.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
