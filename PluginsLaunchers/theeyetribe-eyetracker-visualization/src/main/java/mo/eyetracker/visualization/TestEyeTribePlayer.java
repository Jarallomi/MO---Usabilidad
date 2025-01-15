package mo.eyetracker.visualization;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

public class TestEyeTribePlayer extends Application {

    private boolean isPlaying = false;
    private EyeTribeFixPlayer player;

    @Override
    public void start(Stage primaryStage) {
        // Archivo de prueba
        File testDataFile = new File("C:\\Users\\Jarallo\\Desktop\\Clases\\TESIS\\Test Files\\Eye Tracker\\testeeye2.txt");

        // Crear instancia del reproductor
        player = new EyeTribeFixPlayer(testDataFile);

        // Obtener el nodo de visualización proporcionado por EyeTribeFixPlayer
        AnchorPane visualizationPane = (AnchorPane) player.getPaneNode();
        visualizationPane.setStyle("-fx-background-color: #ffffff; -fx-border-color: lightgray; -fx-border-width: 1px;");

        // Ajustar las proporciones del panel al layout principal (manteniendo el ratio 16:9)
        visualizationPane.setPrefSize(800, 450); // Dimensiones iniciales
        visualizationPane.prefWidthProperty().bind(primaryStage.widthProperty().subtract(20));
        visualizationPane.prefHeightProperty().bind(visualizationPane.prefWidthProperty().multiply(9.0 / 16.0));

        // Botones de control
        Button playButton = new Button("Play");
        Button pauseButton = new Button("Pause");
        Button stopButton = new Button("Stop");

        // Configuración de los botones
        playButton.setOnAction(event -> {
            if (!isPlaying) {
                player.play(player.getStart()); // Inicia la reproducción
                isPlaying = true;
            }
        });

        pauseButton.setOnAction(event -> {
            player.pause(); // Pausa la reproducción
            isPlaying = false;
        });

        stopButton.setOnAction(event -> {
            player.stop(); // Detiene la reproducción
            isPlaying = false;
        });

        // Layout para los controles
        HBox controls = new HBox(10, playButton, pauseButton, stopButton);
        controls.setPadding(new Insets(10));
        controls.setStyle("-fx-background-color: #f4f4f4;");

        // Layout principal que combina visualización y controles
        VBox mainLayout = new VBox(10, visualizationPane, controls);
        mainLayout.setPadding(new Insets(10));

        // Configuración de la ventana principal
        primaryStage.setTitle("Eye Tribe Fixation Test");
        primaryStage.setScene(new Scene(mainLayout, 960, 540)); // Tamaño inicial (ratio 16:9)
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
