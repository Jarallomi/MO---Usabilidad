package mo.updating.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import mo.updating.redirectText;
import mo.updating.updater;
import mo.updating.updaterArguments;
import mo.updating.updaterLogic;
import mo.updating.updaterPluginsUpdating;

/**
 * Controlador de la vista de proceso de actualizacion
 */
public class UpdatingController{

    Boolean updatePluginFlag;

    @FXML
    private TextArea statusText;

    @FXML
    private void initialize() throws IOException{
        // Redireccion de System.out a TextArea
        System.setOut(new java.io.PrintStream(new redirectText(statusText), true));
        // Redireccion de System.err a TextArea
        System.setErr(new java.io.PrintStream(new redirectText(statusText), true));
        
        System.out.println("(UpdatingController.java) - Inicializando Vista de Actualizacion en progreso");
        //Ejecutamos las operaciones logicas de actualizacion en un hilo de fondo
        //Para evitar que bloqueen la responsividad de la animcacion de la barra de progreso indeterminada
        Task<Void> updateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception{
            Boolean permissionBoolean = SplashScreenController.getPermission1Obtained();
            Boolean answerBoolean = SplashScreenController.getAnswerObtained();
            
            //Usamos la logica responsable de actualizacion
            updaterLogic.updaterUpdatingLogic(permissionBoolean, answerBoolean, updaterArguments.getDownloadLinkZip(), updaterArguments.getTargetDirectoryToMoveZip(), 
            updaterArguments.getZipDownloadedPath(), updaterArguments.getTargetDirectoryToExtract(), updaterArguments.getPathToExecuteWrapperGradle());
            if(updaterPluginsUpdating.loopRevisorPluginsToUpdate()){
                //si y solo si hay archivos up disponibles, se carga la vista de confirmationPlugin
                
                updatePluginFlag = true;
                //Aseguramos una pausa para evitar inconsistencias entre la ejecucion de los threads (tasks)
                Thread.sleep(2000);
                
            } else {
                updatePluginFlag = false;
                //Si simplemente no habian archivos .up identificados, simplemente procedemos a cerrar el updater
                System.out.println("(UpdatingController.java) - Abriendo MO - Terminando Launcher ");
                }
            return null;
            }
        };

        new Thread(updateTask).start();

        updateTask.setOnSucceeded(event -> {
            //Si existen plugins por actualizar y el usuario de primeras apreto en actualizar todo
            if (updatePluginFlag && updater.yesToAll) {
                System.out.println("(UpdatingController.java) - Actualizando todo");
                Platform.runLater(() -> {
                    closeStage();
                    //Se abre de manera directa la vista de updating de los plugins (Cual sera es decidido por las variables globales de los archivos .up)
                    loadUpdatingPluginView();
                }); 
            } else if (updatePluginFlag && updater.yesToAll == false) {
                     //Si se detectaron plugins por actualizar, cerramos la vista y procedemos a cargar la vista de confirmacion plugin
                    System.out.println("(UpdatingController.java) - Actualización plugin disponible ");
                    Platform.runLater(() -> {
                        closeStage();
                        loadConfirmationPluginView();
                    }); 
                                
            } else {
                //Caso contrario, simplemente cerramos el launcher y abrimos MO
                System.out.println("(UpdatingController.java) - Actualización plugin no disponible ");
                Platform.runLater(() -> closeStage());
                try {
                    updater.openMO();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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
     * Metodo que abre la vista de confirmacion de actualizaicon de plugin
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
            newStage.setTitle("Actualizacion para Plugin encontrada");
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
