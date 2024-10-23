package mo.core.plugin.gui;

import com.github.junrar.extract.ExtractArchive;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.Files.deleteIfExists;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import mo.core.Utils;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

/**
 *
 * @author felo
 */
public class PluginUncompressor {
    
    private String tempFileName;
    
    private final String fileName;
    
    
    private final String pluginsFolder
            = Utils.getBaseFolder() + "/plugins";
    
    private final String pluginName;
    
    private final String version;
    
    ByteArrayOutputStream file;
        
    public PluginUncompressor(ByteArrayOutputStream r, String fileName, String pluginName, String version){        
        this.fileName = fileName.trim();        
        setTempName();        
        this.file = r; 
        this.pluginName = pluginName.trim();
        this.version = version.trim();
    }

    public boolean uncompress() throws IOException{
        
        writeTempFile();
        
        boolean compress = false;
        
        String destination = pluginsFolder + "/" + this.pluginName + "-" + this.version;
        
        try {
            
            if(fileName.endsWith(".zip")){
                compress = true;
                uncompress(tempFileName, destination, ArchiverFactory.createArchiver(ArchiveFormat.ZIP));

            } 
            else if(fileName.endsWith(".tar.gz")){
                compress = true;
                uncompress(tempFileName, destination, ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP));
            }
            
            else if(fileName.endsWith(".tar.bz2")){
                compress = true;
                uncompress(tempFileName, destination, ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.BZIP2));          
            }   
            
            /*else if(fileName.endsWith(".7z")){
                compress = true;
                uncompress(tempFileName, destination, ArchiverFactory.createArchiver(ArchiveFormat.SEVEN_Z));          
            } */
            
            else if(fileName.endsWith(".tar")){
                compress = true;
                uncompress(tempFileName, destination, ArchiverFactory.createArchiver(ArchiveFormat.TAR));          
            } 
            
            else if(fileName.endsWith(".rar")){
                compress = true;
                unrar(tempFileName, destination);

            } else {
                compress = false;
                
                Path destDirectory = Paths.get(pluginsFolder, this.pluginName + "-" + this.version);
                
                File destDir = new File(destDirectory.toString());
                if (!destDir.exists()) {
                    destDir.mkdir();                    
                }                                
                
                Files.copy((new File(tempFileName)).toPath(), Paths.get(destDir.getAbsolutePath(), this.fileName), StandardCopyOption.REPLACE_EXISTING);

            }
            
        } catch(IOException e){
            System.out.println("Error");
            e.printStackTrace();
            throw e;
            
        } finally {
            cleanTemp();
            return compress;
        }

    }
    
    
    
    private void writeTempFile() throws IOException{        
        FileOutputStream fos = new FileOutputStream(tempFileName);
        fos.write(this.file.toByteArray());
        fos.close();
    }
    
    private void setTempName(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        tempFileName = "temp" + sdf.format(cal.getTime());
    }
    
    private void cleanTemp() throws IOException{
        deleteIfExists(Paths.get(tempFileName));   
    }
    
    private void uncompress(String in, String outDir, Archiver archiver) throws FileNotFoundException, IOException{        
        
        File archive = new File(in);
        File destination = new File(outDir);
        archiver.extract(archive, destination); 
        
    }
    
    
    private void unrar(String rarFilePath, String destDirectory) throws IOException{
        
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        
        final File rar = new File(rarFilePath);  
        final File destinationFolder = new File(destDirectory);  
        ExtractArchive extractArchive = new ExtractArchive();  
        extractArchive.extractArchive(rar, destinationFolder);
    }
    

    
    
}
