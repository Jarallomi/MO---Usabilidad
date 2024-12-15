package mo.mouse.visualization;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;

public class TestMousePlayer extends Application {

    private MousePlayer mousePlayer;

    @Override
    public void start(Stage primaryStage) {
        File existingFile = new File("C:\\Users\\Jarallo\\Desktop\\Clases\\TESIS\\Test Files\\Mouse\\testeo_MS.txt");

        mousePlayer = new MousePlayer(existingFile);

        Button playButton = new Button("Play");
        playButton.setOnAction(event -> mousePlayer.play(mousePlayer.getStart()));

        Button pauseButton = new Button("Pause");
        pauseButton.setOnAction(event -> mousePlayer.pause());

        Button seekButton = new Button("Seek to Middle");
        seekButton.setOnAction(event -> mousePlayer.seek((mousePlayer.getStart() + mousePlayer.getEnd()) / 2));

        VBox vbox = new VBox(10, playButton, pauseButton, seekButton);
        vbox.setPadding(new Insets(10));
        Scene scene = new Scene(vbox, 200, 150);

        primaryStage.setTitle("Test MousePlayer Controls");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
