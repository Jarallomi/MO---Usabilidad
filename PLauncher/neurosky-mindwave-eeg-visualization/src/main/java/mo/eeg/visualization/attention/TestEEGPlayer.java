package mo.eeg.visualization.attention;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;

public class TestEEGPlayer extends Application {

    @Override
    public void start(Stage primaryStage) {
        File testFile = new File("C:\\Users\\Jarallo\\Desktop\\Clases\\TESIS\\Test Files\\Neurosky\\test3_mindwave.txt");

        EEGPlayer player = new EEGPlayer(testFile);

        // Botones de control
        Button playButton = new Button("Play");
        playButton.setOnAction(e -> player.play(player.getStart()));

        Button pauseButton = new Button("Pause");
        pauseButton.setOnAction(e -> player.pause());

        Button stopButton = new Button("Stop");
        stopButton.setOnAction(e -> player.stop()); // Llama al método stop()

        // Diseño de la interfaz
        HBox controlBox = new HBox(10, playButton, pauseButton, stopButton);
        controlBox.setStyle("-fx-padding: 10; -fx-alignment: center;");

        BorderPane root = new BorderPane();
        root.setCenter(player.getPaneNode());
        root.setBottom(controlBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Resizable EEG Player Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
