package mo.updating;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import mo.updating.fileClass;

public class updaterPostUpdateProcesses {
    
    public static void deleteLeftoversFiles() throws IOException{
        //Declaro arreglo con archivos locales post update
        List<Path> postUpdatepathList = new ArrayList<>();

        //formo variable con directorio de inicio
        Path startDir = Paths.get("./");  // Se partira la recoleccion desde la carpeta padre el proyecto, desde carpeta Java

        //coleccionar todos los archivos desde inicio declarado
        try {
            postUpdatepathList = Files.walk(startDir)
                                        .filter(Files::isRegularFile)
                                        .collect(Collectors.toList());
            //DEBUG: Se muestran todos los path recolectados via cmd
            //pathList.forEach(System.out::println);

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //Se crea un archivo para postprocesado
        Path file = Paths.get("./PostUpdateRegister.txt");
        try {
            // Crea el archivo si no existe
            if (!Files.exists(file)) {
                Files.createFile(file);
                System.out.println("El registro pos update no existia, Se ha creado el archivo 'PostUpdateRegister.txt' con lo necesario\n");
            } else {
                System.out.println("El archivo 'PostUpdateRegister.txt' ya existe. Se borra para crear uno nuevo\n");
                Files.delete(file);
                Files.createFile(file);
                System.out.println("Se ha creado otro archivo 'PostUpdateRegister.txt\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Abre el archivo PostUpdateRegister.txt en modo de agregar (APPEND)
            BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    
            for (Path pathfile : postUpdatepathList) {
                //Se anotan de manera local todos los directorios y fechas de modificacion mientras no contengan las frases en el IF siguiente
                try {
                    if ( !Arrays.stream(updaterExceptionList.fileNamesToAvoid).anyMatch(keyword -> pathfile.toString().contains(keyword)) ){
                        BasicFileAttributes attributes = Files.readAttributes(pathfile, BasicFileAttributes.class);
                        long milisegundos = attributes.lastModifiedTime().toMillis();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        String modificationDate = sdf.format(milisegundos);

                        // Escribe en el archivo siguiendo el limitador
                        writer.write(pathfile.toString());
                        writer.newLine();
                        writer.write(modificationDate);
                        writer.newLine();
                    }                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    
            // Cierra el archivo para guardar cambios
            writer.close();
            System.out.println("(updaterPostUpdateProcess.java) - Eliminando ultimo salto de linea\n");
            //Lo volvemos a abrir para eliminar el ultimo salto de linea y evitar insconsistencias en la comparacion de registros
            File file2 = new File("./PostUpdateRegister.txt");
            RandomAccessFile raf = new RandomAccessFile(file2, "rw");
            long size = raf.length();
            if (size > 0) {
                raf.setLength(size - 2);
                }
            raf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Se guarda todo el contenido respectivo de los archivos en bruto como strings
        List<String> postUpdateLocalFileContent = Files.readAllLines(Paths.get("./PostUpdateRegister.txt"));
        List<String> remoteFileContent = Files.readAllLines(Paths.get("./RemoteRegister.txt"));

        System.out.println("(updaterPostUpdateProcess.java) - Tamaño Listas" + String.valueOf(postUpdateLocalFileContent.size()) + " VS " + String.valueOf(remoteFileContent.size())+ "\n");

        //Se inicializa una lista con objetos File
        ArrayList<fileClass> postUpdatelocalFileArray = new ArrayList<>();
        ArrayList<fileClass> remoteFileArray = new ArrayList<>();
        
        //Se inicializa una lista con los formatos de fecha para su procesado
        Date date = new Date();
        //Se inicializa una variable que permita procesar las fechas que estan como strings
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        //Para el nuevo registro, se tomaran los pares como archivos, y los impares como ultimas fechas de modificacion siguiendo el formato de los registros
        //Se usara la clase fileclass propia de updater para manejar y procesar la informacion
        System.out.println("(updaterPostUpdateProcess.java) - Se procesa el nuevo registro local post update en arreglos\n");
        //Variable limitadoras
        int evenOdd = 0;
        String fileName = "";
        try {
            for (String content : postUpdateLocalFileContent) {
                if(evenOdd == 0){
                    fileName = content;
                    evenOdd += 1;
                }else {                    
                    date = dateFormat.parse(content);
                    evenOdd -= 1;
                    fileClass postfile = new fileClass(fileName, date);
                    postUpdatelocalFileArray.add(postfile);
                }
            } 

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Se hara el mismo proceso con el arreglo de fileClass post update
        System.out.println("(updaterPostUpdateProcess.java) - Se procesa el nuevo registro local post update en arreglos\n");
        //Variable limitadoras
        evenOdd = 0;
        fileName = "";
        try {
            for (String content : remoteFileContent) {
                if(evenOdd == 0){
                    fileName = content;
                    evenOdd += 1;
                }else {                    
                    date = dateFormat.parse(content);
                    evenOdd -= 1;
                    fileClass postfile = new fileClass(fileName, date);
                    remoteFileArray.add(postfile);
                }
            } 

        } catch (ParseException e) {
            e.printStackTrace();
        }


        //Este proceso comparara los arreglos de una forma diferente como se maneja en updaterRegisterComparisson
        //Si un archivo local luego del update NO esta en el registro remoto con anterioridad, este se borrara
        for(fileClass postUpdatefileclass: postUpdatelocalFileArray){
            if(!fileClass.isInArrayByName(postUpdatefileclass, remoteFileArray)){
                //Si su existencia por nombre no fue igualada, este se borra
                Files.deleteIfExists(Paths.get(postUpdatefileclass.getName()));
                System.out.println("(updaterPostUpdateProcess.java) - Archivo Sobrante borrado : " + postUpdatefileclass.getName());
            }
        }

        //Finalmente, se intenta borrar el registro post update
        try {
            Files.deleteIfExists(Paths.get("./PostUpdateRegister.txt"));
            System.out.println("(updaterPostUpdateProcess.java) - Archivo ./PostUpdateRegister.txt borrado" );
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }//fin metodo

    /**
     * Metodo que borra elementos innecesarios luego de la actualizacion de un plugin
     * @param nameRepository String con el nombre del repositorio de donde se descarga el codigo fuente del plugin. Sirve para armar las rutas relativas a ser usadas para borrar
     * @throws IOException
     */
    public static void deleteLeftoversFilesPlugin(String nameRepository) throws IOException{
        Path relativePathZip = Paths.get("./ups/Repo.zip");
        Path relativePathFolderDownloaded = Paths.get("./ups/" + nameRepository);
        System.out.println("(updaterPostUpdateProcesses.java) - Intentando borrar carpeta de " + nameRepository + " en: " + relativePathZip.toString());
        try {
            Files.deleteIfExists(relativePathZip);
            System.out.println("(updaterPostUpdateProcesses.java) - El .zip de " + nameRepository + "Fue borrado en: " + relativePathZip.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Files.walk(relativePathFolderDownloaded)
                 .sorted(Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);

            System.out.println("(updaterPostUpdateProcess) -Carpeta " + relativePathFolderDownloaded + " borrada");
        } catch (Exception e) {
            // TODO: handle exception
            System.err.println("(updaterPostUpdateProcess) - Error al borrar la carpeta descargada: " + e.getMessage());
        }
    }


    /**
     * Metodo que reemplaza los archivos locales de version y registro de archivos por los descargados. Se llama luego de haber actualizado y movido el nuevo .jar del plugin. 
     * Es importante por esta funcion, que RegisterFile.txt y Version.txt se llamen como tal en el repositorio
     * @param pathToFatherFolderDownloaded String con la ruta de donde se ubiquen Version.txt y RegisterFile.txt del plugin. Por defecto, debiese ser el mismo donde esta build.gradle, por lo que ocupara la variable pathToExecuteWrapperGradle del plugin
     * @param pathLocalPluginRegister String con la ruta de donde se ubique RegisterFile....txt del plugin de manera local en carpeta ups
     * @param pathLocalVersionPlugin String con la ruta de donde se ubique RegisterFile.....txt del plugin de manera local en carpeta ups
     */
    public static void updatingLocalRegisterAndLocalVersionPlugin(String pathToFatherFolderDownloaded, String pathLocalPluginRegister, String pathLocalVersionPlugin){
        Path targetPathRegisterFile = Paths.get(pathLocalPluginRegister);
        Path targetPathVersionFile = Paths.get(pathLocalVersionPlugin);
        //Se intenta actualizar el registro
        try {
            //En los repositorios, el registro se llama FileRegister.txt
            //Atencion con el orden
            Path originPathRegister = Paths.get(pathToFatherFolderDownloaded + "/FileRegister.txt");
            System.out.println("(updaterPostUpdateProcess.java) - Se actualizara el registro del plugin con: " + originPathRegister.toString() + " a " + targetPathRegisterFile.toString());
            Files.move(originPathRegister, targetPathRegisterFile, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("(updaterPostUpdateProcess.java) - Se actualizo el registro del plugin localmente desde: " + pathToFatherFolderDownloaded + " a " + pathLocalPluginRegister);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Se intenta actualizar el indicador de version del plugin
        try {
            Path originPathVersion = Paths.get(pathToFatherFolderDownloaded + "/Version.txt");
            System.out.println("(updaterPostUpdateProcess.java) - Se actualizara el indicador de version con: " + originPathVersion.toString() + " a " + targetPathVersionFile.toString());
            Files.move(originPathVersion, targetPathVersionFile, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("(updaterPostUpdateProcess.java) - Se actualizo el indicador de version del plugin localmente desde: " + pathToFatherFolderDownloaded + " a " + pathLocalVersionPlugin);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}//Fin clase
