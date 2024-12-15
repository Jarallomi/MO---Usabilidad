/* This KeyboardPlayer class works with the original version of MO */

package mo.keyboard.visualization;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import mo.visualization.Playable;
import org.apache.commons.io.input.ReversedLinesFileReader;

public class KeyboardPlayer2 implements Playable {

    private long start;
    private long end;
    private boolean stopped = false;
    private KeyboardEvent current;
    private KeyboardEvent nextEvent;
    private Timeline timeline;
    DisplayPanel2 pane;
    private RandomAccessFile file;
    private double speedMultiplier = 3.5; 

    private static final Logger logger = Logger.getLogger(KeyboardPlayer2.class.getName());

    public KeyboardPlayer2(File f) {
        try {
            file = new RandomAccessFile(f, "r");
            readLastTime(f);
            current = readNextEventFromFile();
            if (current != null) {
                start = current.time;
                nextEvent = readNextEventFromFile();
            }
            pane = new DisplayPanel2();

            Platform.runLater(() -> {
                Stage stage = new Stage();
                stage.setTitle("Keyboard Visualization");
                stage.setScene(pane.getScene());
                stage.show();

                if (current != null) {
                    pane.display(current);
                }
            });
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private void readLastTime(File f) {
        try (ReversedLinesFileReader rev = new ReversedLinesFileReader(f, Charset.defaultCharset())) {
            String lastLine;
            do {
                lastLine = rev.readLine();
                if (lastLine == null) {
                    return;
                }
            } while (lastLine.trim().isEmpty());
            KeyboardEvent e = parseEventFromLine(lastLine);
            end = e.time;
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
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
        stopped = true;
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

    private void display(KeyboardEvent ev) {
        if (stopped) {
            pane.clear();
            stopped = false;
        }
        pane.display(ev);
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
        logger.info("sync en: " + isSynchronized);
    }

    @Override
    public void play(long millis) {
        if ((millis >= start) && (millis <= end)) {
            seek(millis);
            if (current.time == millis) {
                while (current.time == millis) {
                    display(current);
                    current = nextEvent;
                    nextEvent = readNextEventFromFile();
                }
            }
        }
    }

    public void setSpeedMultiplier(double multiplier) {
        this.speedMultiplier = multiplier;
    }

    @Override
    public void stop() {
        stopped = true;
        pause();
    }
} 