package vispluginbitalino;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.File;

public class TestBitalinoPlayer extends Application {

    private BitalinoPlayer bitalinoPlayer;

    @Override
    public void start(Stage primaryStage) {
        // Ruta del archivo de datos
        File dataFile = new File("C:\\Users\\Jarallo\\Desktop\\Clases\\TESIS\\Test Files\\Bitalino\\testeo_EMG.txt");

        // Instanciar BitalinoPlayer
        bitalinoPlayer = new BitalinoPlayer(dataFile, 2, "Test BitalinoPlayer");

        // Crear botones de control
        Button playButton = new Button("Play");
        playButton.setOnAction(event -> bitalinoPlayer.play(bitalinoPlayer.getStart()));

        Button pauseButton = new Button("Pause");
        pauseButton.setOnAction(event -> bitalinoPlayer.pause());

        Button stopButton = new Button("Stop");
        stopButton.setOnAction(event -> bitalinoPlayer.stop());

        // Contenedor de botones
        VBox controls = new VBox(10, playButton, pauseButton, stopButton);
        controls.setPadding(new Insets(10));

        // Obtener el panel de visualización del BitalinoPlayer
        BorderPane root = new BorderPane();
        root.setCenter(bitalinoPlayer.getPaneNode()); // Añadir el panel al centro
        root.setBottom(controls); // Añadir controles al fondo

        // Configurar la escena
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Test BitalinoPlayer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
