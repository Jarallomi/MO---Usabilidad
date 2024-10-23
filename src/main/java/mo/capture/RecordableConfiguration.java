package mo.capture;

import java.io.File;
import mo.organization.Configuration;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;

/**
 * 
 * Defines a recordable configuration
 */
public interface RecordableConfiguration extends Configuration {
    
    /**
     *
     * @param stageFolder 
     * @param org
     * @param p
     */
    void setupRecording(File stageFolder, ProjectOrganization org, Participant p);

    /**
     *
     */
    void startRecording();

    /**
     *
     */
    void cancelRecording();

    /**
     *
     */
    void pauseRecording();

    /**
     *
     */
    void resumeRecording();

    /**
     *
     */
    void stopRecording();
}
