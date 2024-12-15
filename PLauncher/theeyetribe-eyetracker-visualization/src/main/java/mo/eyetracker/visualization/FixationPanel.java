package mo.eyetracker.visualization;

import com.theeyetribe.clientsdk.data.GazeData;
import com.theeyetribe.clientsdk.data.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class FixationPanel extends AnchorPane {

    private final Canvas canvas;
    private final GraphicsContext gc;

    private int fixationsLimit = 50;
    private int diameterMaxLimit = 120;
    private int diameterMinLimit = 40;

    private boolean prevWasFixated = false;

    private Fixation first;
    private Fixation last;

    private int globalFixationsCount = 0;
    private int fixationsCount;

    private Color[] colors;
    private int[] dataColorIndices;
    private int[] sizes;

    private double originalWidth;
    private double originalHeight;

    public FixationPanel(int width, int height) {
        // Guardar el tamaño original
        this.originalWidth = width;
        this.originalHeight = height;

        // Inicializar el Canvas
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();

        // Añadir el Canvas al AnchorPane
        this.getChildren().add(canvas);
        AnchorPane.setTopAnchor(canvas, 0.0);
        AnchorPane.setLeftAnchor(canvas, 0.0);
        AnchorPane.setRightAnchor(canvas, 0.0);
        AnchorPane.setBottomAnchor(canvas, 0.0);

        // Vincular el tamaño del Canvas al tamaño del AnchorPane
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        // Redibujar el contenido cuando cambie el tamaño del Canvas
        canvas.widthProperty().addListener((obs, oldVal, newVal) -> redraw());
        canvas.heightProperty().addListener((obs, oldVal, newVal) -> redraw());

        // Colores gradientes para las fijaciones
        colors = new Color[]{
            Color.GREEN, Color.YELLOW, Color.ORANGE, Color.RED
        };

        reset();
    }

    public void reset() {
        prevWasFixated = false;
        first = last = null;
        globalFixationsCount = 0;
        fixationsCount = 0;

        clearCanvas();
    }

    private void clearCanvas() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void addGazeData(GazeData data) {
        if (first != null && data.timeStamp == first.firstTime) {
            reset();
        }

        if (data.isFixated && prevWasFixated) {
            accumulateFixation(data);
        } else if (data.isFixated && !prevWasFixated) {
            addNewFixation(data);
        }

        prevWasFixated = data.isFixated;
    }

    private void addNewFixation(GazeData data) {
        Fixation f = new Fixation();
        f.addPoint(data);
        f.addTime(data.timeStamp);

        if (last == null) {
            first = f;
        } else {
            last.next = f;
            f.prev = last;
        }

        last = f;

        fixationsCount++;
        globalFixationsCount++;
        f.number = globalFixationsCount;

        if (fixationsCount > fixationsLimit) {
            first = first.next;
            first.prev = null;
            fixationsCount--;
        }

        updateColorsAndSizes();
        redraw();
    }

    private void accumulateFixation(GazeData data) {
        if (last != null) {
            last.addTime(data.timeStamp);
            last.addPoint(data);
        }

        updateColorsAndSizes();
        redraw();
    }

    private void updateColorsAndSizes() {
        dataColorIndices = new int[fixationsLimit];
        sizes = new int[fixationsLimit];
        long minTime = 0, maxTime = 0;

        for (Fixation fix = first; fix != null; fix = fix.next) {
            maxTime = Math.max(maxTime, fix.ellapsedTime);
        }

        double timeRange = maxTime - minTime;

        int count = 0;
        for (Fixation fix = first; fix != null; fix = fix.next) {
            double norm = (fix.ellapsedTime - minTime) / timeRange;
            int index = (int) Math.floor(norm * (colors.length - 1));
            dataColorIndices[count] = index;

            int size = (int) (fix.ellapsedTime * (diameterMaxLimit - diameterMinLimit)
                    / timeRange) + diameterMinLimit;
            sizes[count] = size;

            count++;
        }
    }

    private void redraw() {
        clearCanvas();

        double widthScale = canvas.getWidth() / originalWidth;
        double heightScale = canvas.getHeight() / originalHeight;

        // Dibujar las conexiones entre fijaciones
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(3);

        Fixation current = first;
        while (current != null && current.next != null) {
            Point2D start = current.getCenter();
            Point2D end = current.next.getCenter();

            gc.strokeLine(
                    start.x * widthScale,
                    start.y * heightScale,
                    end.x * widthScale,
                    end.y * heightScale
            );
            current = current.next;
        }

        // Dibujar los puntos de fijación
        current = first;
        int count = 0;
        while (current != null) {
            Point2D center = current.getCenter();
            gc.setFill(colors[dataColorIndices[count]]);
            int size = sizes[count];

            gc.fillOval(
                    center.x * widthScale - size / 2,
                    center.y * heightScale - size / 2,
                    size * widthScale,
                    size * heightScale
            );

            // Dibujar el número de la fijación
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(current.number),
                    center.x * widthScale,
                    center.y * heightScale
            );

            count++;
            current = current.next;
        }
    }

    private static class Fixation {

        private Point2D sum;
        private int pointsCount;
        private long ellapsedTime;
        private long firstTime;
        private long lastTime;
        private boolean timeSet = false;
        private Fixation prev;
        private Fixation next;
        private int number;

        public void addPoint(GazeData data) {
            Point2D point = data.hasSmoothedGazeCoordinates()
                    ? data.smoothedCoordinates
                    : (data.hasRawGazeCoordinates() ? data.rawCoordinates : new Point2D(0, 0));

            if (sum == null) {
                sum = point;
            } else {
                sum = sum.add(point);
            }

            pointsCount++;
        }

        public void addTime(long time) {
            if (!timeSet) {
                firstTime = time;
                timeSet = true;
            }
            lastTime = time;
            ellapsedTime = lastTime - firstTime;
        }

        public Point2D getCenter() {
            return sum.divide(pointsCount);
        }
    }
}
