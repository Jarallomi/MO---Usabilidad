package audiocaptureplugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import mo.organization.FileDescription;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;
import static mo.organization.ProjectOrganization.logger;

public class AudioRecorder {
    AudioFileFormat.Type aFF_T = AudioFileFormat.Type.WAVE;
    TargetDataLine tD;
    Participant participant;
    ProjectOrganization org;
    private AudioCaptureConfiguration config;
    private File output;
    private File arInicio;
    private String path;
    private FileOutputStream outputStream;    
    private FileDescription desc;    
    private Thread t;
    private long inicio,fin;
    private int op_mic;
    private float SampleRate;
    
    AudioRecorder audio;
    
    public AudioRecorder(){
        
    }
        
    public void Recorder() {
        //Enumerates all available microphones
	Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        int i=0;             
	for (Mixer.Info info: mixerInfos){
		Mixer m = AudioSystem.getMixer(info);
		Line.Info[] lineInfos = m.getTargetLineInfo();   
                if(lineInfos.length>=1 && lineInfos[0].getLineClass().equals(TargetDataLine.class)){//Only prints out info is it is a Microphone
		if(i==op_mic){//Only prints out info is it is a Microphone
                    DataLine.Info dLl = (DataLine.Info) lineInfos[0];
                    try {
                        tD=(TargetDataLine)AudioSystem.getLine(dLl);
                    } catch (LineUnavailableException ex) {
                        Logger.getLogger(AudioRecorder.class.getName()).log(Level.SEVERE, null, ex);
                    }                    
                    hilo h = new hilo();
                    t = new Thread(h);
                    
                    t.start(); 	
		}                
                i++;
                }
	}
    }
    class hilo extends Thread{
        public void run(){
            try {
                AudioFormat aF = new AudioFormat(SampleRate, 16, 2, true, false);
                tD.open(aF);
                inicio=System.currentTimeMillis();
                tD.start();                
                AudioSystem.write(new AudioInputStream(tD), aFF_T, output);                
            } catch (Exception e) {
            }
        }
    }
    
     public AudioRecorder(File stageFolder, ProjectOrganization org, Participant p,int op_mic,int SR,AudioCaptureConfiguration c){
        participant = p;
        this.org = org;
        this.config = c;
        this.op_mic=op_mic;
        this.SampleRate = SR;
        createFile(stageFolder);
    }

    private void createFile(File parent) {

        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS");

        String reportDate = df.format(now);

        output = new File(parent, reportDate + "_" + config.getId() + ".wav");
        arInicio = new File(parent, reportDate + "_" + config.getId() + "-temp.txt");
        path = parent.getAbsolutePath();
        try {
            output.createNewFile();
            outputStream = new FileOutputStream(output);
            desc = new FileDescription(output, AudioRecorder.class.getName());
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
    
     public void StartRecord(){
         Recorder();
        }
        
        public void StopRecord(){
            tD.close();
            fin=System.currentTimeMillis();
             BufferedWriter bw;
                try {
                    bw = new BufferedWriter(new FileWriter(arInicio));
                    bw.write(inicio+"\n");
                    bw.write(fin+"");
                    bw.close();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
                try {
                    arInicio.createNewFile();
                } catch (IOException ex) {
                   logger.log(Level.SEVERE, null, ex);
                }
        }
        
        public void PauseRecord(){
            t.suspend();
        }
        
        public void ResumeRecod(){
            t.resume();
        }
        public void CandelRecord(){
           StopRecord();
           deleteFile();
        }
}
