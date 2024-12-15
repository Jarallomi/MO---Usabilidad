package mo.eeg.visualization.attention;

//import com.theeyetribe.clientsdk.data.GazeData;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mo.visualization.Playable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import mo.eeg.data.EEGData;
import org.apache.commons.io.input.ReversedLinesFileReader;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class EEGPlayer implements Playable {

    private long start;
    private long end;
    private RandomAccessFile file;
    private EEGData current;
    private EEGData next;
    private LiveWave wave;
    private boolean stopped;
    private static final Logger logger = Logger.getLogger(EEGPlayer.class.getName());
    private Timeline playbackTimeline; // Nueva línea
    
    /*
    //test to try different lenguages on the tab
    Locale idiom = new Locale("en", "EN");
    ResourceBundle dialogBundle = ResourceBundle.getBundle("properties/principal", idiom);
    */
    
    ResourceBundle dialogBundle = ResourceBundle.getBundle("properties/principal");

    /*public EEGPlayer(File file) {
        try {
            readLastTime(file);
            this.file = new RandomAccessFile(file, "r");
            while (current == null) {
                current = next();
            }
            start = current.time;
            next = next();

            wave = new LiveWave();
            wave.addVariable("Att", 0, 100, Color.BLUE);

            Platform.runLater(() -> {
                Stage stage = new Stage();
                StackPane root = new StackPane();
                root.getChildren().add(wave.getCanvas());
                Scene scene = new Scene(root, 600, 400);
                stage.setTitle(dialogBundle.getString("title"));
                stage.setScene(scene);
                stage.show();
            });

        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private EEGData next() {
        try {
            String line = file.readLine();

            if (line == null || line.isEmpty()) {
                return null;
            }

            String[] dataParts = line.split(" ");

            if (dataParts.length == 2) {
                return null; //ignore blink data
            }

            if (dataParts.length == 12) {
                EEGData data = new EEGData();
                for (String dataPart : dataParts) {
                    String[] d = dataPart.split(":");
                    String name = d[0];
                    String value = d[1];
                    switch (name) {
                        case "t":
                            data.time = Long.parseLong(value);
                            break;

                        case "att":
                            data.eSense.attention = Byte.parseByte(value);
                            break;

                        default:
                            break;
                    }
                }
                return data;
            }
        } catch (IOException | NumberFormatException ex) {
            pause();
            logger.log(Level.SEVERE, null, ex);
            return null;
        }
        return null;
    }*/
    
    public EEGPlayer(File file) {
        try {
            if (!file.exists() || file.length() == 0) {
                throw new IOException("El archivo está vacío o no existe.");
            }

            readLastTime(file);
            this.file = new RandomAccessFile(file, "r");

            // Obtener el primer dato válido
            current = next();
            if (current == null) {
                throw new IOException("No se encontraron datos válidos en el archivo.");
            }
            start = current.time;
            next = next();

            wave = new LiveWave();
            wave.addVariable("Att", 0, 100, Color.BLUE);

            /*Platform.runLater(() -> {
                Stage stage = new Stage();
                StackPane root = new StackPane();
                root.getChildren().add(wave.getCanvas());
                Scene scene = new Scene(root, 600, 400);
                stage.setTitle(dialogBundle.getString("title"));
                stage.setScene(scene);
                stage.show();
            });*/

        } catch (IOException ex) {
            System.err.println("Error al inicializar EEGPlayer: " + ex.getMessage());
        }
    }
    
    public Node getPaneNode() {
        if (wave != null) {
            return wave; // Devuelve el LiveWave directamente
        } else {
            throw new IllegalStateException("LiveWave instance is not initialized.");
        }
    }




    

    private void readLastTime(File file) {
        try (ReversedLinesFileReader rev = new ReversedLinesFileReader(file, Charset.defaultCharset())) {
            String lastLine;
            do {
                lastLine = rev.readLine();
            } while (lastLine == null || lastLine.trim().isEmpty());
            end = parseTimestamp(lastLine);
        } catch (IOException ex) {
            System.err.println("Error al leer el último tiempo: " + ex.getMessage());
        }
    }

    private static long parseTimestamp(String str) {
        String[] parts = str.split(" ");
        if (parts.length == 2) {
            String[] timeData = parts[0].split(":");
            if (timeData.length == 2 && "t".equals(timeData[0])) {
                return Long.parseLong(timeData[1]);
            }
        }
        return -1;
    }

    @Override
    public void play(long millis) {
        if (current == null) { // Si no hay datos, reiniciar para empezar desde el principio
            try {
                file.seek(0); // Reiniciar la posición del archivo
                current = next(); // Reiniciar los datos
                next = next();
                wave.clear(); // Limpiar el gráfico
            } catch (IOException ex) {
                System.err.println("Error al reiniciar el archivo: " + ex.getMessage());
                return; // Salir si ocurre un error
            }
        }

        if (playbackTimeline == null || stopped) {
            if ((millis >= start) && (millis <= end)) {
                seek(millis);

                playbackTimeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
                    // Validar que wave y current no sean nulos antes de continuar
                    if (wave == null || current == null) {
                        stopPlayback();
                        return;
                    }

                    Platform.runLater(() -> {
                        if (current != null) {
                            wave.addData("Att", current.time, current.eSense.attention);
                        }
                    });

                    current = next(); // Avanzar al siguiente dato

                    if (current == null) { // Si no hay más datos, detener reproducción
                        stopPlayback();
                    }
                }));

                playbackTimeline.setCycleCount(Timeline.INDEFINITE);
                playbackTimeline.play();
                stopped = false; // Marca que no está detenido
            }
        } else {
            playbackTimeline.play(); // Reanuda desde donde se pausó
        }
    }


    private EEGData next() {
        try {
            String line = file.readLine();
            if (line == null || line.trim().isEmpty()) {
                return null; // Fin del archivo o línea vacía
            }

            String[] dataParts = line.trim().split(" ");
            if (dataParts.length != 2) {
                return null; // Formato incorrecto, ignorar
            }

            EEGData data = new EEGData();
            for (String dataPart : dataParts) {
                String[] keyValue = dataPart.split(":");
                if (keyValue.length != 2) {
                    continue; // Saltar datos mal formateados
                }

                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                switch (key) {
                    case "t":
                        data.time = Long.parseLong(value);
                        break;
                    case "att":
                        data.eSense.attention = Byte.parseByte(value);
                        break;
                    default:
                        break;
                }
            }
            return data.time > 0 ? data : null; // Retornar dato válido o null
        } catch (IOException | NumberFormatException ex) {
            System.err.println("Error al leer siguiente dato: " + ex.getMessage());
            return null; // En caso de error, retornar null
        }
    }




    private void stopPlayback() {
        if (playbackTimeline != null) {
            playbackTimeline.stop();
            playbackTimeline = null; // Liberar recursos
        }
        stopped = true;
    }

    @Override
    public void pause() {
        if (playbackTimeline != null) {
            playbackTimeline.pause(); // Detiene temporalmente la reproducción
        }
    }

    @Override
    public void stop() {
        if (playbackTimeline != null) {
            playbackTimeline.stop(); // Detiene la reproducción
            playbackTimeline = null;
        }

        // Limpia el canvas en LiveWave
        if (wave != null) {
            wave.clear();
        }

        // Reinicia el archivo y los datos
        try {
            file.seek(0); // Regresa al inicio del archivo
            current = next(); // Reinicia los datos
            next = next();
        } catch (IOException ex) {
            System.err.println("Error al reiniciar el archivo: " + ex.getMessage());
        }

        stopped = true; // Marca como detenido
    }


    @Override
    public void seek(long requestedMillis) {
        if (requestedMillis < start || requestedMillis > end) {
            return;
        }

        try {
            file.seek(0);
            current = next();
            while (current != null && current.time < requestedMillis) {
                current = next();
            }
        } catch (IOException ex) {
            System.err.println("Error al buscar en el archivo: " + ex.getMessage());
        }
    }

    @Override
    public long getStart() {
        return start;
    }

    @Override
    public long getEnd() {
        return end;
    }
}
