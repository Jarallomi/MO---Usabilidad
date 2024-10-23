package mo.updating;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mo.updating.controllers.SplashScreenController;
import mo.updating.controllers.errorController;

/**
 * Clase principal encarga de iniciar el launcher, la logica y todo los demas procesos
 * Contiene el metodo main()
 */
public class updater extends Application {

    //Variables
    public boolean permissionByVersions; //Si las versiones tienen diferencia
    public boolean pluginPermission; //Si los plugins tienen necesidad de actualizarse
    public boolean detectedEnviromentVariable; //Si las variables globables fueron detectadas en su archivo
    public boolean argumentsPermission; //Para permtir permiso para actualizar
    public static boolean yesToAll = false; //Para ejecutar todas las actualizaciones sin preguntar una por una (Variable global)
    

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        // Se inicia la primera vista (Splasher)
        String currentPath = System.getProperty("user.dir").replace("\\", "/");
        System.out.println("UPDATER INICIADO DESDE: " + currentPath + "\n");

        //Se detecta la variable de entorno necesaria para gradle wrapper
        detectEnviromentVariableAndArgsUp();

        //Se detecta la presencia del ejecutable de MO
        detectExecutable();

        //Se cargan y setean los argumentos globales desde el archivo args.up para trabajar MO
        argumentsPermission = updaterArguments.setArguments(updaterArguments.saveArguments("./args.up", "MO"));

        //Se carga el archivo que contiene los plugins integrados a ser revisados en la carpeta .ups
        //y se guardan en la variable estatica para ser usado en los procesos posteriores a actualizar mo
        ///Este archivo a capturar tiene en cada linea <nombre_del_plugin>: <string ruta a argumentos de trabajo de plugin>
        updaterPluginsUpdating.upFile.addAll(updaterPluginsUpdating.getUpFilePluginsToUpdate("./plugins.up"));

        //Comprobamos si existe conexion a internet
        if (internetConnectionChecker()) {
            //Con todo lo anterior listo, se inicia el splasher de MO y las logicas posteriores
            loadSplashView("/src/main/java/mo/updating/visual/SplasherPrincipal.fxml", "Bienvenido a Multimodal Observer", primaryStage, argumentsPermission);
        } else {
            //si no existe conexion, se cierra el launcher
            loadErrorView();
        }

        
    }

    /**
     * Metodo que carga la vista Spalsh
     * @param fxml Archivo que contiene la informacion descriptiva de la vista
     * @param title String que pone titulo a la ventana que se presenta
     * @param stage Stage necesaria para proseguir con el funcionamiento de JavaFX
     */
    private void loadSplashView(String fxml, String title, Stage stage, boolean argumentsPermission) throws Exception{
        try {
            System.out.println("(updater.java) - Se carga vista de Splasher" + getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
            //Se carga el archivo de vista
            URL fxmlURL = getClass().getResource(fxml);
             System.out.println(fxml);
            FXMLLoader loader = new FXMLLoader(fxmlURL);

            Parent root = loader.load();
            Scene scene = new Scene(root);

            //Se instancia controller para poder usar el metodo getFirstsPermission que esta en controlador splasher
            SplashScreenController controller = loader.getController();

            //con la vista ya mostrada, se procede a usar la logica de las primeras comparaciones
            //Primero asegurandonos que se muestre la ventana, la escena y los nodos en el siguiente metodo
            controller.getFirstsPermission(stage, argumentsPermission);

            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();     

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Metodo que ejecuta Multimodal Observer a traves de su .jar. Si no lo encuentra, lanza una pantalla de error
     * @throws IOException
     */
    public static void openMO() throws IOException{
        try {
            System.out.println("(updater.java) -Apertura de MO - Finalizando Launcher");
            // Iniciar MO
            //Es importante que este comando tenga el nombre el .jar de MO (verificar el uso de contains)
            //Runtime.getRuntime().exec("java -jar ./build/libs/multimodal-observer-server-5-0.0.0.jar");

            String MOLocation = "./build/libs/multimodal-observer-server-5-0.0.0.jar";
            Path MOPath = Paths.get(MOLocation);

            if (Files.exists(MOPath) && Files.isRegularFile(MOPath)) {
                //El ejecutable de MO existe, se ejecuta
                ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "./build/libs/multimodal-observer-server-5-0.0.0.jar");
                processBuilder.start();
                //Se termina el launcher
                System.out.println("MO ejecutado correctamente");
                System.exit(0);
            } else {
                //Si el ejecutable de MO no existe y se requirio su apertura, entonces se informa el error
                System.err.println("Ejecutable de MO no encontrado");
                //Se llama a la pantalla de error
                updater object = new updater();
                Stage stage = new Stage();
                //Se configura como MODAL para evitar que se interactue con otras ventanas
                stage.initModality(Modality.APPLICATION_MODAL);
                FXMLLoader loader = new FXMLLoader(object.getClass().getResource("/src/main/java/mo/updating/visual/error.fxml"));
                AnchorPane root = loader.load();
                Scene scene = new Scene(root);
                errorController controller = loader.getController();
                controller.setTextInScreen(5, "Multimodal Observer");
                stage.setScene(scene);
                stage.showAndWait();
                //Se cierra el launcher con codigo de error 1
                System.exit(1);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

            /**

            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "./build/libs/multimodal-observer-server-5-0.0.0.jar");
            Process process = processBuilder.start();
            //SE almacena el codigo de salida
            int exitCode = process.waitFor();

            // Se verifica que el proceso se ejecutó correctamente
            if (exitCode == 0) {
                // El proceso se ejecutó sin problemas, se abre MO y se cierra el launcher
                System.out.println("Proceso ejecutado correctamente");
                System.exit(0);
            } else {
                // El proceso falló
                System.err.println("Ejecutable de MO no encontrado");
                //Se llama a la pantalla de error
                updater object = new updater();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                FXMLLoader loader = new FXMLLoader(object.getClass().getResource("/src/main/java/mo/updating/visual/error.fxml"));
                AnchorPane root = loader.load();
                Scene scene = new Scene(root);
                errorController controller = loader.getController();
                controller.setTextInScreen(5, "Multimodal Observer");
                stage.setScene(scene);
                stage.showAndWait();
                System.exit(1);
                }
            
        } catch (Exception e) {
            //Si no se encuntra el ejecutable de Multimodal Observer se configura y llama a la vista de error
            e.printStackTrace();
            System.err.println("Ejecutable de MO no encontrado");
            //Se llama a la pantalla de error
            updater object = new updater();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader loader = new FXMLLoader(object.getClass().getResource("/src/main/java/mo/updating/visual/error.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            errorController controller = loader.getController();
            controller.setTextInScreen(5, "Multimodal Observer");
            stage.setScene(scene);
            stage.showAndWait();
            //Se abre Multimodal Observer
            System.exit(1);
        }
        */
    }

    /**
     * Metodo que detecta la existencia de la variable de entorno JAVA_HOME en el sistema de la computadora 
     * Este metodo hace aparecer una pantalla de error.
     * @return false si no encuentra la variable en el sistema, true si la encuentra
     */
    public void detectEnviromentVariableAndArgsUp() throws IOException{
        File file = new File("args.up");
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome == null) {
            System.err.println("Variable JAVA HOME no detectada - Se aborta la revisiones - Se inicia MO");
            //Se llama a la pantalla de error
            updater object = new updater();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader loader = new FXMLLoader(object.getClass().getResource("/src/main/java/mo/updating/visual/error.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            errorController controller = loader.getController();
            controller.setTextInScreen(3, "JAVA_HOME");
            stage.setScene(scene);
            stage.showAndWait();
            //Se abre Multimodal Observer
            updater.openMO();
            System.exit(1);
        } else if (!file.exists()) {
            System.err.println("Args.up no detectado - Se aborta la revisiones - Se inicia MO");
            //Se llama a la pantalla de error
            updater object = new updater();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader loader = new FXMLLoader(object.getClass().getResource("/src/main/java/mo/updating/visual/error.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            errorController controller = loader.getController();
            controller.setTextInScreen(4, "args.up");
            stage.setScene(scene);
            stage.showAndWait();
            //Se abre Multimodal Observer
            updater.openMO();
            System.exit(1);
        }
    }

    public boolean internetConnectionChecker(){
        try {
            // Comprobamos una direccion sencilla
            InetAddress.getByName("www.google.com").isReachable(5000); // 5000 ms de tiempo de espera (5 segundos)
            return true;
        } catch (UnknownHostException e) {
            // No se pudo resolver el nombre de dominio
            return false;
        } catch (Exception e) {
            // Otras excepciones
            return false;
        }
    }

    public void loadErrorView() throws IOException {
        System.err.println("Args.up no detectado - Se aborta la revisiones - Se inicia MO");
        //Se llama a la pantalla de error
        updater object = new updater();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader = new FXMLLoader(object.getClass().getResource("/src/main/java/mo/updating/visual/error.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);
        errorController controller = loader.getController();
        controller.setTextInScreen(6, "Launcher");
        stage.setScene(scene);
        stage.showAndWait();
        //Se abre Multimodal Observer
        updater.openMO();
        System.exit(1);
    }

    public void detectExecutable() throws IOException{
        String filePath = "./build/libs/multimodal-observer-server-5-0.0.0.jar";
        Path path = Paths.get(filePath);
        if (Files.exists(path) && Files.isRegularFile(path)) {
            return;            
        } else {
            //Se llama a la pantalla de error
            updater object = new updater();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader loader = new FXMLLoader(object.getClass().getResource("/src/main/java/mo/updating/visual/error.fxml"));
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            errorController controller = loader.getController();
            controller.setTextInScreen(5, "Multimodal Observer");
            stage.setScene(scene);
            stage.showAndWait();
            //Se abre Multimodal Observer
            System.exit(1);
        }
    }
    

}