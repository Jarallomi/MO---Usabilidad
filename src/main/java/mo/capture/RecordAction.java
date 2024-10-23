/*
    Se suscribe RecordAction a ServerConnection
    Se implementa el metodo OnDataReceived
    Se sacan los botones y menúItems fuera de los métodos createAndShowTray y createAndShowControls para manupularlos remotamente
    Se agregan variables que permiten el manejo remoto de la grabación
    Se agregan a una lista en ServerConnection las configuraciones activas en la grabación
*/

package mo.capture;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import mo.communication.Command;
import mo.communication.ServerConnection;
import mo.communication.ConnectionListener;
import mo.communication.PetitionResponse;
import mo.communication.streaming.capture.PluginCaptureSender;
import mo.core.ui.GridBConstraints;
import mo.core.ui.Utils;
import mo.core.ui.dockables.DockablesRegistry;
import mo.organization.*;

public class RecordAction implements StageAction, ConnectionListener {

    private final static String ACTION_NAME = "Record";
    private ProjectOrganization org;
    private Participant participant;
    private List<RecordableConfiguration> configurations;
    private RecordDialog dialog;
    File storageFolder;
    boolean isPaused = false;
    static boolean isTrayEnable = false, isRecording = false;

    private static final Image recImage
            = Utils.createImageIcon("images/rec.png", RecordAction.class).getImage();

    private static final Image pausedImage
            = Utils.createImageIcon("images/rec-paused.png", RecordAction.class).getImage();
    
    private static final Logger logger = Logger.getLogger(RecordAction.class.getName());

    public RecordAction() {
        configurations = new ArrayList<>();
        ServerConnection.getInstance().addListener(this);
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public void init(
            ProjectOrganization organization,
            Participant participant,
            StageModule stage) {

        this.org = organization;
        this.participant = participant;

        ArrayList<Configuration> configs = new ArrayList<>();
        for (StagePlugin plugin : stage.getPlugins()) {
            for (Configuration configuration : plugin.getConfigurations()) {
                configs.add(configuration);
            }
        }
        System.out.println("??????????: " + stage.getCodeName().toLowerCase());
        storageFolder = new File(org.getLocation(),
                "participant-" + participant.id + "/"
                + stage.getCodeName().toLowerCase());
        storageFolder.mkdirs();
        dialog = new RecordDialog(configs);
        configurations = dialog.showDialog();
        if (configurations != null) {
            startRecording();
        }
    }

    private void startRecording() {
        try {
            System.out.println("?????????: " + storageFolder.getAbsolutePath());
            for (RecordableConfiguration config : configurations) {
                config.setupRecording(storageFolder, org, participant);
            }
            isRecording = true;
            JFrame frame = DockablesRegistry.getInstance().getMainFrame();
            if (SystemTray.isSupported()) {
                try {
                    createAndShowTray();
                } catch (AWTException ex) {
                    createAndShowControls();
                }
            } else {
                createAndShowControls();
            }
            frame.setVisible(false);
            
            ServerConnection.getInstance().setParticipantInfo(org, participant, storageFolder);
            
            for (RecordableConfiguration config : configurations) {
                if (config instanceof PluginCaptureSender)
                    ServerConnection.getInstance().addActiveCapturePlugin((PluginCaptureSender)config);
                System.out.println(config.getId());
                config.startRecording();
            }
            ServerConnection.getInstance().sendInitialConfigs(null);
            
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private static MenuItem pauseResume, stop, cancel;
    
    private void createAndShowTray() throws AWTException {
        
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(recImage);
        final SystemTray tray = SystemTray.getSystemTray();

        pauseResume = new MenuItem("Pause Recording");
        stop = new MenuItem("Stop Recording");
        cancel = new MenuItem("Cancel Recording");

        popup.add(pauseResume);
        popup.add(stop);
        popup.add(cancel);

        trayIcon.setPopupMenu(popup);

        pauseResume.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPaused) {
                    resumeRecording();
                    trayIcon.setImage(recImage);
                    pauseResume.setLabel("Pause Recording");
                } else {
                    pauseRecording();
                    trayIcon.setImage(pausedImage);
                    pauseResume.setLabel("Resume Recording");
                }
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelRecording();
                tray.remove(trayIcon);
                pauseResume =null; stop=null; cancel = null;
            }
        });

        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopRecording();
                tray.remove(trayIcon);
                pauseResume =null; stop=null; cancel = null;
            }
        });
        isTrayEnable = true;
        tray.add(trayIcon);
    }
    
    
    // esto es para sistemas operativos que no soportan icono en background en la barra de notificacaciones
    private static JButton stopButton, pauseButton, cancelButton;
    private void createAndShowControls() {
        JDialog controlsDialog
                = new JDialog((JFrame) null, "Recording Controls");

        controlsDialog.setIconImage(recImage);
        controlsDialog.setLayout(new GridBagLayout());

        GridBConstraints gbc = new GridBConstraints();
        gbc.f(GridBConstraints.HORIZONTAL);
        gbc.i(new Insets(5, 5, 5, 5));

        stopButton = new JButton("Stop");
        pauseButton = new JButton("||  Pause");
        cancelButton = new JButton("Cancel");

        controlsDialog.add(pauseButton, gbc);
        controlsDialog.add(stopButton, gbc.gx(1));
        controlsDialog.add(cancelButton, gbc.gx(2));

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPaused) {
                    resumeRecording();
                    pauseButton.setText("|| Pause");
                    controlsDialog.setIconImage(recImage);
                } else {
                    pauseRecording();
                    pauseButton.setText("> Resume");
                    controlsDialog.setIconImage(pausedImage);
                }
            }
        });
        
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopRecording();
                controlsDialog.dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelRecording();
                controlsDialog.dispose();
            }
        });

        controlsDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancelRecording();
                controlsDialog.dispose();
            }
        });
        controlsDialog.pack();
        controlsDialog.setVisible(true);
        isTrayEnable = false;
    }
    

    private void cancelRecording() {
        for (RecordableConfiguration configuration : configurations) {
            configuration.cancelRecording();
        }
        JFrame frame = DockablesRegistry.getInstance().getMainFrame();
        frame.setVisible(false);
        isRecording = false;
        HashMap<String,Object> map = new HashMap<>();
        map.put("recording_state_r", Command.CANCEL_RECORDING);
        map.put("isPaused", isPaused);
//        ServerConnection.getInstance().getClients().get(0).send(new Response(Command.RECORDING_STATE_R,map));
    }

    private void stopRecording() {
        for (RecordableConfiguration configuration : configurations) {
            configuration.stopRecording();
        }
        JFrame frame = DockablesRegistry.getInstance().getMainFrame();
        frame.setVisible(false);
        isRecording = false;
        HashMap<String,Object> map = new HashMap<>();
        map.put("recording_state_r", Command.STOP_RECORDING);
        map.put("isPaused", isPaused);
//        ServerConnection.getInstance().getClients().get(0).send(new Response(Command.RECORDING_STATE_R,map));
    }

    private void pauseRecording() {
        for (RecordableConfiguration configuration : configurations) {
            configuration.pauseRecording();
        }
        isPaused = true;
        HashMap<String,Object> map = new HashMap<>();
        map.put("recording_state_r", Command.PAUSE_RESUME_RECORDING);
        map.put("isPaused", isPaused);
//        ServerConnection.getInstance().getClients().get(0).send(new Response(Command.RECORDING_STATE_R,map));
    }

    private void resumeRecording(){
        for (RecordableConfiguration configuration : configurations) {
            configuration.resumeRecording();
        }
        isPaused = false;
        HashMap<String,Object> map = new HashMap<>();
        map.put("recording_state_r", Command.PAUSE_RESUME_RECORDING);
        map.put("isPaused", isPaused);
//        ServerConnection.getInstance().getClients().get(0).send(new Response(Command.RECORDING_STATE_R,map));
    }

    @Override
    public void onMessageReceived(Object obj, PetitionResponse petition) {
        //System.out.println("Record action recibe 1: "+connection.mensajeRecibido+"\n");
        if(petition != null && isRecording){
            //System.out.println("Record action recibe 2: "+connection.mensajeRecibido+"\n");
            try{
                switch (petition.getType()) {
                    case Command.STOP_RECORDING:
                        if(isTrayEnable)
                            stop.getActionListeners()[0].actionPerformed(
                                        new ActionEvent(stop,ActionEvent.ACTION_PERFORMED,stop.getActionCommand()));
                        else stopButton.doClick();
                        break;
                        
                    case Command.PAUSE_RESUME_RECORDING:
                        if(isPaused){
                            if(isTrayEnable)
                            pauseResume.getActionListeners()[0].actionPerformed(
                                    new ActionEvent(pauseResume,ActionEvent.ACTION_PERFORMED,pauseResume.getActionCommand()));
                            else pauseButton.doClick();
                        }
                        else{
                            if(isTrayEnable)
                                pauseResume.getActionListeners()[0].actionPerformed(
                                        new ActionEvent(pauseResume,ActionEvent.ACTION_PERFORMED,pauseResume.getActionCommand()));
                            else pauseButton.doClick();
                        }
                        break;
                        
                    case Command.CANCEL_RECORDING:
                        if(isTrayEnable)
                            cancel.getActionListeners()[0].actionPerformed(
                                    new ActionEvent(cancel,ActionEvent.ACTION_PERFORMED,cancel.getActionCommand()));
                        else cancelButton.doClick();
                        break;
                    default:
                        break;
                }
            }catch(NullPointerException e){
                System.out.println("Ha ocurrido un error");
            }
        }
        
    }
}
