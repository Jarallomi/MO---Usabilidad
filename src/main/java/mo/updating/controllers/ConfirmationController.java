package mo.updating.controllers;

import java.io.IOException;
import java.nio.file.Paths;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import mo.updating.updater;
import mo.updating.updaterArguments;
import mo.updating.updaterPluginsUpdating;
import mo.updating.updaterRegisterComparison;
import mo.updating.updaterVersionNotesRegister;

/**
 * Controlador del splasher inicial del Launcher
 */
public class ConfirmationController {

    @FXML
    private Button noButton;

    @FXML
    private Button yesButton;

    @FXML
    private TextArea newNotesVersion;

    @FXML
    private Button skipButton;

    @FXML
    private Button updateAll;

    // Lógica de inicialiacion de la vista de confirmacion
    @FXML
    private void initialize() throws IOException {
        System.out.println("(ConfirmationController.java) - Inicializando Vista de confirmacion");
        // Establecer el TextArea newNotesVersion como de solo lectura
        String notes = "";
        try {
            //Se obtienen las notas de version remotas y se muestran en la vista
            //notes = updaterVersionNotesRegister.getRemoteVersionNotes("ghp_0D6Zmt", "4sfGEZJzK7Fiutyfj6J", 
            //"DizVO3CK3zW", "https://raw.githubusercontent.com/NaNoAAD/MO-Autoupdater/master/versionNotes.txt");
            notes = updaterVersionNotesRegister.getRemoteVersionNotes(updaterArguments.getAToken(), updaterArguments.getBToken(), updaterArguments.getCToken(),
                 updaterArguments.getRemoteNotesApiUrl());
            //Se solocan las notas obtenidas en la vista
            newNotesVersion.setText(notes);
            newNotesVersion.setEditable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * Metodo del controlador que permite el cerrado del launcher y la apertura de MO en caso de que el usuario seleccione no actualizar
     * @param event
     */
    @FXML
    private void cancelUpdate(ActionEvent event) throws IOException {
        System.out.println("(ConfirmationController.java) - Eliminando Archivo remoto obtenido y cerrando app");
        updaterRegisterComparison.deleteFilesIfNotPermission(SplashScreenController.getPermission1Obtained(), SplashScreenController.getAnswerObtained(), Paths.get("./RemoteRegister.txt") );
        if(updaterPluginsUpdating.loopRevisorPluginsToUpdate()){
            Stage stage = (Stage) this.noButton.getScene().getWindow();
            stage.close();
            //si y solo si hay archivos up disponibles, se carga la vista de confirmationPlugin
            loadConfirmationPluginView();
        } else {
            Stage stage = (Stage) this.noButton.getScene().getWindow();
            stage.close();
            updater.openMO();
        }
        
    }

    /**
     * Metodo que abre la vista que muestra el progreso de la actualizacion de MO
     * @param event
     */
    @FXML
    private void acceptUpdate(ActionEvent event){
        System.out.println("(ConfirmationController.java) - Abriendo vista Updating");
        Stage stage = (Stage) this.yesButton.getScene().getWindow();
        stage.close();
        //Se hace apertura de la vista de actualizacion en progreso
        try {
            // Se carga FXML con vista de confirmacion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/src/main/java/mo/updating/visual/Updating.fxml"));
            Parent root = loader.load();
            //Se carga nuevo controlador (Si es necesario algun procedimiento a priori)
            //ConfirmationController controller = loader.getController();

            // Se configura la nueva escena
            Scene scene = new Scene(root);

            // Se crea un nuevo Stage para la segunda vista
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Actualizando Multimodal Observer");
            newStage.setResizable(false);

            // Mostrar la nueva vista
            newStage.show();    

            //Con los recursos listos y mostrados, al igual que en SplashController se ejecuta metodo
            //controller.secondPlaneUpdating(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateAll(ActionEvent event){
        System.out.println("(ConfirmationController.java) - Abriendo vista Updating");
        Stage stage = (Stage) this.updateAll.getScene().getWindow();
        stage.close();
        //Se coloca la variable global a true, para ser usada con posterioridad
        updater.yesToAll = true;
        //Se hace apertura de la vista de actualizacion en progreso
        try {
            // Se carga FXML con vista de confirmacion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/src/main/java/mo/updating/visual/Updating.fxml"));
            Parent root = loader.load();
            //Se carga nuevo controlador (Si es necesario algun procedimiento a priori)
            //ConfirmationController controller = loader.getController();

            // Se configura la nueva escena
            Scene scene = new Scene(root);

            // Se crea un nuevo Stage para la segunda vista
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Actualizando " + updaterArguments.getBuildedJarFileName());
            newStage.setResizable(false);

            // Mostrar la nueva vista
            newStage.show();    

            //Con los recursos listos y mostrados, al igual que en SplashController se ejecuta metodo
            //controller.secondPlaneUpdating(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     * Metodo que permite omitir todas las actualizaciones venideras
     * @param event
     */
    @FXML
    private void skipUpdates(ActionEvent event){
        System.out.println("(ConfirmationController.java) - Omitiendo actualizaciones - Abriendo vista Updating");
        Stage stage = (Stage) this.skipButton.getScene().getWindow();
        stage.close();
        try {
            updater.openMO();
            System.exit(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
