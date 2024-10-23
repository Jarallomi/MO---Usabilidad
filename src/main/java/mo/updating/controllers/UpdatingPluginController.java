package mo.updating.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import mo.updating.redirectText;
import mo.updating.updater;
import mo.updating.updaterArguments;
import mo.updating.updaterLogic;
import mo.updating.updaterPluginsUpdating;
import mo.updating.updaterPostUpdateProcesses;

/**
 * Controlador de la vista de proceso de actualizacion
 */
public class UpdatingPluginController {

    @FXML
    private TextArea statusText;

    @FXML
    private void initialize() throws IOException{
        // Redireccion de System.out a TextArea
        System.setOut(new java.io.PrintStream(new redirectText(statusText), true));
        // Redireccion de System.err a TextArea
        System.setErr(new java.io.PrintStream(new redirectText(statusText), true));
        
        System.out.println("(UpdatingPluginController.java) - Inicializando Vista de Actualizacion en progreso");
        //Ejecutamos las operaciones logicas de actualizacion en un hilo de fondo
        //Para evitar que bloqueen la responsividad de la animcacion de la barra de progreso indeterminada


        Task<Void> updateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception{
                        
            //Usamos la logica responsable de actualizacion
            updaterLogic.updaterUpdatingPluginLogic(true, true, updaterArguments.getDownloadLinkZip(), updaterArguments.getTargetDirectoryToMoveZip(), 
            updaterArguments.getZipDownloadedPath(), updaterArguments.getTargetDirectoryToExtract(), updaterArguments.getPathToExecuteWrapperGradle());
            try {
                //Con el plugin actualizado, se intenta mover el jar generado a la carpeta de plugins de MO y a eliminar la carpeta extraida

                updaterPluginsUpdating.moveJarToPluginsFolder("./ups/" + updaterArguments.getJarLocationInRepository() + "/" + updaterArguments.getBuildedJarFileName(), "./build/libs/plugins/" + updaterArguments.getBuildedJarFileName());

                //Se actualiza desde lo descargado, el registro local y el indicador de version local
                updaterPostUpdateProcesses.updatingLocalRegisterAndLocalVersionPlugin(updaterArguments.getPathToExecuteWrapperGradle(), updaterArguments.getPathToPluginRegisterFile(), updaterArguments.getLocalVersionString());

                //Se borran los archivos sobrantes
                updaterPostUpdateProcesses.deleteLeftoversFilesPlugin(updaterArguments.getRepositoryName());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            return null;
            }
        };

        new Thread(updateTask).start();

        updateTask.setOnSucceeded(event -> {
            //Una vez que la actualizacion de mo termino, si hay upfiles que revisar, entonces es el turno de los plugins
            //Y para ello se vuelve a abrir la vista de confirmacion pero antes! se actualizan las variables globales del updater
            try {
                //CASO 1: el usuario anteriormente decidio actualziar todo
                if (updater.yesToAll) {
                    if(updaterPluginsUpdating.loopRevisorPluginsToUpdate()){
                        //Se cierra la vista de la barra de progeso para dar paso a la siguiente vista de confirmacion
    
                        //si y solo si hay archivos up disponibles, se carga la vista de confirmationPlugin
                        Platform.runLater(() -> {
                            closeStage();
                            loadUpdatingPluginView();
                        });
                        
    
                        // Cierra la vista en el hilo de JavaFX y se cierra la app
                        //Platform.runLater(() -> closeStage());
    
                    } else {
                        //Si simplemente no habian archivos .up identificados, simplemente procedemos a cerrar el updater
                        System.out.println("(UpdatingController.java) - Abriendo MO - Terminando Launcher ");
                        // Cierra la vista en el hilo de JavaFX y se cierra la app
                        closeStage();
                        // Se abre MO
                        updater.openMO();
                        
                    }
                    
                //CASO 2: Si el usuario no ha decidido actualizar todo    
                } else {
                    if(updaterPluginsUpdating.loopRevisorPluginsToUpdate()){
                        //Se cierra la vista de la barra de progeso para dar paso a la siguiente vista de confirmacion
    
                        //si y solo si hay archivos up disponibles, se carga la vista de confirmationPlugin
                        Platform.runLater(() -> {
                            closeStage();
                            loadConfirmationPluginView();
                        });
                        
    
                        // Cierra la vista en el hilo de JavaFX y se cierra la app
                        //Platform.runLater(() -> closeStage());
    
                    } else {
                        //Si simplemente no habian archivos .up identificados, simplemente procedemos a cerrar el updater
                        System.out.println("(UpdatingController.java) - Abriendo MO - Terminando Launcher ");
                        // Cierra la vista en el hilo de JavaFX y se cierra la app
                        closeStage();
                        // Se abre MO
                        updater.openMO();
                        
                    }
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        });
        
    }


    /**
     * Metodo interno del conrolador que cierra la vista y tambien la app
     */
    private void closeStage() {
        // Obtén el Stage asociado a la vista a traves de la barra de progreso
        Stage stage = (Stage) statusText.getScene().getWindow();
        // Cerrado del Stage (y la vista)
        stage.close();
    }

    /**
     * Metodo que permite la apertura de la vista de confirmacion de actualizacion de los plugins
     */
    private void loadConfirmationPluginView(){
        try {
            // Se carga FXML con vista de confirmacion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/src/main/java/mo/updating/visual/ConfirmacionPlugin.fxml"));
            Parent root = loader.load();
            //Se carga nuevo controlador (Si es necesario algun procedimiento a priori)
            //ConfirmationController controller = loader.getController();

            // Se configura la nueva escena
            Scene scene = new Scene(root);

            // Se crea un nuevo Stage para la segunda vista
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Actualizacion para Plugin: " + updaterArguments.getBuildedJarFileName());
            newStage.setResizable(false);

            // Mostrar la nueva vista
            newStage.show();   

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo que abre la vista de actualizacion de plugin
     */
    private void loadUpdatingPluginView(){
        try {
            // Se carga FXML con vista de confirmacion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/src/main/java/mo/updating/visual/UpdatingPlugin.fxml"));
            Parent root = loader.load();
            //Se carga nuevo controlador (Si es necesario algun procedimiento a priori)
            //ConfirmationController controller = loader.getController();

            // Se configura la nueva escena
            Scene scene = new Scene(root);

            // Se crea un nuevo Stage para la segunda vista
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Actualizando Plugin " + updaterArguments.getBuildedJarFileName());
            newStage.setResizable(false);

            // Mostrar la nueva vista
            newStage.show();   

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}  //Fin controller
