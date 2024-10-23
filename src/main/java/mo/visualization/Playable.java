package mo.visualization;

public interface Playable {
    long getStart();
    long getEnd();
    void play(long millis);
    void pause();
    void seek(long millis);
    void stop();
    void sync(boolean sync);
}
