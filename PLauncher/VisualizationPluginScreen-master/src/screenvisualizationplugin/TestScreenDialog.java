package screenvisualizationplugin;

import javafx.application.Application;
import javafx.stage.Stage;
import mo.organization.ProjectOrganization;

public class TestScreenDialog extends Application {

    @Override
    public void start(Stage primaryStage) {
        ProjectOrganization organization = new ProjectOrganization("C:\\MO-Autoactualizado\\build\\libs\\newproject");

        ScreenDialog dialog = new ScreenDialog(organization);

        boolean accepted = dialog.showDialog();

        System.out.println("Accepted: " + accepted);
        System.out.println("Configuration Name: " + dialog.getConfigurationName());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
