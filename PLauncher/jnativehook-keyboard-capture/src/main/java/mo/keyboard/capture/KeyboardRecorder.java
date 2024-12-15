package mo.keyboard.capture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import mo.organization.FileDescription;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class KeyboardRecorder implements NativeKeyListener {

    Participant participant;
    ProjectOrganization org;
    KeyboardCaptureConfiguration config;
    File output;
    FileOutputStream outputStream;
    FileDescription desc;

    Thread thread;

    private static final Logger globalScreenLogger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    private static final Logger logger = Logger.getLogger(KeyboardRecorder.class.getName());

    public KeyboardRecorder(File stageFolder, ProjectOrganization org, Participant p, 
            KeyboardCaptureConfiguration c) {

        participant = p;
        this.org = org;
        this.config = c;

        globalScreenLogger.setUseParentHandlers(false);
        globalScreenLogger.setLevel(Level.OFF);

        GlobalScreen.addNativeKeyListener(this);

        createFile(stageFolder);
    }

    private void createFile(File parent) {

        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS");

        String reportDate = df.format(now);

        output = new File(parent, reportDate + "_" + config.getId() + ".txt");
        try {
            output.createNewFile();
            outputStream = new FileOutputStream(output);
            desc = new FileDescription(output, KeyboardRecorder.class.getName());
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }
    
    private void deleteFile() {
        if (output.isFile()) {
            output.delete();
        }
        if (desc.getDescriptionFile().isFile()) {
            desc.deleteFileDescription();
        }
    }
    
    public void cancel() {
        stop();
        deleteFile();
    }

    public void start() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void pause() {
        try {
            GlobalScreen.unregisterNativeHook();
            GlobalScreen.removeNativeKeyListener(this);
        } catch (NativeHookException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public void resume() {
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
        } catch (NativeHookException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void stop() {
        try {
            GlobalScreen.unregisterNativeHook();
            GlobalScreen.removeNativeKeyListener(this);
            outputStream.close();
        } catch (NativeHookException | IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private void write(NativeKeyEvent event) {

        // replace new line char to keep one event per line in the text file
        if (event.getID() == NativeKeyEvent.NATIVE_KEY_TYPED && isEnterKey(event)){
            event.setKeyChar(' ');
        }
        
        try {
            outputStream.write((event.getWhen() + "," + event.paramString() + "\n").getBytes());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean isEnterKey(NativeKeyEvent event) {
        return (event.getKeyCode()== NativeKeyEvent.VC_ENTER
                || event.getRawCode() == NativeKeyEvent.VC_ENTER
                || event.getRawCode() == 13);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        write(e);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        write(e);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        write(e);
    }

}
