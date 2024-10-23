package mo.updating.controllers;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mo.updating.updater;
import mo.updating.updaterArguments;
import mo.updating.updaterLogic;
import mo.updating.updaterPluginsUpdating;

/**
 * Controlador del splasher inicial del Launcher
 */
public class SplashScreenController {

    // Lógica y métodos para el SplashScreen
    @FXML
    public void initialize() {
        System.out.println("(SplashScreenController.java) - Inicializando Vista Splasher");
        //boolean permission1 = updaterLogic.updaterComparissonLogic();
        
    }

    //Variables boolean que se obtienen de la logica de comparacion de registros y n° version
    private static Boolean permission1Obtained;
    private static Boolean answerObtained;

    /**
     * Getter del boolean obtenido de la comparacion entre strings de versiones
     * @return true si se debe hacer actualizacion. En este contexto, este boolean servira para indicar
     * el valor obtenido a la vista de updating
     */
    public static Boolean getPermission1Obtained(){
        return permission1Obtained;
    }

    /**
     * Getter del boolean obtenido de la comparacion entre registros de archivos
     * @return true si se debe hacer actualizacion. En este contexto, este boolean servira para indicar
     * el valor obtenido a la vista de updating
     */
    public static Boolean getAnswerObtained(){
        return answerObtained;
    }

    /**
     * Metodo de controlador que permite la ejecucion de fondo de los metodos logicos que permiten comparar los registros de versiones locales y remotos
     * ademas de lso archivos, ganando los boolean necesarios para ejecutar la actualizacion si correspondiese
     * @param stage
     */
    public void getFirstsPermission(Stage stage, boolean argumentsPermission){
        stage.setOnShown((WindowEvent event) -> {
                //Luego asegurandonos que se muestre la escena y los nodos
                Platform.runLater(() -> {

                    if (!argumentsPermission) {
                        System.out.println("(SplashScreenController.java) - Argumentos iniciales no permiten actualizar - Se revisan ahora los .up");
                        try {
                            if(updaterPluginsUpdating.loopRevisorPluginsToUpdate()){
                                //Se debe actualizar, se cierra el splasher y se accede a la vista de confirmacion de plugin
                                stage.close();
                                //si y solo si hay archivos up disponibles, se carga la vista de confirmationPlugin
                                loadConfirmationPluginView();
                            } else {
                                //Se debe simplemente abrir MO
                                updater.openMO();
                                //y Se cierra el launcher
                                stage.close();
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {

                        //Se obtienen booleans a traves por comparaciones entre los numeros de versiones local/remoto
                        permission1Obtained = updaterLogic.updaterpermissionsLogic(updaterArguments.getLocalVersionString(), updaterArguments.getAToken(), updaterArguments.getBToken(), 
                        updaterArguments.getCToken(), updaterArguments.getRemoteVersionApiUrl());
                        
                        //Se obtiene el segundo boolean solo si el anterior fue true
                        answerObtained = updaterLogic.updaterComparissonLogic(permission1Obtained, updaterArguments.getStartDirRegister(), updaterArguments.getAToken(), updaterArguments.getBToken(),
                            updaterArguments.getCToken(), updaterArguments.getRemoteRegisterApiUrl());
                              
                        /*Ahora, dependiendo de los 2 valores anteriores, se decide si se lanza la vista de 
                        * confirmacion, o se pasa de largo y continuamos con la apertura sencilla de MO
                        */
                        if(permission1Obtained == true && answerObtained == true){
                            try {
                                System.out.println("(SplashScreenController.java) - Inciando proceso de Updating - permisos en true");
                                //Se debe actualizar, se cierra el splasher y se accede a la vista de confirmacion
                                stage.close();
                                // Se carga FXML con vista de confirmacion
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/src/main/java/mo/updating/visual/Confirmacion.fxml"));
                                Parent root = loader.load();

                                // Se configura la nueva escena
                                Scene scene = new Scene(root);

                                // Se crea un nuevo Stage para la segunda vista
                                Stage newStage = new Stage();
                                newStage.setScene(scene);
                                newStage.setTitle("Nuevas Caracteristicas encontradas");
                                newStage.setResizable(false);

                                // Mostrar la nueva vista
                                newStage.show();    
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            
                        } else {
                            System.out.println("(SplashScreenController.java) - Los permisos no permiten actualizar - Se revisan ahora los .up");
                            try {
                                if(updaterPluginsUpdating.loopRevisorPluginsToUpdate()){
                                    //Se debe actualizar, se cierra el splasher y se accede a la vista de confirmacion de plugin
                                    stage.close();
                                    //si y solo si hay archivos up disponibles, se carga la vista de confirmationPlugin
                                    loadConfirmationPluginView();
                                } else {
                                    //Se debe simplemente abrir MO
                                    updater.openMO();
                                    //y Se cierra el launcher
                                    stage.close();
                                }
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            
                        }
                    }

                    
                });
            });
        
    }

    /**
     * Metodo que se encarga de cargar la vista de confirmacion para plugin
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

}
