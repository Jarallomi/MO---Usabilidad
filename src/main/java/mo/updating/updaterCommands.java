package mo.updating;

import java.io.File;
import java.io.IOException;

/**
 * Clase publica que permite la ejecucion de comandos en el sistema operativo presente
 */
public class updaterCommands {
    
    /**
     * Metodo que permite la ejecucion del comando build del wrapper de gradle dependiendo de que sistema operativo este en uso. Usado por peimera vez
     * en el controlador de la vista updating
     * @param pathToExecuteWrapperGradle String que indica el path relativo para ejecutar gradlew/gradle para hacer build
     */
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
