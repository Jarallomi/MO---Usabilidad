package mo.eeg.visualization.attention;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class LiveWave extends BorderPane {

    private final Canvas canvas;
    private final GraphicsContext graphics;

    private int whiteSpaceWidth = 50; // Espacio derecho para valores
    private int pointDistance = 10;  // Distancia entre puntos consecutivos

    private long lastTimestamp = 0;

    private double prevX = 0;           // Última posición X
    private double prevY = 0;           // Última posición Y

    private final List<Variable> variables;

    public LiveWave() {
        canvas = new Canvas(500, 200); // Canvas inicial
        graphics = canvas.getGraphicsContext2D();
        clearCanvas();

        variables = new ArrayList<>();

        // Agregar el Canvas al centro del BorderPane
        this.setCenter(canvas);

        // Listeners para ajustar el Canvas al tamaño del contenedor
        this.widthProperty().addListener((observable, oldValue, newValue) -> {
            canvas.setWidth(newValue.doubleValue());
            redraw();
        });
        this.heightProperty().addListener((observable, oldValue, newValue) -> {
            canvas.setHeight(newValue.doubleValue());
            redraw();
        });
    }

    public void addData(String variableName, long timestamp, double value) {
        for (Variable variable : variables) {
            if (variable.name.equals(variableName)) {
                draw(variable, timestamp, value);
            }
        }
    }

    private void draw(Variable variable, long timestamp, double value) {
        // Desplazar el canvas hacia la izquierda si el timestamp avanza
        if (timestamp > lastTimestamp) {
            graphics.drawImage(canvas.snapshot(null, null), -pointDistance, 0);
            clearRightMargin();
        }

        lastTimestamp = timestamp;

        // Mapear el valor al rango de altura del canvas
        double scaledValue = (value - variable.min) / (variable.max - variable.min);
        double mappedValue = scaledValue * canvas.getHeight();
        double inverted = canvas.getHeight() - mappedValue;

        // Configurar color de la línea
        graphics.setStroke(variable.color);
        graphics.setLineWidth(1);

        double x = canvas.getWidth() - whiteSpaceWidth;
        double y = inverted;

        // Dibujar una línea entre el punto previo y el actual
        if (prevX != 0 || prevY != 0) {
            graphics.strokeLine(prevX, prevY, x, y);
        }
        prevX = x - pointDistance;
        prevY = y;

        // Limpiar y dibujar los valores de atención y tiempo
        drawValues(value, timestamp);
    }

    public void addVariable(String name, double min, double max, Color color) {
        for (Variable variable : variables) {
            if (variable.name.equals(name)) {
                return; // No agregar si ya existe
            }
        }
        variables.add(new Variable(name, min, max, color));
    }

    public void clear() {
        clearCanvas(); // Limpia el canvas completamente
        prevX = 0;
        prevY = 0;
        lastTimestamp = 0; // Reinicia el tiempo
    }


    private void clearCanvas() {
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphics.setStroke(Color.BLACK);
    }

    private void clearRightMargin() {
        graphics.setFill(Color.WHITE);
        graphics.fillRect(canvas.getWidth() - whiteSpaceWidth, 0, whiteSpaceWidth, canvas.getHeight());
    }

    private void drawValues(double value, long timestamp) {
        // Limpiar el área de texto antes de escribir los nuevos valores
        graphics.setFill(Color.WHITE);
        graphics.fillRect(0, canvas.getHeight() - 50, canvas.getWidth(), 50); // Limpiar la sección inferior

        // Dibujar el valor y el timestamp
        graphics.setFill(Color.BLUE);
        graphics.fillText(String.format("%.1f", value), 10, canvas.getHeight() - 30);
        graphics.fillText(String.format("%d", timestamp), 10, canvas.getHeight() - 10);
    }

    private void redraw() {
        clearCanvas(); // Limpiar el canvas al redimensionarlo
    }

    private static class Variable {

        String name;
        double min;
        double max;
        Color color;

        public Variable(String name, double min, double max, Color color) {
            this.name = name;
            this.min = min;
            this.max = max;
            this.color = color;
        }
    }
}
