package mo.eeg.visualization.attention;

import javafx.application.Application;
import javafx.stage.Stage;
import mo.organization.ProjectOrganization;

public class TestAttConfigDialog extends Application {

    @Override
    public void start(Stage primaryStage) {
        ProjectOrganization mockOrg = new ProjectOrganization("Mock Project");
        AttConfigDialog dialog = new AttConfigDialog(mockOrg);
        boolean accepted = dialog.showDialog();

        if (accepted) {
            System.out.println("Configuraci�n aceptada: " + dialog.getConfigurationName());
        } else {
            System.out.println("Configuraci�n cancelada.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
