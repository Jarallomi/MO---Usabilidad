package mo.core;

import java.io.File;

public interface WatchHandler {
    public void onCreate(File file);
    public void onDelete(File file);
    public void onModify(File file);
}
