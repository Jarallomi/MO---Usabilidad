package mo.mouse.capture;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
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
import org.jnativehook.mouse.*;

public class MouseRecorder
        implements NativeMouseListener, NativeMouseMotionListener,
        NativeMouseWheelListener {

    Participant participant;
    ProjectOrganization org;
    MouseCaptureConfiguration config;
    File output;
    FileOutputStream outputStream;
    BufferedWriter writer;
    BufferedOutputStream buffOutStream;
    FileDescription desc;

    Thread thread;

    private static final Logger globalScreenLogger
            = Logger.getLogger(GlobalScreen.class.getPackage().getName());
    private static final Logger logger = Logger.getLogger(MouseRecorder.class.getName());

    public MouseRecorder(File stageFolder, ProjectOrganization org, Participant p,
            MouseCaptureConfiguration c) {

        participant = p;
        this.org = org;
        this.config = c;

        globalScreenLogger.setUseParentHandlers(false);
        globalScreenLogger.setLevel(Level.OFF);

        GlobalScreen.addNativeMouseListener(this);
        GlobalScreen.addNativeMouseMotionListener(this);
        GlobalScreen.addNativeMouseWheelListener(this);

        createFile(stageFolder);
    }

    private void createFile(File parent) {

        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS");

        String reportDate = df.format(now);

        output = new File(parent, reportDate + "_" + config.getId() + ".txt");
        try {
            output.createNewFile();
            buffOutStream = new BufferedOutputStream(new FileOutputStream(output));
            writeScreens();
            desc = new FileDescription(output, MouseRecorder.class.getName());
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    private void writeScreens() {
        GraphicsDevice devices[] 
                = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        String devs = "";
        for (GraphicsDevice device : devices) {
            Rectangle r = device.getDefaultConfiguration().getBounds();
            devs += "x=" + r.x + ",y=" + r.y
                    + ",width=" + r.width + ",height=" + r.height + ";";
        }
        try {
            buffOutStream.write((devs.substring(0, devs.length()-1)+"\n").getBytes());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private void deleteFile() {
        if (output.isFile()) {
            output.delete();
        }
        if (desc != null && desc.getDescriptionFile().isFile()) {
            desc.deleteFileDescription();
        }
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
            GlobalScreen.removeNativeMouseListener(this);
            GlobalScreen.removeNativeMouseMotionListener(this);
            GlobalScreen.removeNativeMouseWheelListener(this);
        } catch (NativeHookException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void resume() {
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeMouseListener(this);
            GlobalScreen.addNativeMouseMotionListener(this);
            GlobalScreen.addNativeMouseWheelListener(this);
        } catch (NativeHookException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void cancel() {
        stop();
        deleteFile();
    }

    public void stop() {
        try {
            GlobalScreen.unregisterNativeHook();
            GlobalScreen.removeNativeMouseListener(this);
            GlobalScreen.removeNativeMouseMotionListener(this);
            GlobalScreen.removeNativeMouseWheelListener(this);
            buffOutStream.close();
        } catch (NativeHookException | IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private void write(NativeMouseEvent event) {
        try {
            buffOutStream.write((event.getWhen() + "," + event.paramString() + "\n").getBytes());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent e) {
        write(e);
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
        write(e);
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
        write(e);
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent e) {
        write(e);
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent e) {
        write(e);
    }

    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent e) {
        write(e);
    }

}
