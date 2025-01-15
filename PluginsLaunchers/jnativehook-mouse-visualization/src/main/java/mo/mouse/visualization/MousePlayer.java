package mo.mouse.visualization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import static mo.organization.ProjectOrganization.logger;
import mo.visualization.Playable;
import org.apache.commons.io.input.ReversedLinesFileReader;

public class MousePlayer implements Playable {

    private long start, end, currentTime;
    private RandomAccessFile file;
    private PlayerPane pane;
    private MouseEvent currentEvent, nextEvent;

    public MousePlayer(File file) {
        this(file, getDefaultScreens());
    }

    public MousePlayer(File file, List<Rectangle2D> screens) {
        try {
            this.file = new RandomAccessFile(file, "r");

            String line = this.file.readLine();
            List<Rectangle2D> bounds = screens != null ? screens : parseScreens(line);

            String lastLine;
            try (ReversedLinesFileReader rev = new ReversedLinesFileReader(file, Charset.defaultCharset())) {
                lastLine = null;
                do {
                    lastLine = rev.readLine();
                    if (lastLine == null) {
                        break;
                    }
                } while (lastLine.trim().isEmpty());
            }

            MouseEvent lastEvent = parseEventFromLine(lastLine);
            if (lastEvent != null) {
                end = lastEvent.time;
            }

            currentEvent = readNextEventFromFile();
            if (currentEvent != null) {
                start = currentEvent.time;
                nextEvent = readNextEventFromFile();
            }

            pane = new PlayerPane(bounds);
            pane.setPrefSize(800, 600);
            pane.setMinSize(200, 200);

            /*Platform.runLater(() -> {
                Stage stage = new Stage();
                stage.setTitle("Mouse Visualization");
                Scene scene = new Scene(pane, 800, 600);
                stage.setScene(scene);
                stage.show();

                if (currentEvent != null) {
                    pane.display(currentEvent);
                }
            });*/

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Node getPaneNode() {
        return pane;
    }

    private static List<Rectangle2D> getDefaultScreens() {
        List<Rectangle2D> screens = new ArrayList<>();
        for (Screen screen : Screen.getScreens()) {
            javafx.geometry.Rectangle2D bounds = screen.getBounds();
            screens.add(new Rectangle2D(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()));
        }
        return screens;
    }

    private MouseEvent readNextEventFromFile() {
        try {
            String line = file.readLine();
            if (line != null) {
                return parseEventFromLine(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void pause() {
        isPlaying.set(false);
    }

    public long getCurrentTime() {
        return currentTime;
    }

    @Override
    public void seek(long desiredMillis) {
        if (desiredMillis < start) {
            seek(start);
            return;
        }

        if (desiredMillis > end) {
            seek(end);
            return;
        }

        currentTime = desiredMillis;

        if (desiredMillis == currentEvent.time) {
            pane.display(currentEvent);
            return;
        }

        if (desiredMillis > currentEvent.time && desiredMillis < nextEvent.time) {
            return;
        }

        MouseEvent event = currentEvent;

        if (desiredMillis < currentEvent.time) {
            try {
                file.seek(0);
                file.readLine();
                event = readNextEventFromFile();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        long marker;
        try {
            marker = file.getFilePointer();
            MouseEvent next = readNextEventFromFile();
            if (next == null) {
                return;
            }

            while (!(next.time > desiredMillis)) {
                event = next;
                marker = file.getFilePointer();
                next = readNextEventFromFile();

                if (next == null) {
                    return;
                }
            }

            file.seek(marker);
            currentEvent = event;
            nextEvent = next;
            pane.display(currentEvent);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private AtomicBoolean isPlaying = new AtomicBoolean(false);
    private AtomicBoolean isStopped = new AtomicBoolean(false);

    @Override
    public void play(long millis) {
        if (isPlaying.get()) {
            return;
        }

        isPlaying.set(true);
        isStopped.set(false);

        new Thread(() -> {
            try {
                while (!isStopped.get() && currentEvent != null && currentTime <= end) {
                    if (currentEvent == null) {
                        break;
                    }

                    Platform.runLater(() -> pane.display(currentEvent));

                    currentEvent = nextEvent;
                    nextEvent = readNextEventFromFile();

                    if (nextEvent == null) {
                        break;
                    }

                    long delay = Math.max(0, nextEvent.time - currentEvent.time);
                    Thread.sleep(delay);

                    currentTime = nextEvent.time;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                isPlaying.set(false);
            }
        }).start();

    }
    
    /*@Override
    public void play(long millis) {
        if (millis < start) {
            play(start);
        } else if (millis > end) {
            play(end);
        } else {
            seek(millis);
        }
    }*/

    @Override
    public void stop() {
        isStopped.set(true);
        isPlaying.set(false);
        currentEvent = null;
        try {
            file.seek(0);
            file.readLine();
            currentEvent = readNextEventFromFile();
            nextEvent = readNextEventFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MouseEvent parseEventFromLine(String line) {
        if (line != null && line.contains(",")) {
            MouseEvent e = new MouseEvent();
            int index = line.indexOf(",");
            String when = line.substring(0, index);
            long time = Long.parseLong(when);
            index++;
            String eventType = line.substring(index, line.indexOf(",", index++));
            switch (eventType) {
                case ("NATIVE_MOUSE_CLICKED"):
                    e.type = MouseEventType.NATIVE_MOUSE_CLICKED;
                    break;

                case ("NATIVE_MOUSE_PRESSED"):
                    e.type = MouseEventType.NATIVE_MOUSE_PRESSED;
                    break;

                case ("NATIVE_MOUSE_RELEASED"):
                    e.type = MouseEventType.NATIVE_MOUSE_RELEASED;
                    break;

                case ("NATIVE_MOUSE_MOVED"):
                    e.type = MouseEventType.NATIVE_MOUSE_MOVED;
                    break;

                case ("NATIVE_MOUSE_DRAGGED"):
                    e.type = MouseEventType.NATIVE_MOUSE_DRAGGED;
                    break;

                case ("NATIVE_MOUSE_WHEEL"):
                    e.type = MouseEventType.NATIVE_MOUSE_WHEEL;
                    break;

                default:
                    e.type = null;
                    break;
            }
            index += eventType.length() + 1;
            String xStr = line.substring(index, line.indexOf(",", index++));
            index += xStr.length();

            String yStr = line.substring(index, line.indexOf("),", index++));

            index += yStr.length() + 1;

            String button = line.substring(line.indexOf("=", index) + 1, line.indexOf(",", index));
            e.x = Integer.parseInt(xStr);
            e.y = Integer.parseInt(yStr);
            e.time = time;
            e.button = Integer.parseInt(button);
            return e;
        }
        logger.info("returning null");
        return null;
    }

    @Override
    public long getStart() {
        return start;
    }

    @Override
    public long getEnd() {
        return end;
    }

    public void sync(boolean isSynchronized) {
    }

    private static List<Rectangle2D> parseScreens(String line) {
        ArrayList<Rectangle2D> screens = new ArrayList<>();
        if (line.contains(";")) {
            String[] screensStrs = line.split(";");
            for (String screenStr : screensStrs) {
                Rectangle2D r = parseRectangle(screenStr);
                if (r != null) {
                    screens.add(r);
                }
            }
        } else {
            Rectangle2D r = parseRectangle(line);
            if (r != null) {
                screens.add(r);
            }
        }
        return screens;
    }

    private static Rectangle2D parseRectangle(String str) {
        int x, y, w, h, i;
        if (str.contains("x=")) {
            i = str.indexOf(",");
            x = Integer.parseInt(str.substring(str.indexOf("x=") + 2, i));
        } else {
            return null;
        }
        if (str.contains("y=")) {
            i = str.indexOf(",", i + 1);
            y = Integer.parseInt(str.substring(str.indexOf("y=") + 2, i));
        } else {
            return null;
        }
        if (str.contains("width=")) {
            i = str.indexOf(",", i + 1);
            w = Integer.parseInt(str.substring(str.indexOf("width=") + 6, i));
        } else {
            return null;
        }
        if (str.contains("height=")) {
            h = Integer.parseInt(str.substring(str.indexOf("height=") + 7));
        } else {
            return null;
        }
        return new Rectangle2D(x, y, w, h);
    }
}