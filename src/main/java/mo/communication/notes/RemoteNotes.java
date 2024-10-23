package mo.communication.notes;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteNotes {
    
    File storageFolder;
    File parentDir; //directorio en donde se guardará el archivo: partenrDir + \Analysis\ +nameFile.txt
    String path;
    String fileName;
    public RemoteNotes(){
        
    }
    
    public void setInfo(File parentDir){
        try{
            if(!parentDir.equals(this.parentDir)){
                this.parentDir = parentDir;
                createFileName();
                File path = new File(this.parentDir.getParent()+"\\Analysis");
                if(!path.exists())
                    path.mkdir();
                this.storageFolder = new File(this.parentDir.getParent()+"\\Analysis\\"+this.fileName);
                this.storageFolder.createNewFile();
            }
        }catch(NullPointerException | IOException ex){}
    }
    
    public void createFileName(){
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS");
        String reportDate = df.format(now);
        this.fileName = reportDate + "_notas.txt";
    }
    
    public void saveNote(HashMap<String,Object> map){
        if(storageFolder != null){
            try {
                long currentTime = System.currentTimeMillis();
                int phase = Integer.parseInt((String)map.get("time")) + 2000;
                String note = String.valueOf(currentTime-phase)
                        +","
                        +String.valueOf(currentTime+2000)
                        +","
                        +(String)map.get("note")
                        +"\n";
                
                Writer output;
                output = new BufferedWriter(new FileWriter(storageFolder, true));
                output.append(note);
                output.close();
            } catch (IOException ex) {
                Logger.getLogger(RemoteNotes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
