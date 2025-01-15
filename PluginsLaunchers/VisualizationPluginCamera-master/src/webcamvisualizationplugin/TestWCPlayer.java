package webcamvisualizationplugin;

import java.io.File;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestWCPlayer extends Application {

    private WCPlayer wcPlayer;

    @Override
    public void start(Stage primaryStage) {
        File videoFile = new File("C:\\Users\\Jarallo\\Desktop\\Clases\\TESIS\\Test Files\\Webcam\\camarita.mp4");

        wcPlayer = new WCPlayer(videoFile, "Test WCPlayer");

        System.out.println("Inicio: " + wcPlayer.getStart());
        System.out.println("Fin: " + wcPlayer.getEnd());

        Button playButton = new Button("Play");
        playButton.setOnAction(event -> {
            wcPlayer.play(wcPlayer.getStart());
        });

        Button pauseButton = new Button("Pause");
        pauseButton.setOnAction(event -> wcPlayer.pause());

        Button seekButton = new Button("Seek +1s");
        seekButton.setOnAction(event -> wcPlayer.seek(wcPlayer.getStart() + 1000));

        VBox vbox = new VBox(10, playButton, pauseButton, seekButton);
        vbox.setPadding(new Insets(10));
        Scene scene = new Scene(vbox, 200, 150);

        primaryStage.setTitle("Test WCPlayer Controls");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
