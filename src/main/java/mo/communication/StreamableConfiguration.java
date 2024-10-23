package mo.communication;

import mo.capture.*;
import java.io.File;
import mo.organization.Configuration;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;

public interface StreamableConfiguration extends Configuration {
    /**
     *
     * @param stageFolder 
     * @param org
     * @param p
     */
    void setupStreaming();

    /**
     *
     */
    void startStreaming();

    /**
     *
     */
    void pauseStreaming();

    /**
     *
     */
    void resumeStreaming();

    /**
     *
     */
    void stopStreaming();
}
