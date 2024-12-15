package mo.keyboard.visualization;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestKeyboardPlayer extends Application {

    private static final Logger logger = Logger.getLogger(TestKeyboardPlayer.class.getName());

    private KeyboardPlayer keyboardPlayer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            File keyboardFile = new File("C:\\Users\\Jarallo\\Desktop\\Clases\\TESIS\\Test Files\\Teclado\\testeo_TECLADO.txt");

            if (!keyboardFile.exists()) {
                logger.log(Level.SEVERE, "El archivo de teclado no existe: " + keyboardFile.getAbsolutePath());
                return;
            }

            keyboardPlayer = new KeyboardPlayer(keyboardFile);

            VBox root = new VBox(10);

            Button playButton = new Button("Play");
            playButton.setOnAction(e -> {
                if (keyboardPlayer != null) {
                    keyboardPlayer.play(keyboardPlayer.getStart());
                }
            });

            Button stopButton = new Button("Stop");
            stopButton.setOnAction(e -> {
                if (keyboardPlayer != null) {
                    keyboardPlayer.stop();
                }
            });

            root.getChildren().addAll(keyboardPlayer.getPaneNode(), playButton, stopButton);
            Scene scene = new Scene(root, 500, 400);

            primaryStage.setTitle("Test Keyboard Player");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al iniciar la prueba de KeyboardPlayer", e);
        }
    }
}
