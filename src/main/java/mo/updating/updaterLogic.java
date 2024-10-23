package mo.updating;

import java.io.File;
import java.io.IOException;
import java.lang.String;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Clase que contiene de manera general toda la logica del updater. Dividida en 3 metodos
 */
public class updaterLogic {

    /**
     * Primer metodo contendor de la logica del updater. Obtiene los primeros permisos para establecer si se puede o no
     *  actualizar Multimodal Observer. Este metodo es llamado por primera vez por el controlador del Splasher
     * @param localVersionString String con el nombre del archivo de texto que indique la version local
     * @param aTokenString Una de las tres partes del Token generado
     * @param bTokenString Una de las tres partes del Token generado
     * @param cTokenString Una de las tres partes del Token generado
     * @param apiUrlString String con el enlace adecuado que permita la obtencion del RAW con el contenido del archivo de texto que indica la version remota
     * @return true si las comparaciones entre registros entre archivos y las versiones declaradas arrojan diferencias necesarias para actualizar
     */
    public static boolean updaterpermissionsLogic(String localVersionString, String aTokenString, String bTokenString, String cTokenString, String remoteVersionApiUrlString) {
        boolean permission1 = false;

        try {
            //Lectura de la version local indicada en version.txt
            String localVersion = updaterPermissions.getLocalVersion(localVersionString);

            //Funcion que obtiene version.txt desde el repositorio, que sera anotado como Remoteregister.txt localmente
            String remoteVersion = updaterPermissions.remoteVersionRepository(aTokenString, bTokenString, cTokenString, remoteVersionApiUrlString);

            //Se comparan las versiones declaradas en los txt de version (true == permiso para actualizar)
            permission1 = updaterPermissions.permissionToUpdateByVersions(localVersion, remoteVersion);

            } catch (IOException e) {
            e.printStackTrace();
        }
        //por defecto se arrojara false
        return permission1;
    }

    /**
     * Metodo que contiene la logica y los submetodos para comparar los registros de archivos que contienen los archivos y sus fechas modificacion
     * para obtener booleans que den permiso a la actualizacion. Metodo llamado por primera vez en el controlador del Splash
     * @param permission1 Boolean obtenido de las comparaciones entre los indicadores de version local vs Remota
     * @param startDirRegister String que indica donde comenzar a hacer la lista de archivos y atributos para generar una lista de archivos con atributos filtrados
     * @param aToken Una de tres partes del token que sirve para acceder la lectura del repositorio
     * @param bToken Una de tres partes del token que sirve para acceder la lectura del repositorio
     * @param cToken Una de tres partes del token que sirve para acceder la lectura del repositorio
     * @param remoteRegisterApiUrl String con el Url para acceder al RAW del registro remoto a traves de la api github
     * @return true si las comparaciones indican diferencia, false si es innecesario actualizar
     */
    public static boolean updaterComparissonLogic(boolean permission1, String startDirRegister, String aToken, String bToken, String cToken, String remoteRegisterApiUrl){
        try{
            //Se crea una lista con todas las rutas de los archivos locales de MO
            List<Path> PathFiles = updaterRegisterCreator.listPathFiles(startDirRegister);

            //Se crea registro local para uso interno usando todas las rutas obtenidas y filtrando excepciones
            updaterRegisterCreator.createRegisterFile(PathFiles, "./Register.txt");

            //Se crea registro de los archivos remotos en el repositorio correspondiente
            updaterRemoteFilesProcess.getRemoteFiles(aToken, bToken, cToken, remoteRegisterApiUrl);

            //Se comparan los registros generados de manera interna para verificar por segunda vez si corresponde actualizar (true == existen diferencias)
            boolean answer = updaterRegisterComparison.differencesInRegisters(Paths.get("./Register.txt"), Paths.get("./RemoteRegister.txt"), "mo");
            System.out.println("(updaterLogic.java) - Procesados los registros en arreglos! y las versiones\n--- El permiso por comparar versiones es: " + String.valueOf(permission1) + "\n--- Y las diferencias en los registros son: " + String.valueOf(answer));
            
            //Si updater genero booleans que no permiten la actualizacion, se borran los registros generados internamente
            updaterRegisterComparison.deleteFilesIfNotPermission(permission1, answer, Paths.get("./RemoteRegister.txt") );

            return answer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("(updaterLogic.java) - Respuesta defecto: false");
        return false;

    }


    /**
     * Metodo que contiene la logica y los submetodos que permiten la descarga, la descompresion, el reemplazo y la compilacion de una nueva version de Mo/plugin.
     *  Usado por primera vez por el controlador de la vista updating
     * @param permission1 Boolean obtenido desde el proceso anterior de compracion entre numero de version local vs remoto
     * @param answer Boolean obtenido desde el proces anterior entre la comparacion entre registros internos que comparan el registro local vs remoto
     * @param downloadLinkZip String que contiene la url que descarga el .zip del repositorio
     * @param targetDirectoryToMoveZip String que contiene la ruta relativa para mover el zip descargado (mover a carpeta arriba es util para descomprimir y reemplazar)
     * @param zipDownloadedPath String con path relativo que indica la ubicacion del zip descargado
     * @param targetDirectoryToExtract String con el path del directorio a usar para descomprimir (se creara si no existe)
     * @param pathToExecuteWrapperGradle String con el path relativo para indicar donde usar el comando gradlew.bat/gradle build para compilar
     */
    public static void updaterUpdatingLogic(boolean permission1, boolean answer, String downloadLinkZip, String targetDirectoryToMoveZip ,String zipDownloadedPath, String targetDirectoryToExtract, String pathToExecuteWrapperGradle){
        try {
            //Si las respuestas son las esperadas, se procede a la descarga de los archivos del repositorio en formato .zip desde link predeterminado
            boolean permissionToDownloadZip = updaterDownloader.downloadFilesFromRepository(permission1, answer, downloadLinkZip, targetDirectoryToMoveZip);

            //Se procede a reemplazar los archivos con los del comprimible
            updaterZipProcess.unzipFile(zipDownloadedPath, targetDirectoryToExtract, permissionToDownloadZip);

            //Se acciona el comando que permite la ejecucion del wrapper de gradle
            gradleBuildCommand(pathToExecuteWrapperGradle);

            //Con el nuevo .jar creado, se verifica ue no hayan archivos sobrantes borrandolos
            //updaterPostUpdateProcesses.deleteLeftoversFiles();            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo que contiene la logica y los submetodos que permiten la descarga, la descompresion, el reemplazo y la compilacion de una nueva version de Mo/plugin.
     *  Usado por primera vez por el controlador de la vista updating. Es igual que la logica de actualizar MO, pero no ejecuta borrado de archivos
     * @param permission1 Boolean obtenido desde el proceso anterior de compracion entre numero de version local vs remoto
     * @param answer Boolean obtenido desde el proces anterior entre la comparacion entre registros internos que comparan el registro local vs remoto
     * @param downloadLinkZip String que contiene la url que descarga el .zip del repositorio
     * @param targetDirectoryToMoveZip String que contiene la ruta relativa para mover el zip descargado (mover a carpeta arriba es util para descomprimir y reemplazar)
     * @param zipDownloadedPath String con path relativo que indica la ubicacion del zip descargado
     * @param targetDirectoryToExtract String con el path del directorio a usar para descomprimir (se creara si no existe)
     * @param pathToExecuteWrapperGradle String con el path relativo para indicar donde usar el comando gradlew.bat/gradle build para compilar
     */
    public static void updaterUpdatingPluginLogic(boolean permission1, boolean answer, String downloadLinkZip, String targetDirectoryToMoveZip ,String zipDownloadedPath, String targetDirectoryToExtract, String pathToExecuteWrapperGradle){
        try {
            //Si las respuestas son las esperadas, se procede a la descarga de los archivos del repositorio en formato .zip desde link predeterminado
            boolean permissionToDownloadZip = updaterDownloader.downloadFilesFromRepository(permission1, answer, downloadLinkZip, targetDirectoryToMoveZip);

            //Se procede a reemplazar los archivos con los del comprimible
            updaterZipProcess.unzipFile(zipDownloadedPath, targetDirectoryToExtract, permissionToDownloadZip);

            //Se acciona el comando que permite la ejecucion del wrapper de gradle
            updaterCommands.gradleBuildCommand(pathToExecuteWrapperGradle);       

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gradleBuildCommand(String pathToExecuteWrapperGradle){
        //Se obtiene el path absoluto (ejemplo abs: C:\\Users\\Usuario\\Documentos\\miArchivo.txt) para ejecutar el wrapper de gradle
        String directory = new File(pathToExecuteWrapperGradle).getAbsolutePath();

        //Se obtiene el sistema operativo
        System.out.println("(updatercommands.java) - Iniciando");
        String operativeSystem = System.getProperty("os.name").toLowerCase();

        //Caso 1: el OS es Windows
        if(operativeSystem.contains("win")){
            //Se genera una instancia que se encarga de ejecutar cmd y accionar el .bat
            try {
                ProcessBuilder processB = new ProcessBuilder("cmd.exe", "/c", "gradlew.bat build");
                processB.directory(new File(directory));

                System.out.println("(updatercommands.java) - Ejecutando : gradlew.bat build en OS: " + operativeSystem + " en directorio: " + directory);
                Process process = processB.start();
                process.waitFor();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                }
        } else if (operativeSystem.contains("nix") || operativeSystem.contains("nux") || operativeSystem.contains("mac")){
            //comando para Linux o Unix
            try {
                ProcessBuilder processB = new ProcessBuilder("bash", "-c", "gradlew build");
                processB.directory(new File(directory));

                System.out.println("(updatercommands.java) - Ejecutando : gradlew build en OS: " + operativeSystem);
                Process proceso = processB.start();
                proceso.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("(updatercommands.java) - Sistema operativo no compatible con el comando");
        }
    }

}


