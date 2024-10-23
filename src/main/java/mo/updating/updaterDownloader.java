package mo.updating;

import java.io.IOException;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


/**
 * Clase que permite la descarga de los archivos remotos de Multimodal observer
 */
public class updaterDownloader {

    /**
     * Metodo que descarga el .zip que provee github desde el link predeterminado, si existen los permisos necesarios
     * @param permissionToUpdateByVersions
     * @param differencesInRegisters
     * @return true Si la descarga del .zip fue existosa
     * @return false Si la descarga no fue efectuada
     * @throws IOException
     */   
    public static boolean downloadFilesFromRepository(boolean permissionToUpdateByVersions, boolean differencesInRegisters, String downloadLinkZip, String targetDirectoryToMoveZip) throws IOException{
        //Se revisan los permisos dados por los resultados de las comparaciones
        if(permissionToUpdateByVersions == false || differencesInRegisters == false){
            System.out.println("(updaterDownloader.java) - No se cumplen los requisitos para actualizar\nSe vuelve a Updater.main() \n");
            System.out.println("(updaterDownloader.java) - permissionToUpdateByVersions indica: " + String.valueOf(permissionToUpdateByVersions) + "\n" + "differencesInRegisters indica: " + String.valueOf(differencesInRegisters) + "\n");
            return false;
        } else {
            try {
                //Se descarga el codigo fuente del repositorio de github desde el link predeterminado
                System.out.println("(updaterDownloader.java) - Se descarga el zip desde el repositorio\n");
                URL website = new URL(downloadLinkZip);
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                FileOutputStream fos = new FileOutputStream("Repo.zip");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                fos.close();
                rbc.close();
                // Se mueve el archivo descargado a la raiz de la carpeta del proyecto
                //Nota: Las rutas indicadas aca podrian ser modificadas a posteriormente
                Path fileDownloaded = Paths.get("Repo.zip");
                Path targetDirectory = Paths.get(targetDirectoryToMoveZip);               
                Path targetFile = targetDirectory.resolve(fileDownloaded.getFileName());
                Files.move(fileDownloaded, targetFile, StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e){
                e.printStackTrace();
            }
            //Con los archivos descargados sin problemas, se retorna true para indicar a la clase de procesamiento del .zip posterior que tiene luz verde para continuar
            return true;
        }
        
        
    }
}
