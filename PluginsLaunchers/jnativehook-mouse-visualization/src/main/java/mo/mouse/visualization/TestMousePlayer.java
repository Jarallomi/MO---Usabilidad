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
        // Ruta del archivo que contiene los eventos del mouse
        File existingFile = new File("C:\\Users\\Jarallo\\Desktop\\Clases\\TESIS\\Test Files\\Mouse\\testeo_MS.txt");

        // Instancia de MousePlayer con el archivo proporcionado
        mousePlayer = new MousePlayer(existingFile);

        // Botón para iniciar la reproducción
        Button playButton = new Button("Play");
        playButton.setOnAction(event -> mousePlayer.play(mousePlayer.getStart()));

        // Botón para pausar la reproducción
        Button pauseButton = new Button("Pause");
        pauseButton.setOnAction(event -> mousePlayer.pause());

        // Botón para buscar el punto medio de la reproducción
        Button seekButton = new Button("Seek to Middle");
        seekButton.setOnAction(event -> mousePlayer.seek((mousePlayer.getStart() + mousePlayer.getEnd()) / 2));

        // Contenedor principal
        VBox vbox = new VBox(10, playButton, pauseButton, seekButton);
        vbox.setPadding(new Insets(10));

        // Se añade el panel del MousePlayer al VBox
        vbox.getChildren().add(mousePlayer.getPaneNode());

        // Configuración de la escena principal
        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setTitle("Test MousePlayer Controls");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
