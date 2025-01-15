package mo.keyboard.visualization;

import javafx.application.Platform;
import javafx.scene.Node;
import mo.visualization.Playable;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.Stage;

public class KeyboardPlayer implements Playable {

    private long start;
    private long end;
    private boolean stopped = false;
    private KeyboardEvent current;
    private KeyboardEvent nextEvent;
    private RandomAccessFile file;
    private DisplayPanel pane;
    private AtomicBoolean isPlaying = new AtomicBoolean(false);

    private static final Logger logger = Logger.getLogger(KeyboardPlayer.class.getName());

    public KeyboardPlayer(File f) {
        try {
            file = new RandomAccessFile(f, "r");
            readLastTime(f);
            current = readNextEventFromFile();
            if (current != null) {
                start = current.time;
                nextEvent = readNextEventFromFile();
            }
            pane = new DisplayPanel();
            
            /*Platform.runLater(() -> {
                Stage stage = new Stage();
                stage.setTitle("Keyboard Visualization");
                stage.setScene(pane.getScene());
                stage.show();

                if (current != null) {
                    pane.display(current);
                }
            });*/

            if (current != null) {
                pane.display(current);
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public Node getPaneNode() {
        return pane.getPanel();
    }

    private void readLastTime(File f) {
        try (ReversedLinesFileReader rev = new ReversedLinesFileReader(f, Charset.defaultCharset())) {
            String lastLine;
            do {
                lastLine = rev.readLine();
                if (lastLine == null || lastLine.trim().isEmpty()) {
                    return; 
                }
            } while (lastLine.trim().isEmpty());

            KeyboardEvent e = parseEventFromLine(lastLine);
            if (e != null) {
                end = e.time;
            } else {
                logger.log(Level.SEVERE, "Unable to parse last line: " + lastLine);
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error reading last line from file", ex);
        }
    }


    private KeyboardEvent readNextEventFromFile() {
        try {
            String line = file.readLine();
            if (line != null) {
                return parseEventFromLine(line);
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private KeyboardEvent parseEventFromLine(String line) {
        try {
            int firstComma = line.indexOf(',');
            int secondComma = line.indexOf(',', firstComma + 1);
            int thirdComma = line.indexOf(',', secondComma + 1);
            int fourthComma = line.indexOf(',', thirdComma + 1);
            int fifthComma = line.indexOf(',', fourthComma + 1);

            long time = Long.parseLong(line.substring(0, firstComma));
            String eventTypeStr = line.substring(firstComma + 1, secondComma);
            String keyCodeStr = line.substring(secondComma + 1, thirdComma);
            String keyTextStr = line.substring(thirdComma + 1, fourthComma);

            int keyCharIndex = line.indexOf("keyChar=", fourthComma + 1);
            char keyChar = line.charAt(keyCharIndex + 9);

            int keyLocationIndex = line.indexOf("keyLocation=", fourthComma + 1);
            String keyLocationStr = line.substring(keyLocationIndex + 12, line.indexOf(',', keyLocationIndex));

            int rawCodeIndex = line.indexOf("rawCode=", keyLocationIndex);
            String rawCodeStr = line.substring(rawCodeIndex + 8);

            KeyboardEvent ev = new KeyboardEvent();
            ev.time = time;
            ev.type = KeyboardEventType.getEventTypeFromString(eventTypeStr);
            ev.keyCode = Integer.parseInt(keyCodeStr.split("=")[1]);
            ev.keyText = keyTextStr.split("=")[1];
            ev.keyChar = keyChar;
            ev.keyLocation = KeyLocation.getKeyLocationFromString(keyLocationStr);
            ev.rawCode = Integer.parseInt(rawCodeStr);

            return ev;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al analizar la linea: " + line, e);
            return null;
        }
    }

    @Override
    public void pause() {
        isPlaying.set(false);
    }

    @Override
    public void seek(long desiredMillis) {
        if (desiredMillis < start || desiredMillis > end || desiredMillis == current.time
                || (desiredMillis > current.time && desiredMillis < nextEvent.time)) {
            return;
        }

        KeyboardEvent event = current;

        if (desiredMillis < current.time) {
            try {
                file.seek(0);
                event = readNextEventFromFile();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }

        long marker;
        try {
            marker = file.getFilePointer();

            KeyboardEvent next = readNextEventFromFile();
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
            current = event;
            nextEvent = next;

        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
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

    @Override
    public void play(long millis) {
        if (isPlaying.get()) {
            return;
        }

        isPlaying.set(true);
        stopped = false;

        new Thread(() -> {
            try {
                while (!stopped && current != null && current.time <= end) {
                    if (current.type == KeyboardEventType.NATIVE_KEY_TYPED) {
                        Platform.runLater(() -> pane.display(current));
                    }

                    if (nextEvent == null) {
                        break;
                    }

                    long delay = Math.max(0, nextEvent.time - current.time);
                    Thread.sleep(delay);

                    current = nextEvent;
                    nextEvent = readNextEventFromFile();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                isPlaying.set(false);
            }
        }).start();
    }


    @Override
    public void stop() {
        isPlaying.set(false);
        stopped = true;
        pane.clear();
        try {
            file.seek(0);
            current = readNextEventFromFile();
            nextEvent = readNextEventFromFile();
        } catch (IOException e) {
            logger.log(Level.SEVERE, null, e);
        }
    }
    
    public void sync(boolean isSynchronized) {
        System.out.println("Sync status: " + isSynchronized);
    }
}
