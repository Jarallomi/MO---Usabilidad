package mo.analysis;

import java.io.File;
import java.util.List;
import mo.organization.Configuration;

public interface NotPlayableAnalyzableConfiguration extends AnalyzableConfiguration {
	List<String> getCompatibleCreators();
    void addFile(File file);
    void removeFile(File file);
}