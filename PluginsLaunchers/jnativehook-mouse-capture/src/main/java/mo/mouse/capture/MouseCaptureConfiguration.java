package mo.mouse.capture;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mo.capture.RecordableConfiguration;
import mo.organization.Configuration;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;

public class MouseCaptureConfiguration implements RecordableConfiguration {
    
    String name;
    MouseRecorder recorder;

    public static final Logger logger = Logger.getLogger(MouseCaptureConfiguration.class.getName());
    
    public MouseCaptureConfiguration(String name) {
        this.name = name;
    }

    @Override
    public File toFile(File parent) {
        try {
            File f = new File(parent, "mouse_"+name+".xml");
            //XElement root = new XElement("root");
            f.createNewFile();
            //XIO.writeUTF(root, new FileOutputStream(f));
            return f;
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Configuration fromFile(File file) {
        String fileName = file.getName();
        if (fileName.contains("_") && fileName.contains(".")) {
            String name = fileName.substring(fileName.indexOf("_")+1, fileName.lastIndexOf("."));
            MouseCaptureConfiguration config = new MouseCaptureConfiguration(name);
            return config;
        }
        return null;
    }

    @Override
    public String getId() {
        return name;
    }

    @Override
    public String toString() {
        return "["+name+"]";
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
    public void setupRecording(File stageFolder, ProjectOrganization org, Participant p) {
        recorder = new MouseRecorder(stageFolder, org, p, this);
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
