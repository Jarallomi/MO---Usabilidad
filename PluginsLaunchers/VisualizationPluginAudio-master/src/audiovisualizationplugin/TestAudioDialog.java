package audiovisualizationplugin;

import javafx.application.Application;
import javafx.stage.Stage;
import mo.organization.ProjectOrganization;

public class TestAudioDialog extends Application {

    @Override
    public void start(Stage primaryStage) {
        ProjectOrganization organization = new ProjectOrganization("C:\\MO-Autoactualizado\\build\\libs\\newproject1");

        AudioDialog audioDialog = new AudioDialog(organization);

        boolean accepted = audioDialog.showDialog();

        System.out.println("Accepted: " + accepted);
        System.out.println("Configuration Name: " + audioDialog.getConfigurationName());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
