package mo.eeg.capture;

import javafx.application.Application;
import javafx.stage.Stage;
import mo.organization.ProjectOrganization;

public class TestEEGConfigurationDialog extends Application {

    @Override
    public void start(Stage primaryStage) {
        ProjectOrganization mockOrg = new ProjectOrganization("Mock Project");
        EEGConfigurationDialog dialog = new EEGConfigurationDialog(mockOrg);
        boolean accepted = dialog.showDialog();

        if (accepted) {
            System.out.println("Configuración aceptada: " + dialog.getConfigurationName());
        } else {
            System.out.println("Configuración cancelada.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
