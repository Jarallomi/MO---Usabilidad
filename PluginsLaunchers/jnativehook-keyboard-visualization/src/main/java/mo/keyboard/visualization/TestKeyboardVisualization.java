package mo.keyboard.visualization;

import javafx.application.Application;
import javafx.stage.Stage;
import mo.organization.ProjectOrganization;

public class TestKeyboardVisualization extends Application {

    @Override
    public void start(Stage primaryStage) {
        ProjectOrganization organization = new ProjectOrganization("C:\\MO-Autoactualizado\\build\\libs\\newproject2");
        KBDialog dialog = new KBDialog(organization);
        boolean result = dialog.showDialog();
        System.out.println("Accepted: " + result);
        System.out.println("Configuration Name: " + dialog.getConfigurationName());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
