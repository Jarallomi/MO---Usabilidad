package mo.updating;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que se escarga del manejo de actualizaciones de los plugins de Multimodal Observer
 */
public class updaterPluginsUpdating {

    public static List<String> pluginName = new ArrayList<>();
    public static List<String> upFile = new ArrayList<>();



    public static void setUpFilesList(List<String> uPListIncoming){
        upFile.addAll(uPListIncoming);
    }

    /**
     * Metodo que lee el registro de plugins bajo supervision a actualizar
     * @param filePath String que indica la ruta del archivo donde obtener la lista de archivos .up a trabajar
     * @return Una lista con todas las rutas relativas a todos los archivos .up que serviran para actualizar los plugins registrados
     * @throws IOException
     */
    public static List<String> getUpFilePluginsToUpdate(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            List<String> prePluginsList = new ArrayList<>();
            List<String> PluginList = new ArrayList<>();
            try {
                Path path = Paths.get(filePath);
                prePluginsList = Files.readAllLines(path);
                if (prePluginsList.isEmpty()) {
                    System.out.println("updaterPluginsUpdating - El archivo plugins.up está vacío.");
                    PluginList.add("null");
                    return PluginList;
                } else if (prePluginsList.stream().allMatch(String::isEmpty)) {
                    System.out.println("updaterPluginsUpdating - El archivo plugins.up tiene todas sus lineas vacias.");
                    PluginList.add("null");
                    return PluginList;
                } else {
                    for (String line : prePluginsList) {
                    String[] parts = line.split(": ", 2);
                    //Se guarda el nombre del plugin
                    pluginName.add(parts[0]);
                    //Se agrega la reuta de donde esta el archivo .up
                    PluginList.add("./ups/" + parts[1]);
                    System.out.println("(updaterPluginsUpdating.java) - obtenido el .up de nombre: " + parts[1]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return PluginList;
        } else {
            System.out.println("(updaterPluginsUpdating.java) - Archivo plugins.up no detectado - Cerrando Launcher - iniciando MO");
            updater.openMO();
            System.exit(1);
            return null;
        }

        
    }

    /**
     * Metodo que se encarga de manejar la logica de los metodos de comparacion entre los registros de archivos de los plugins registrados en el Updater. 
     * A diferencia del original que se encarga de manejar la actualizacion de MO, este no crea un registro nuevo segun los archivos presentes, dado que no estan localmente, 
     * si no que rescata un registro local que se encuentra en la carpeta ups/registers
     * @param permission1 El Boolean que se obtiene de comparar las versiones registradas
     * @param aToken Una de tres partes del token de actualización
     * @param bToken Una de tres partes del token de actualización
     * @param cToken Una de tres partes del token de actualización
     * @param remoteRegisterApiUrl String con la url que permite la obtencion del RAW del registro remoto ubicado en el repositorio del plugin
     * @param pathToPluginRegisterFile String con la ruta relativa al registro local de archivos del plugin, ubicado en ups/registers
     * @return Boolean con el resultado de la comparacion. True si existe diferencia
     */
    public static boolean updaterComparissonLogicPlugin(boolean permission1, String aToken, String bToken, String cToken, String remoteRegisterApiUrl, String pathToPluginRegisterFile){
        try{
            //Se crea registro de los archivos remotos en el repositorio correspondiente
            updaterRemoteFilesProcess.getRemoteFiles(aToken, bToken, cToken, remoteRegisterApiUrl);

            //Se comparan los registros generados de manera interna para verificar por segunda vez si corresponde actualizar (true == existen diferencias)
            //Este metodo ocupa el mismo metodod para actualizar mo, pero en modo plugin, esto es, que no borrara el registro local, dado que en este caso, el registro
            //no es creado por la app, si no que es recogido desde la carpeta ups, y logicamente, no queremos borrarlo, a deferencia del otro.
            boolean answer = updaterRegisterComparison.differencesInRegisters(Paths.get(pathToPluginRegisterFile), Paths.get("./RemoteRegister.txt"), "plugin");
            System.out.println("(updaterPluginUpdater.java) - Procesados los registros en arreglos! y las versiones\n--- El permiso por comparar versiones es: " + String.valueOf(permission1) + "\n--- Y las diferencias en los registros son: " + String.valueOf(answer));
            
            //Si updater genero booleans que no permiten la actualizacion, se borra el registros generados internamente
            updaterRegisterComparison.deleteFilesIfNotPermission(permission1, answer, Paths.get("./RemoteRegister.txt") );

            return answer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("(updaterLogic.java) - Respuesta defecto: false");
        return false;

    }

    /**
     * Metodo que se encarga de mover un archivo a traves de Files.move()
     * @param origin String con la ruta relativa de origen
     * @param target String con la ruta relativa objetivo
     */
    public static void moveJarToPluginsFolder(String origin, String target){
        Path originPath = Paths.get(origin);
        Path targetPath = Paths.get(target);
        try {
            Files.move(originPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("(updaterPluginsUpdating.java) - archivo Jar movido desde " + originPath + " a " + targetPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * bucle que se encarga de manejar la logica de comparacion entre versiones y registros. A traves de un loop que recorre lo capturado en el archivo plugins.up, se toman 
     * las lineas que contienen el nombre del plugin a actualizar junto con el archivo .up que tiene las variables globales para que updater trabaje. Si existe necesidad de
     * actualizar un plugin, se retornara un true para interrumpir el bucle para proceder con la actualizacion, caso contrario, se seguira con otra linea hasta un false por defecto
     * @return Boolean, true si existe necesidad de actualizar un plugin
     * @throws IOException
     */
    public static boolean loopRevisorPluginsToUpdate() throws IOException{
        if (updaterPluginsUpdating.upFile.size() != 0) {
            int sizeUpFiles = updaterPluginsUpdating.upFile.size();
            while (sizeUpFiles > 0) {
                String upFile = updaterPluginsUpdating.upFile.get(0);
                String pluginName = updaterPluginsUpdating.pluginName.get(0);
                if (sizeUpFiles >= 1) {
                    updaterPluginsUpdating.upFile.remove(0);
                    updaterPluginsUpdating.pluginName.remove(0);
                    sizeUpFiles -= 1;
                }
                //Actualizacion de las variables globales
                //Se obtiene el primer string de upFile, que es un String con la direccion relativa de un .up de la carpeta /ups
                ///Con esta ruta, se trabaja el archivo del cual se obtienen las nuevas variables 
                ////con estas variables se hace set de las variables globales del updater 
                if (!updaterArguments.setArguments(updaterArguments.saveArguments(upFile, "pluginRevision"))){
                    System.err.println("(UpdatingPlugins.java) - Variables globales NO actualizadas con el archivo: " + upFile + " pertenecientes al plugin " + pluginName);
                } else {
                    System.out.println("(UpdatingPlugins.java) - Variables globales actualizadas con el archivo: " + upFile + " pertenecientes al plugin " + pluginName);

                    //aprovechando que la vista de pdating tiene la barra de progreso en marcha, se revisara a traves de las variables obtenidas que sus versiones y registros
                    //remotos vs locales permitan hacer una actualizacion
                    boolean permission = updaterLogic.updaterpermissionsLogic(updaterArguments.getLocalVersionString(), updaterArguments.getAToken(),
                        updaterArguments.getBToken(), updaterArguments.getCToken(), updaterArguments.getRemoteVersionApiUrl());
                    boolean answer = updaterComparissonLogicPlugin(permission, updaterArguments.getAToken(), updaterArguments.getBToken(), updaterArguments.getCToken(), 
                        updaterArguments.getRemoteRegisterApiUrl(), updaterArguments.getPathToPluginRegisterFile());
                    if (permission && answer) {
                        return true;
                    }

                }
                
            }
            //si ninguno de los plugins cumple con las condiciones actualizarse, se arrojara un false  
            return false;
            //esto confirma la inexistencia de archivos .up a revisar              
        } else {
            return false;
        }
    }
     
    
}
