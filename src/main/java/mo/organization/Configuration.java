package mo.organization;

import java.io.File;

public interface Configuration {
    String getId();
    File toFile(File parent);
    Configuration fromFile(File file);
}
