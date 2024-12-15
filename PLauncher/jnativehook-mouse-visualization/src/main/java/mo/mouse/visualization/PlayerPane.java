package mo.mouse.visualization;

import java.awt.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Dimension2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;

public class PlayerPane extends Pane {

    private double scale;
    private final List<Rectangle2D> screenBounds;
    private Point virtualPoint;
    private Point screenPoint;
    private Rectangle2D virtualBounds;
    private Canvas canvas;

    public PlayerPane(List<Rectangle2D> screens) {
        screenBounds = screens;
        virtualBounds = new Rectangle2D(0, 0, 0, 0);
        screens.forEach(screen -> virtualBounds = union(virtualBounds, screen));

        canvas = new Canvas();
        getChildren().add(canvas);

        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        widthProperty().addListener((obs, oldVal, newVal) -> refreshScale());
        heightProperty().addListener((obs, oldVal, newVal) -> refreshScale());
    }

    private void refreshScale() {
        double containerWidth = getWidth();
        double containerHeight = getHeight();

        if (containerWidth <= 0 || containerHeight <= 0) {
            return;
        }

        scale = getScaleFactorToFit(
                new Dimension2D(virtualBounds.getWidth(), virtualBounds.getHeight()),
                new Dimension2D(containerWidth, containerHeight)
        );

        redraw();
    }

    private void redraw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        int xOffset = 0;
        int yOffset = 0;
        List<Rectangle2D> scaledBounds = new ArrayList<>();

        for (Rectangle2D bounds : screenBounds) {
            Rectangle2D scaled = scale(bounds);
            scaledBounds.add(scaled);
            xOffset = Math.min(xOffset, (int) scaled.getMinX());
            yOffset = Math.min(yOffset, (int) scaled.getMinY());
        }
        xOffset = xOffset < 0 ? -xOffset : xOffset;
        yOffset = yOffset < 0 ? -yOffset : yOffset;

        gc.setFill(Color.DARKGRAY);
        for (Rectangle2D bounds : scaledBounds) {
            gc.fillRect(bounds.getMinX() + xOffset, bounds.getMinY() + yOffset, bounds.getWidth(), bounds.getHeight());
            gc.setStroke(Color.GRAY);
            gc.strokeRect(bounds.getMinX() + xOffset, bounds.getMinY() + yOffset, bounds.getWidth(), bounds.getHeight());
        }

        if (screenPoint != null) {
            gc.setFill(Color.RED);
            gc.setFont(new Font(10));
            gc.fillText(screenPoint.x + "," + screenPoint.y, 0, 10);
            gc.fillOval(screenPoint.x * scale + xOffset - 2, screenPoint.y * scale + yOffset - 2, 4, 4);
        }
    }

    private Rectangle2D scale(Rectangle2D bounds) {
        return new Rectangle2D(bounds.getMinX() * scale, bounds.getMinY() * scale, bounds.getWidth() * scale, bounds.getHeight() * scale);
    }

    public void display(MouseEvent event) {
        if (event == null) {
            return;
        }

        if (event.x < virtualBounds.getMinX() || event.x > virtualBounds.getMaxX()
                || event.y < virtualBounds.getMinY() || event.y > virtualBounds.getMaxY()) {
            return;
        }

        double adjustedX = Math.max(virtualBounds.getMinX(), Math.min(event.x, virtualBounds.getMaxX()));
        double adjustedY = Math.max(virtualBounds.getMinY(), Math.min(event.y, virtualBounds.getMaxY()));

        screenPoint = new Point((int) adjustedX, (int) adjustedY);
        redraw();
    }

    public static double getScaleFactorToFit(Dimension2D original, Dimension2D toFit) {
        double dScaleWidth = getScaleFactor(original.getWidth(), toFit.getWidth());
        double dScaleHeight = getScaleFactor(original.getHeight(), toFit.getHeight());
        return Math.min(dScaleHeight, dScaleWidth);
    }

    public static double getScaleFactor(double iMasterSize, double iTargetSize) {
        return iTargetSize / iMasterSize;
    }

    private Rectangle2D union(Rectangle2D r1, Rectangle2D r2) {
        double minX = Math.min(r1.getMinX(), r2.getMinX());
        double minY = Math.min(r1.getMinY(), r2.getMinY());
        double maxX = Math.max(r1.getMaxX(), r2.getMaxX());
        double maxY = Math.max(r1.getMaxY(), r2.getMaxY());
        return new Rectangle2D(minX, minY, maxX - minX, maxY - minY);
    }
}
