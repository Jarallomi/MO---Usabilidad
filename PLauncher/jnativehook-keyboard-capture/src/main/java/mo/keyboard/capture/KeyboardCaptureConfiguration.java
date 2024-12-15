package mo.keyboard.capture;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mo.capture.RecordableConfiguration;
import mo.organization.Configuration;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;

public class KeyboardCaptureConfiguration implements RecordableConfiguration {
    
    private String id;
    private KeyboardRecorder recorder;
    public static final Logger logger = Logger.getLogger(KeyboardCaptureConfiguration.class.getName());

    public KeyboardCaptureConfiguration(String id) {
        this.id = id;
    }

    @Override
    public void setupRecording(File stageFolder, ProjectOrganization org, Participant p) {
        recorder = new KeyboardRecorder(stageFolder, org, p, this);
    }

    @Override
    public void startRecording() {
        recorder.start();
    }

    @Override
    public void stopRecording() {
        recorder.stop();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public File toFile(File parent) {
        try {
            File f = new File(parent, "keyboard_"+id+".xml");
            f.createNewFile();
            return f;
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Configuration fromFile(File file) {
        String fileName = file.getName();
        if (fileName.contains("_") && fileName.contains(".")){
            String newId = fileName.substring(
                    fileName.indexOf('_') + 1, fileName.lastIndexOf("."));
            KeyboardCaptureConfiguration c = new KeyboardCaptureConfiguration(newId);
            return c;
        }
        return null;
    }

    @Override
    public void cancelRecording() {
        recorder.cancel();
    }

    @Override
    public void pauseRecording() {
        recorder.pause();
    }

    @Override
    public void resumeRecording() {
        recorder.resume();
    }
    
}
