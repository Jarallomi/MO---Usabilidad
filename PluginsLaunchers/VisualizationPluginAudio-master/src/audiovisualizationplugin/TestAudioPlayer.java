package audiovisualizationplugin;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestAudioPlayer extends Application {

    private static final Logger logger = Logger.getLogger(TestAudioPlayer.class.getName());

    private AudioPlayer audioPlayer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Ruta del archivo de audio
            File audioFile = new File("C:\\Users\\Jarallo\\Desktop\\Clases\\TESIS\\Test Files\\Audio\\2024-12-07_21.52.47.534_Hola.wav");

            if (!audioFile.exists()) {
                logger.log(Level.SEVERE, "El archivo de audio no existe: " + audioFile.getAbsolutePath());
                return;
            }

            // Crear instancia de AudioPlayer
            audioPlayer = new AudioPlayer(audioFile);

            // Crear layout
            VBox root = new VBox(10);

            // Botón para reproducir
            Button playButton = new Button("Play");
            playButton.setOnAction(e -> {
                if (audioPlayer != null) {
                    audioPlayer.play(audioPlayer.getStart());
                }
            });

            // Botón para pausar
            Button pauseButton = new Button("Pause");
            pauseButton.setOnAction(e -> {
                if (audioPlayer != null) {
                    audioPlayer.pause();
                }
            });

            // Botón para detener
            Button stopButton = new Button("Stop");
            stopButton.setOnAction(e -> {
                if (audioPlayer != null) {
                    audioPlayer.stop();
                }
            });

            // Agregar nodos al layout
            root.getChildren().addAll(audioPlayer.getPaneNode(), playButton, pauseButton, stopButton);

            // Crear la escena
            Scene scene = new Scene(root, 800, 600);

            // Configurar y mostrar la ventana
            primaryStage.setTitle("Test Audio Player");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al iniciar la prueba de AudioPlayer", e);
        }
    }
}
