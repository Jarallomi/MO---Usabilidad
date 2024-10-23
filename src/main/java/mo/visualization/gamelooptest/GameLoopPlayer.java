package mo.visualization.gamelooptest;

import java.util.ArrayList;
import java.util.List;

public class GameLoopPlayer implements Runnable {

    List<Playable2> configs;
    long start, end, current;
    volatile boolean isPaused = true;

    public GameLoopPlayer(List<Playable2> configurations) {
        this.configs = configurations;
        setTimes();
        
    }

    private void setTimes() {
        long min = Long.MAX_VALUE, max = Long.MIN_VALUE;
        for (Playable2 config : configs) {
            if (config.getStart() < min) {
                min = config.getStart();
            }
            if (config.getEnd() > max) {
                max = config.getEnd();
            }
        }
        if (min == Long.MAX_VALUE) {
            min = 0;
        }
        if (max == Long.MIN_VALUE) {
            max = 100000;
        }

        current = start = min;

        end = max;
    }

    @Override
    public void run() {
        long counter = 0;
        while (true) {
            if (!isPaused) {
                long updateDuration = System.nanoTime();
                updateAll(current);
                updateDuration = System.nanoTime() - updateDuration;
                
                current++;
                
                long renderDuration = System.nanoTime();
                renderAll();
                renderDuration = System.nanoTime() - renderDuration;
                
                counter++;
                if (counter == 1000) {
                    System.exit(0);
                    
                }
            }
        }
    }

    private void updateAll(long current) {
        configs.stream().forEach(config -> config.update(current));
    }

    private void renderAll() {
        configs.stream().forEach(config -> config.render());
    }
    
    public void pause() {
        isPaused = false;
    }
}
