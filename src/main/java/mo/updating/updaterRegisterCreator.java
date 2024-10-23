package mo.updating;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase que se encarga de crear el registro local de todos los archivos presentes de MO
 */
public class updaterRegisterCreator {
   
    /**
     * Metodo que obtiene los directorios de todos los archivos de MO presentes en carpeta "src" de manera local. Partiendo por la carpeta padre del proyecto
     * @param startString String que indique el directorio donde emepzar a reloectar los archivos y sus atributos
     * @return Una lista de Paths de todos los archivos presentes
     */
    public static List<Path> listPathFiles(String startString) {
        //Declaro variable de salida
        List<Path> pathList = new ArrayList<>();

        //formo variable con directorio de inicio
        //Path startDir = Paths.get("../../../..");  // Atencion, este directorio solo servia con el programa no modulado
        Path startDir = Paths.get(startString);  // Nuevo directorio, para ejecutar usar el comando "java mo.updating.updater" desde la carpeta .../main/java, el programa "partira" aca y no donde este el .class
        
        //coleccionar todos los archivos desde inicio declarado
        try {
            pathList = Files.walk(startDir)
                                        .filter(Files::isRegularFile)
                                        .collect(Collectors.toList());
            //DEBUG: Se muestran todos los path recolectados via cmd
            //pathList.forEach(System.out::println);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return pathList;
    }

    /**
     * Metodo que escribe en un archivo .txt todos los archivos y su fecha de modificacion respectiva, sin contar los excluidos en la funcion misma. Se elimina ademas, la ultima linea para evitar insconsitencias en la comparacion posterior
     * @param pathList Una lista de Paths que es obtenida a traves del metodo listPathFiles
     */
    public static void createRegisterFile(List<Path> pathList, String localRegisterPath){
        Path file = Paths.get(localRegisterPath);

        try {
            // Crea el archivo si no existe
            if (!Files.exists(file)) {
                Files.createFile(file);
                System.out.println("(updaterRegisterCreator.java) - El registro no existia, Se ha creado el archivo " + localRegisterPath + " con lo necesario\n");
            } else {
                System.out.println("(updaterRegisterCreator.java) - El archivo " + localRegisterPath + " ya existe. Se borra para crear uno nuevo\n");
                Files.delete(file);
                Files.createFile(file);
                System.out.println("(updaterRegisterCreator.java) - Se ha creado otro archivo " + localRegisterPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Abre el archivo en modo de agregar (APPEND)
            BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    
            for (Path pathfile : pathList) {
                //Se anotan de manera local todos los directorios y fechas de modificacion mientras no contengan las frases en el IF siguiente
                try {
                    /*La siguiente declaracion en el if hace lo siguiente:
                     * el array de excepciones se convierte en un flujo de stream y al ser de esta forma
                     * con la expresion anyMatch verificamos si al menos un elemento del flujo cumple con 
                     * la condicion dada por la expresion lambda: donde keyword es la representacion de un elemento
                     * del flujo y se usa en la expresion pathfile.toString().contains(keyword)
                     * E.O.P: si una palabra clave esta presente via contains en el pathfile.toString() nos arroja true
                     * pero como se busca lo contrario, se antepone !
                     */
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
            System.out.println("(updaterRegisterCreator.java) - Eliminando ultimo salto de linea\n");
            //Lo volvemos a abrir para eliminar el ultimo salto de linea y evitar insconsistencias en la comparacion de registros
            File file2 = new File(localRegisterPath);
            RandomAccessFile raf = new RandomAccessFile(file2, "rw");
            long size = raf.length();
            if (size > 0) {
                raf.setLength(size - 2);
                }
            raf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
