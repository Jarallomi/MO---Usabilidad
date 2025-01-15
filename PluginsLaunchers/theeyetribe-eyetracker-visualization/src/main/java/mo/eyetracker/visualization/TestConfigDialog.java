package mo.eyetracker.visualization;

import javafx.application.Application;
import javafx.stage.Stage;
import mo.organization.ProjectOrganization;

public class TestConfigDialog extends Application {

    @Override
    public void start(Stage primaryStage) {
        ProjectOrganization mockOrg = new ProjectOrganization("Mock Project");
        ConfigDialog dialog = new ConfigDialog(mockOrg);
        boolean accepted = dialog.showDialog();

        if (accepted) {
            System.out.println("Configuracion aceptada: " + dialog.getConfigurationName());
        } else {
            System.out.println("Configuracion cancelada.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
