package mo.visualization;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mo.core.ui.dockables.DockableElement;

public class VisualizationPlayer {

    private long start;
    private long end;
    private long current;
    //------Variables Christian----
    private long inicio;
    private long temp_act;
    //----------------------------
    private boolean isPlaying = false;
    private boolean stopped = false;
    private boolean endReached = false;

    private Thread playerThread;

    private final List<VisualizableConfiguration> configs;

    private final PlayerControlsPanel panel;
    private final DockableElement dockable;

    private static final Logger logger = Logger.getLogger(VisualizationPlayer.class.getName());

    //private byte STOPPED=0, PLAYING=1, PAUSED=2;

    public VisualizationPlayer(List<VisualizableConfiguration> configurations) {
        configs = configurations;
        for (VisualizableConfiguration config : configs) {
            System.out.println("ESTO ES  "+config.getId());
        }
        obtainMinAndMaxTime();

        panel = new PlayerControlsPanel(this);

        dockable = new DockableElement();
        dockable.add(panel.getPanel());
        dockable.setTitleText("Player Controls");
    }

    private void obtainMinAndMaxTime() {
        long min = Long.MAX_VALUE, max = Long.MIN_VALUE;
        for (VisualizableConfiguration config : configs) {
            if (config.getPlayer().getStart() < min) {
                min = config.getPlayer().getStart();
            }
            if (config.getPlayer().getEnd() > max) {
                max = config.getPlayer().getEnd();
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

    public void seek(long millis) {

        if (millis < start) {
            millis = start;
        } else if (millis > end) {
            millis = end;
        }

        current = millis;

        for (VisualizableConfiguration config : configs) {
            config.getPlayer().seek(millis);
        }
    }

    public void pause() {
        //pauseAll();

        playerThread.interrupt();
        isPlaying = false;
        panel.stop();
        //playButton.setText(">");
        for (VisualizableConfiguration config : configs) {
            config.getPlayer().pause();
        }
    }

    public void sync(boolean sync){
        for (VisualizableConfiguration config : configs) {
            config.getPlayer().sync(sync);
        }
    }

    public void play() {

        playerThread = new Thread(() -> {
            isPlaying = true;
            //----------Christian------------
            //Esto repara el problema de desface de la seekSlider
            if(!panel.getSync()){
                inicio=System.currentTimeMillis()-current;
            }
             //-------------------------------
            while (true/*!Thread.interrupted()*/) {
                if (!isPlaying) {
                    return;
                }

                //----------Christian------------
                if(!panel.getSync()){
                    current=System.currentTimeMillis()-inicio;
                }
                 //-------------------------------
                if (current > end) {
                    if (isPlaying && !stopped) {
                        isPlaying = false;
                        stopped = true;
                        for (VisualizableConfiguration config : configs) {
                            config.getPlayer().stop();
                        }
                        panel.stop();
                        //----------Christian------------
                        if(!panel.getSync()){
                            current = start;
                        }
                        //-------------------------------
                        return;
                    } else {
                        current = start;
                        stopped = false;
                        isPlaying = true;
                    }
                }

                long loopStart = System.nanoTime();
                for (VisualizableConfiguration config : configs) {
                    config.getPlayer().play(current);
                }

                panel.setTime(current);

                sleep(loopStart);
                if(panel.getSync()){
                   current++;
                }
            }
        });
        playerThread.start();
    }

    private void sleep(long loopStart) {
        long loopEnd = System.nanoTime();
        long loopTime = loopEnd - loopStart;
        long timeToWait = 1000000 - loopTime;
        if (timeToWait > 0) {
            try {
                Thread.sleep(0, (int) timeToWait);
            } catch (InterruptedException ex) {
                //logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public DockableElement getDockable() {
        return dockable;
    }

    public long getCurrentTime() {
        return current;
    }
}
