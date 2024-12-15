package webcamvisualizationplugin;

import javafx.application.Application;
import javafx.stage.Stage;
import mo.organization.ProjectOrganization;

public class TestWCDialog extends Application {

    @Override
    public void start(Stage primaryStage) {
        ProjectOrganization organization = new ProjectOrganization("C:\\MO-Autoactualizado\\build\\libs\\newproject");

        WCDialog dialog = new WCDialog(organization);
        
        boolean accepted = dialog.showDialog();

        System.out.println("Accepted: " + accepted);
        System.out.println("Configuration Name: " + dialog.getConfigurationName());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
