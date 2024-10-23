package mo.updating.controllers;
import java.io.IOException;
import java.nio.file.Paths;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mo.updating.updater;
import mo.updating.updaterRegisterComparison;

    

public class errorController {

    @FXML
    private Text errorText;

    @FXML
    private Button OKbutton;

    @FXML
    public void initialize() throws IOException{        
    }

    @FXML
    private void errorAccepted(ActionEvent event) throws IOException{
        Stage stage = (Stage) this.OKbutton.getScene().getWindow();

        updaterRegisterComparison.deleteFilesIfNotPermission(false, false, Paths.get("./RemoteRegister.txt") );
        
        stage.close();
        //updater.openMO();
        //Se cierra de manera segura
        //Platform.exit();
        
    }

    /**
     * Metodo que setea el texto en la pantalla de error
     * @param error
     * @param fileName
     */
    public void setTextInScreen(int error, String fileName){
        if (error == 1) {
            errorText.setText("Formato de Args.up corrupto: " + fileName);
        } else if (error == 2) {
            errorText.setText("Archivo .up de plugin corrupto: " + fileName);
        } else if (error == 3) {
            errorText.setText("Variable de entorno no definida: " + fileName);
        } else if (error == 4){
            errorText.setText("Inexistencia de archivo: " + fileName);
        } else if (error == 5){
            errorText.setText("No se encuentra el ejecutable: " + fileName);
        } else if (error == 6){
            errorText.setText("Sin Conexion a internet");
        } else {
            errorText.setText("Error");
        }
        // Utiliza el valor proporcionado para establecer el texto
    }

}

