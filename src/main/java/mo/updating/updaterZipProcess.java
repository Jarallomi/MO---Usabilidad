package mo.updating;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Clase que se encarga de los procesos relacionados al .zip descargado desde Github de MO
 */
public class updaterZipProcess {
    

    /**
     * Metodo que descomprime el .zip descargado desde el repositorio ya una vez que fue descargado
     * @param zipDownloadedPath Es un String con la ruta del .zip descargado
     * @param targetDirectory Es un String con la ruta de destino a descomprimir
     * @param permissionFromDownload Boolean recibido con anterioridad para conocer si existe la posibilidad de descomprimir o no
     * @throws IOException
     */
    public static void unzipFile(String zipDownloadedPath, String targetDirectoryToExtract, boolean permissionFromDownload) throws IOException {
        //Se revisa si el archivo .zip fue descargado
        if (permissionFromDownload == false){
            System.out.println("(updaterZipProcess.java) - El proceso de unzipping no tiene los permisos\n");
            return;
        }

        // En caso de que por algun motivo no exista el directorio, se crea
        //Aunque este caso es poco probable, se considera por alguna eventualidad
        File dir = new File(targetDirectoryToExtract);
        if(!dir.exists()) dir.mkdirs();

        //buffer para lectura y escritura de los contenidos del zip
        byte[] buffer = new byte[1024];

        // A traves de este bloque try, nos aseguramos que se cierren los recursos usados
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipDownloadedPath))) {
            //Obtenemos el contenido del zip
            ZipEntry zipEntry = zis.getNextEntry();
            //Uno por uno hasta que no sea null
            while (zipEntry != null) {
                //obteniendo su nombre se crea un nuevo File
                String fileName = zipEntry.getName();
                File newFile = new File(targetDirectoryToExtract, fileName);

                //Como el .zip descargado incluye el nombre del arbol principal, se le reemplaza con un "" (asi al unzip se hace reemplazo directo de los archivos!)
                if(fileName.contains("-master")){
                    newFile = new File(targetDirectoryToExtract, fileName.replace("-master", ""));
                    }
                //Evitamos que la descompresion toque elementos relcionados al launcher
                //NOTA DEBUG: Esto impide que el src del launcher se actualice, lo que hace que el launcher en si solo este en los Release del repositorio
                if(!Arrays.stream(updaterExceptionList.filesToNotExtract).anyMatch(keyword -> fileName.contains(keyword))){                   
                    if (zipEntry.isDirectory()) {
                        //Si lo procesado resulta ser un directorio, se crea un igual como tal
                        System.out.println("(updaterZipProcess.java) - Unzipping Directorio: " + fileName);
                        newFile.mkdirs();
                    } else {
                        //Caso contrario, es un archivo y con su nombre se procede a crear el mismo
                        //Primero, se creara un directorio padre en caso de que no exista
                        new File(newFile.getParent()).mkdirs();
                        //Y con necesario, se crea el nuevo archivo hasta agotar los bytes del buffer de lectura
                        try (FileOutputStream fos = new FileOutputStream(newFile)) {
                            System.out.println("(updaterZipProcess.java) - Unzipping Archivo: " + fileName);
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                        }
                    }
                }
                
                //Se cierra el ZipEntry y se continua al siguiente archivo del .zip
                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("(updaterZipProcess.java) - El proceso de unzipping ha terminado\n");
        //Se procede con el borrado del archivo .zip descargado
        try {
            // Se obtiene la ruta del .zip descargado (que es la carpeta donde esta el .jar launcher)
            Path ZIPdownloaded = Paths.get("./Repo.zip");

            // Si existe el archivo, se eliminara
            Files.deleteIfExists(ZIPdownloaded);

            System.out.println("(updaterZipProcess.java) - Archivo .zip Descargado Eliminado");
        } catch (IOException e) {
            System.out.println("(updaterZipProcess.java) - Error intentando eliminar el .zip" + e.getMessage());
        }
    }

}
