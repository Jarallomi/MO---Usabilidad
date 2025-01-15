package capturepluginbitalino;

import com.bitalino.comm.BITalinoDevice;
import com.bitalino.comm.BITalinoException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import mo.organization.FileDescription;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;




public class BitalinoRecorder {    
    Participant participant;
    ProjectOrganization org;
    private BitalinoCaptureConfiguration config;
    private File output;
    private String path;
    private String file_name;
    private FileOutputStream outputStream;    
    private FileDescription desc;  
    // validate MAC address
    private final String MAC = "98:D3:31:B2:11:33";
    final String mac = MAC.replace(":", "");
    private int samplerate = 100;
    //canales que lee
    final int[] analogs = {0,1,2};
    long resume = 0;
    long pause;
    //ECG = 0, EMG=1,EDA=2,ECG&EMG=3,ECG&EDA=4,EMG&EDA=5,EMG&ECG&EDA=6
    private int sensor_op;
    
    private static final Logger logger = Logger.getLogger(BitalinoRecorder.class.getName());
    
    public int sw=1;
    
    ResourceBundle dialogBundle = ResourceBundle.getBundle("properties/principal");
    
    public BitalinoRecorder(File stageFolder, ProjectOrganization org, Participant p,int sensor,int samplingRate,BitalinoCaptureConfiguration c){
        participant = p;
        this.org = org;
        this.config = c;
        this.samplerate=samplingRate;
        switch(sensor){            
                case 1:
                    this.sensor_op = 0;
                case 10:
                    this.sensor_op = 1;
                case 11:
                    this.sensor_op = 3;
                case 100:                    
                    this.sensor_op = 2;
                case 101:
                    this.sensor_op = 4;
                case 110:
                    this.sensor_op = 5;
                case 111:
                    this.sensor_op = 6;
        }                    
        createFile(stageFolder);
    }

    private void createFile(File parent) {

        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS");

        String reportDate = df.format(now);

        output = new File(parent, reportDate + "_" + config.getId() + ".txt");
        path = parent.getAbsolutePath();
        file_name = reportDate + "_"+config.getId();
        try {
            output.createNewFile();
            outputStream = new FileOutputStream(output);
            desc = new FileDescription(output, BitalinoRecorder.class.getName()+sensor_op);
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
       private class Record implements Runnable{
            @Override
            public void run() {
            
            BITalinoDevice device;
            try {
                device = new BITalinoDevice(samplerate, analogs);
                // connect to BITalino device
                final StreamConnection conn = (StreamConnection) Connector.open("btspp://"+mac+":1", Connector.READ_WRITE);
                device.open(conn.openInputStream(), conn.openOutputStream());
                
                // start acquisition on predefined analog channels
                device.start();
                
                BufferedWriter bw = new BufferedWriter(new FileWriter(output));
                while(sw!=0){
                    bw.write((System.currentTimeMillis()-resume)+","+
                            device.read(1)[0].getAnalog(1)+","+
                            device.read(1)[0].getAnalog(2)+","+
                            device.read(1)[0].getAnalog(0)+"\n");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(BitalinoRecorder.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    //bw.write((System.currentTimeMillis()-resume)+","+device.read(1)[0].getAnalog(1)+",0,0\n");
                    while(sw==2){                           
                        try {       
                            Thread.sleep(2);
                        } catch (InterruptedException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    }
                }
                bw.close();
                
                // stop acquisition and close bluetooth connection
                device.stop(); 
                
            } catch (BITalinoException | IOException ex) {
                showAlert(dialogBundle.getString("alert"), "Bitalino Error");
                CancelRecord();
                
            }           
        }
       }
        
        public void StartRecord(){            
                Thread t=new Thread(new Record());
                t.start();  
        }
        
        public void StopRecord(){
            sw=0;
        }
        
        public void PauseRecord(){
             sw=2;
            pause = System.currentTimeMillis()-resume;
        }
        
        public void ResumeRecod(){
            sw=1;
            resume=System.currentTimeMillis()-pause;
        }
        public void CancelRecord(){
            StopRecord();
            deleteFile();
        }
        
        //customizable error message
        private void showAlert(String message, String title) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);

            Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            okButton.setStyle("-fx-background-color: #ea908a; -fx-text-fill: black;");

            alert.getDialogPane().setStyle("-fx-background-color: #d6cfcf; -fx-font-size: 14px; -fx-font-family: Arial;");

            alert.showAndWait();
        });
    }


}