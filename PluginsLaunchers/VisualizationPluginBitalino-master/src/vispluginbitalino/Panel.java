package vispluginbitalino;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.geometry.Insets;

public class Panel extends StackPane {

    private int escalaX = 20;
    private int escalaY = 10;
    private final int x0 = 15;
    private int y0;
    private int xp = 0;
    private long fin;
    private int sensor;
    public long start, end;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private long pausedMillis = 0;
    private Canvas canvas;
    private ScrollPane scrollPane;
    private ArrayList<Long> Tiempo = new ArrayList<>();
    private ArrayList<Long> Datos = new ArrayList<>();
    private Timeline timeline;
    ResourceBundle dialogBundle = ResourceBundle.getBundle("properties/principal");

    public Panel(File file, int sensor) {
        this.sensor = sensor;

        // Crear Canvas principal
        canvas = new Canvas(800, 400);

        // Cargar datos del archivo
        if (file != null && file.exists()) {
            loadFileData(file);
        }

        // Configurar ScrollPane
        scrollPane = new ScrollPane(canvas);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Ajustar tamaño dinámico del canvas
        scrollPane.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            canvas.setWidth(newBounds.getWidth());
            canvas.setHeight(newBounds.getHeight());
            redrawAll();
        });

        // Configurar título
        Label titleLabel = new Label(getSensorLabel(sensor));
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        StackPane.setAlignment(titleLabel, Pos.TOP_CENTER);

        // Configurar botones
        Button AmplitudU = createStyledButton("+", "#b4eda6");
        Button AmplitudD = createStyledButton("-", "#ea908a");
        Button LongitudU = createStyledButton("+", "#b4eda6");
        Button LongitudD = createStyledButton("-", "#ea908a");

        AmplitudU.setOnAction(e -> {
            setUEscalaY();
            redrawAll();
        });
        AmplitudD.setOnAction(e -> {
            setDEscalaY();
            redrawAll();
        });
        LongitudU.setOnAction(e -> {
            setUEscalaX();
            redrawAll();
        });
        LongitudD.setOnAction(e -> {
            setDEscalaX();
            redrawAll();
        });

        // Disposición de los botones
        GridPane controlGrid = new GridPane();
        controlGrid.add(AmplitudU, 1, 0);
        controlGrid.add(AmplitudD, 1, 2);
        controlGrid.add(LongitudU, 2, 1);
        controlGrid.add(LongitudD, 0, 1);
        controlGrid.setAlignment(Pos.TOP_LEFT);
        controlGrid.setHgap(5);
        controlGrid.setVgap(5);
        StackPane.setAlignment(controlGrid, Pos.TOP_LEFT);
        StackPane.setMargin(controlGrid, new Insets(50, 0, 0, 50));

        // Agregar elementos al StackPane
        getChildren().addAll(scrollPane, titleLabel, controlGrid);

        // Dibujar datos iniciales
        redrawAll();
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: black;");
        return button;
    }

    private String getSensorLabel(int sensor) {
        switch (sensor) {
            case 1:
                return dialogBundle.getString("ecd");
            case 2:
                return dialogBundle.getString("emg");
            case 3:
                return dialogBundle.getString("eda");
            default:
                return dialogBundle.getString("def");
        }
    }

    private void loadFileData(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > sensor) {
                    Tiempo.add(Long.parseLong(parts[0]));
                    Datos.add(Long.parseLong(parts[sensor]));
                }
            }
            start = Tiempo.get(0);
            end = Tiempo.get(Tiempo.size() - 1);
            fin = end - start;
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void setUEscalaX() {
        escalaX = Math.max(5, escalaX - 1);
    }

    private void setDEscalaX() {
        escalaX += 1;
    }

    private void setUEscalaY() {
        escalaY = Math.max(1, escalaY - 1);
    }

    private void setDEscalaY() {
        escalaY = Math.min(50, escalaY + 1);
    }

    private void redrawAll() {
        y0 = (int) (canvas.getHeight() * 0.8);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawAxes(gc);
        drawData(gc);
        drawProgressLine(gc);
    }

    private void drawAxes(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.strokeLine(x0, y0, canvas.getWidth(), y0);
        gc.strokeLine(x0, 0, x0, canvas.getHeight());
    }

    private void drawData(GraphicsContext gc) {
        gc.setStroke(getSensorColor(sensor));
        long xi, yi, xf, yf;
        for (int i = 0; i < Tiempo.size() - 1; i++) {
            xi = (Tiempo.get(i) - start) / escalaX;
            yi = Datos.get(i) / escalaY;
            xf = (Tiempo.get(i + 1) - start) / escalaX;
            yf = Datos.get(i + 1) / escalaY;
            gc.strokeLine(x0 + xi, y0 - yi, x0 + xf, y0 - yf);
        }
    }

    private void drawProgressLine(GraphicsContext gc) {
        gc.setFill(Color.ORANGE);
        gc.fillRect(x0 + xp, 0, 5, canvas.getHeight()); // Línea de progreso más ancha
    }

    private Color getSensorColor(int sensor) {
        switch (sensor) {
            case 1:
                return Color.BLUE;
            case 2:
                return Color.GREEN;
            case 3:
                return Color.RED;
            default:
                return Color.BLACK;
        }
    }

    public void play(long millis) {
        if (isPaused) {
            millis = pausedMillis;
            isPaused = false;
        }

        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) {
            timeline.stop();
        }

        xp = (int) ((millis - start) / escalaX);
        isPlaying = true;

        timeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            xp++;
            redrawAll();
            if (xp > (fin / escalaX)) {
                stop();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void pause() {
        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) {
            timeline.pause();
            pausedMillis = start + (long) xp * escalaX;
            isPaused = true;
        }
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
        xp = 0;
        pausedMillis = 0;
        isPaused = false;
        redrawAll();
    }
}
