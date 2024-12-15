package webcamvisualizationplugin;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;

public class TestPanelCamara extends Application {

    @Override
    public void start(Stage primaryStage) {
        File videoFile = new File("C:\\MO-Autoactualizado\\build\\libs\\newprojectCAMERA\\participant-1\\capture\\2024-11-18_23.06.36.357_Camarita.mp4");

        PanelCamara panelCamara = new PanelCamara(videoFile);

        
        Scene scene = new Scene(panelCamara, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Test PanelCamara");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
