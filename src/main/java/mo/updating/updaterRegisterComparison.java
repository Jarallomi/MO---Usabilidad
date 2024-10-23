package mo.updating;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Clase que permite la comparacion de los registros .txt creados que contienen los archivos locales/remotos junto con su fecha de modificacion correspondiente
 */
public class updaterRegisterComparison {

    /**
     * Metodo que compara 2 archivos con igual formato, para obtener respuesta a si los archivos remotos han sido modificados. Primero leera todos los archivos registrados con su fecha de modificacion correspondiente
     * en ambos .txt, para procesarlos via arreglos
     * @param localFile Ruta del registro Register.txt con los archivos locales actuales 
     * @param remoteFile Ruta del registro remoteRegister.txt que se forma a traves de la obtencion de un Json que representa RegisterFiles.txt del repositorio github
     * @return true si hay diferencias en los archivos. Ya sea por tamaño, la no existencia de un archivo local comparado con el remoto o, alguna diferencia en la fecha de modificacion
     * @throws IOException
     */
    public static boolean differencesInRegisters(Path localFile, Path remoteFile, String mode) throws IOException {
        System.out.println("(updaterRegisterComparison.java) - Los archivos a comparar: " + localFile.toString() + " vs " + remoteFile.toString());
        //CASO BASE: Algo sucedio con los registros de uso interno que, no estan disponibles o no existen desde procesos anteriores
        if( !Files.exists(localFile) || !Files.exists(remoteFile)) {
            if (!Files.exists(localFile)) {
                System.out.println("(updaterRegisterComparison.java) - Archivo local no existe/no se encuentra");
            }
            if (!Files.exists(remoteFile)) {
                System.out.println("(updaterRegisterComparison.java) - Archivo con registros remoto no existe/no se encuentra");
            }
            System.out.println("(updaterRegisterComparison.java) - Los archivos internos de comparacion no estan disponibles - Se cancela la comparacion entre registros y la actualizacion");
            //Se retorna false para indicar a procesos posteriores que no no hubo comparacion ni habra permisos para actualizar
            return false;
        }

        //Variables limitadoras
        int evenOdd = 0;
        String fileName = "";
        //Se guarda todo el contenido respectivo de los archivos en bruto como strings
        List<String> localFileContent = Files.readAllLines(localFile);
        List<String> remoteFileContent = Files.readAllLines(remoteFile);

        System.out.println("(updaterRegisterComparison.java) - Tamaños de listas iniciales ->" + String.valueOf(localFileContent.size()) + " VS " + String.valueOf(remoteFileContent.size())+ "\n");

        //Se inicializa una lista con objetos File
        ArrayList<fileClass> localFileArray = new ArrayList<>();
        ArrayList<fileClass> remoteFileArray = new ArrayList<>();

        //Se inicializa una lista con los formatos de fecha para su procesado
        Date date = new Date();
        //Se inicializa una variable que permita procesar las fechas que estan como strings
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        //Para cada registro, se tomaran los pares como archivos, y los impares como ultimas fechas de modificacion siguiendo el formato de los registros
        //Se usara la clase fileclass propia de updater para manejar y procesar la informacion
        System.out.println("(updaterRegisterComparison.java) - Se procesa el registro local en arreglos\n");
        try {
            for (String content : localFileContent) {
                if(evenOdd == 0){
                    fileName = content;
                    evenOdd += 1;
                }else {                    
                    date = dateFormat.parse(content);
                    evenOdd -= 1;
                    fileClass file = new fileClass(fileName, date);
                    localFileArray.add(file);
                }
            } 

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //IDEM con el registro local, pero ahora ocupando lo remoto
        System.out.println("(updaterRegisterComparison.java) - Se procesa el registro remoto en arreglos\n");
        evenOdd = 0;
        try {
            for (String content : remoteFileContent) {
                if(evenOdd == 0){
                    fileName = content;
                    evenOdd += 1;
                }else {                    
                    date = dateFormat.parse(content);
                    evenOdd -= 1;
                    fileClass file = new fileClass(fileName, date);
                    remoteFileArray.add(file);
                }
            } 

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Primera regla: Los registros deben llevar la misma cantidad de archivos
        //Caso contrario: (por ahora) no tenemos formas de saber como afectara a la compilacion de mo
        System.out.println("(updaterRegisterComparison.java) - Comparacion de tamaños de arreglos ->" + String.valueOf(localFileArray.size()) + "VS " + String.valueOf(remoteFileArray.size())+ "\n");
        if(!(localFileArray.size() == remoteFileArray.size())){
            System.out.println("Caso 1: Tamaños Diferentes\n");

            //Se borran los registros generados para la comparacion
            //DEBUG: Descomentar si es necesario hacer una comparacion
            try {
                // Elimina los registros que fueron sometidos a comparacion (argumentos de este metodo).
                if (mode.equals("mo")) {
                    Files.deleteIfExists(localFile);
                    System.out.println("(updaterRegisterComparison.java) - Los archivos de registro comparados fueron borrados");
                } else {
                    //Files.deleteIfExists(remoteFile);
                System.out.println("(updaterRegisterComparison.java) - Los archivos de registro comparados fueron borrados");
                }                
            } catch (IOException e) {
                System.out.println("(updaterRegisterComparison.java) - Error al borrar los archivos de registro comparados" + e.getMessage());
            }

            return true;
        }

        System.out.println("(updaterRegisterComparison.java) - Caso 1 SUPERADO\n");

        //Segunda regla: Si anteriormente los largos son iguales, se debe revisar que todos los archivos locales estan en el repositorio remoto
        //nota: Esta parte del metodo puede usarse para definir que elementos pueden ser ignorados por su tipo de archivo ej; .jpg o .info
        for (fileClass localFileRevisor :localFileArray) {
            if(remoteFileContent.contains(localFileRevisor.getName())){
                continue;
            } else{
                System.out.println("(updaterRegisterComparison.java) - Caso 2: discrepancia en archivos\n");
                System.out.println("(updaterRegisterComparison.java) - El archivo que tiene descrepancia es " + localFileRevisor.getName() + " con fecha: " + localFileRevisor.getDate() + "\n");

                //Se borran los registros generados para la comparacion
                //DEBUG: Descomentar si es necesario hacer una comparacion
                try {
                    // Elimina los registros que fueron sometidos a comparacion (argumentos de este metodo).
                if (mode.equals("mo")) {
                    Files.deleteIfExists(localFile);
                    System.out.println("(updaterRegisterComparison.java) - Los archivos de registro comparados fueron borrados");
                } else {
                    //Files.deleteIfExists(remoteFile);
                    System.out.println("(updaterRegisterComparison.java) - Los archivos de registro comparados fueron borrados");
                }  
                } catch (IOException e) {
                    System.out.println("(updaterRegisterComparison.java) - Error al borrar los archivos de registro comparados" + e.getMessage());
                }

                return true;
            }
        }

        System.out.println("(updaterRegisterComparison.java) - Caso 2 SUPERADO\n");

        //Tercera regla; Si los largo son iguales y si todos los archivos locales estan en los remotos, entonces 
        //Con la informacion de los arreglos de fileclass en posesion, se procede a generar la comparacion de los archivos segun su nombre y fecha de modificacion 
        //Aprovechando que estan en arreglos, tienen metodos get y los arreglos les dan indice
        //NOTA DEBUG; no olvidar que los cambios en codigo fuente de updater no afectan a los registros
        for (fileClass localFileRevisor : localFileArray) {
            for(fileClass remoteFileRevisor : remoteFileArray){
                //System.out.println("Comparando " + localFileRevisor.getName() + " con " + remoteFileRevisor.getName() + "\n");
                //Cuando los fileClass a revisar sean iguales en nombre
                if(localFileRevisor.getName().equals(remoteFileRevisor.getName())){
                    if(fileClass.isSameFileButBeforeDate(localFileRevisor, remoteFileRevisor)){
                        //Si el archivo local revisado es el mismo tanto en nombre pero es anterior en fecha de modificacion al remoteFileRevisor, Existe diferencia (atraso) en un archivo y se debe actualizar
                        System.out.println("(updaterRegisterComparison.java) - Caso 3: Un archivo presenta diferencias desfavorables en la fecha de modificacion .> " + localFileRevisor.getName() + " local: " + localFileRevisor.getDate() + " remoto: " + remoteFileRevisor.getDate() + " \n");

                        //Se borran los registros generados para la comparacion
                        //DEBUG: Descomentar si es necesario hacer una comparacion
                        try {
                            // Elimina los registros que fueron sometidos a comparacion (argumentos de este metodo).
                            if (mode.equals("mo")) {
                                Files.deleteIfExists(localFile);
                                System.out.println("(updaterRegisterComparison.java) - Los archivos de registro comparados fueron borrados");
                            } else {
                                //Files.deleteIfExists(remoteFile);
                            System.out.println("(updaterRegisterComparison.java) - Los archivos de registro comparados fueron borrados");
                            }  
                        } catch (IOException e) {
                            System.out.println("(updaterRegisterComparison.java) - Error al borrar los archivos de registro comparados" + e.getMessage());
                        }

                        return true;
                        
                    } else  {
                        //caso contrario, la fecha de localFileRevisor es igual o superior, seguimos con el siguiente archivo
                        break;                        
                    }
                } else {
                    //Si el nombre del archivo remoto a revisar no es igual al local a revisar, se continua con otro remoteFileRevisor
                    continue;
                }
            }
            //Si el for interno fue roto con break, entonces se toma el proximo localFileRevisor para evitar trabajo redundante
            continue;
        }
    
        System.out.println("(updaterRegisterComparison.java) - Caso 3 SUPERADO\n");
        System.out.println("(updaterRegisterComparison.java) - Casos superados: Los registros no indican diferencia\n");

        //Se borran los registros generados para la comparacion
        //DEBUG: Descomentar si es necesario hacer una comparacion
        
        try {
            // Elimina los registros que fueron sometidos a comparacion (argumentos de este metodo).
            if (mode.equals("mo")) {
                Files.deleteIfExists(localFile);
                System.out.println("(updaterRegisterComparison.java) - Los archivos de registro comparados fueron borrados");
            } else {
                //Files.deleteIfExists(remoteFile);
            System.out.println("(updaterRegisterComparison.java) - Los archivos de registro comparados fueron borrados");
            }  
        } catch (IOException e) {
            System.out.println("(updaterRegisterComparison.java) - Error al borrar los archivos de registro comparados" + e.getMessage());
        }

        return false;
    }

    /**
     * Este es un pequeño metodo que borrara el remote file en caso de que no se cumplan los permisos para actualizar
     * Se hace para que no quede sobrando, ya que si hay permisos, remotefile se mantiene para la comparacion post update
     * @param permissiBoolean booleano que se toma desde la comparacion entre numero de versiones
     * @param remoteFile El path con la ruta del remote file creado
     * @throws IOException Lanza un error en caso de deleteIfExists obtenga algun error
     */
    public static void deleteFilesIfNotPermission(Boolean permissiBoolean, Boolean answer, Path remoteFile) throws IOException{
        if (!permissiBoolean || !answer){
            System.out.println("(updaterRegisterComparison.java) - Borrando RemoteFile al no haber actualizacion");
            try {
                Files.deleteIfExists(remoteFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }

}
