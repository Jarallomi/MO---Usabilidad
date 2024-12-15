package mo.eyetracker.capture;

import javafx.application.Application;
import javafx.stage.Stage;
import mo.organization.ProjectOrganization;

public class TestTheEyeTribeDialog extends Application {

    @Override
    public void start(Stage primaryStage) {
        ProjectOrganization organization = new ProjectOrganization("C:\\MO-Autoactualizado\\build\\libs\\newproject");

        TheEyeTribeDialog dialog = new TheEyeTribeDialog(organization);

        boolean accepted = dialog.showDialog();

        if (accepted) {
            System.out.println("Configuracion aceptada:");
            System.out.println("Nombre de configuracion: " + dialog.getConfigurationName());
        } else {
            System.out.println("Dialogo cancelado.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
