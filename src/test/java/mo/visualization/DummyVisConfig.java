package mo.visualization;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import mo.organization.Configuration;

public class DummyVisConfig implements VisualizableConfiguration, Playable {

    private List<String> compatibleCreators;
    private List<File> files;
    private long start = 0;
    private long end = 1000;
    private long current = 0;
    
    public DummyVisConfig() {
        compatibleCreators = new ArrayList<>();
        compatibleCreators.add("mo.mouse.capture.MouseRecorder");
        files = new ArrayList<>();
    }

    @Override
    public List<String> getCompatibleCreators() {
        return compatibleCreators;
    }

    @Override
    public void addFile(File file) {
        files.add(file);
    }

    @Override
    public void removeFile(File file) {
        files.remove(file);
    }

    @Override
    public String getId() {
        return DummyVisConfig.class.getName();
    }

    @Override
    public File toFile(File parent) {
        return null;
    }

    @Override
    public Configuration fromFile(File file) {
        DummyVisConfig c = new DummyVisConfig();
        return c;
    }

    @Override
    public void pause() {
    }

    @Override
    public void seek(long millis) {
        setCurrent(millis);
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
        setCurrent(millis);
    }

    public long getCurrentTime() {
        return current;
    }
    
    private void setCurrent(long millis) {
        if (millis < start) {
            current = start;
        } else if (millis > end) {
            current = end;
        } else {
            current = millis;
        }
    }

    @Override
    public void stop() {
        current = start;
    }

    @Override
    public Playable getPlayer() {
        return this;
    }
    @Override
    public void sync(boolean sync) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
