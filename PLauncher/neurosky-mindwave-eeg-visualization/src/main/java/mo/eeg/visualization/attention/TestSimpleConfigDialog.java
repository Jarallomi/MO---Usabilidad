package mo.eeg.visualization.attention;

import javafx.application.Application;
import javafx.stage.Stage;
import mo.organization.ProjectOrganization;

public class TestSimpleConfigDialog extends Application {

    @Override
    public void start(Stage primaryStage) {
        ProjectOrganization mockOrg = new ProjectOrganization("Mock Project");
        SimpleConfigDialog dialog = new SimpleConfigDialog("Test Simple Config Dialog", mockOrg);
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
