package screenvisualizationplugin;

import java.io.File;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestScreenPlayer extends Application {

    private ScreenPlayer screenPlayer;

    @Override
    public void start(Stage primaryStage) {
        File videoFile = new File("C:\\Users\\Jarallo\\Desktop\\Clases\\TESIS\\Test Files\\Pantalla\\2024-12-13_03.55.58.544_aver.mp4");

        if (videoFile.exists()) {
            screenPlayer = new ScreenPlayer(videoFile, "Test ScreenPlayer");

            // Botones de control
            Button playButton = new Button("Play");
            playButton.setOnAction(event -> screenPlayer.play(screenPlayer.getStart()));

            Button pauseButton = new Button("Pause");
            pauseButton.setOnAction(event -> screenPlayer.pause());

            Button seekButton = new Button("Seek +1s");
            seekButton.setOnAction(event -> screenPlayer.seek(screenPlayer.getStart() + 1000));

            // Obteniendo el Panel de reproducción
            Panel screenPane = screenPlayer.getPaneNode();

            VBox controls = new VBox(10, playButton, pauseButton, seekButton);
            controls.setPadding(new Insets(10));

            VBox root = new VBox(10, screenPane, controls); // Integrando el Panel y los controles
            root.setPadding(new Insets(10));

            Scene scene = new Scene(root, 800, 600); // Ajustar el tamaño de la ventana
            primaryStage.setTitle("Test ScreenPlayer Controls");
            primaryStage.setScene(scene);
            primaryStage.show();
        } else {
            System.err.println("El archivo de video no existe: " + videoFile.getAbsolutePath());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
