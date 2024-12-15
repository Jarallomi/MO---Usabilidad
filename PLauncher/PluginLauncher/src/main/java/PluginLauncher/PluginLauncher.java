package PluginLauncher;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mo.capture.CaptureProvider;
import mo.visualization.VisualizationProvider;
import mo.organization.ProjectOrganization;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PluginLauncher extends Application {

    private TabPane tabPane;
    private List<CaptureProvider> captureProviders = new ArrayList<>();
    private List<Object> visualizationProviders = new ArrayList<>();
    private ProjectOrganization organization;
    private CaptureStage capturePlugin;

    @Override
    public void start(Stage primaryStage) {
        // Asegurar que el directorio "DefaultProject" existe
        String projectPath = "build/libs/DefaultProject";
        File projectDirectory = new File(projectPath);
        if (!projectDirectory.exists()) {
            projectDirectory.mkdirs();
        }
        organization = new ProjectOrganization(projectPath);

        // Crear la ventana principal
        primaryStage.setTitle("Data Capture and Visualization");

        // Crear el contenedor raíz de la interfaz
        BorderPane root = new BorderPane();

        // Crear la barra de menú
        MenuBar menuBar = createMenuBar(primaryStage);
        root.setTop(menuBar);

        // Inicializar el TabPane
        tabPane = new TabPane();
        root.setCenter(tabPane);

        // Crear instancia de CaptureStage
        capturePlugin = new CaptureStage(captureProviders, organization);
        capturePlugin.setTabPane(tabPane);
        capturePlugin.createCapturePane();

        // Crear instancia de VisualizationStage
        VisualizationStage visualizationStage = new VisualizationStage(visualizationProviders, organization);
        visualizationStage.setTabPane(tabPane);
        visualizationStage.createVisualizationPane();

        // Cargar plugins y refrescar las pestañas
        loadPlugins(visualizationStage);

        // Configurar y mostrar la escena principal
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem loadPluginsItem = new MenuItem("Load Plugins");
        loadPluginsItem.setOnAction(e -> {
            VisualizationStage visualizationStage = new VisualizationStage(visualizationProviders, organization);
            visualizationStage.setTabPane(tabPane);
            loadPlugins(visualizationStage);
        });
        fileMenu.getItems().add(loadPluginsItem);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu);

        return menuBar;
    }

    private void loadPlugins(VisualizationStage visualizationStage) {
        File pluginDir = new File("build/libs/plugins");
        if (!pluginDir.exists()) {
            pluginDir.mkdirs();
        }

        PluginLoader pluginLoader = new PluginLoader(pluginDir);
        List<Object> plugins = pluginLoader.loadPlugins();

        captureProviders.clear();
        visualizationProviders.clear();

        // Procesar los plugins cargados
        for (Object plugin : plugins) {
            if (plugin instanceof CaptureProvider) {
                captureProviders.add((CaptureProvider) plugin);
                System.out.println("Capture plugin loaded: " + plugin.getClass().getName());
            } else if (plugin instanceof VisualizationProvider) {
                visualizationProviders.add(plugin); // Añadir a visualizationProviders como Object
                System.out.println("Visualization plugin loaded: " + plugin.getClass().getName());
            } else {
                // Los plugins que no son ni de captura ni de visualización ya han sido agregados al classpath
                System.out.println("Other plugin added to classpath: " + plugin.getClass().getName());
            }
        }

        capturePlugin.refreshCapturePane();
        visualizationStage.refreshVisualizationPane(visualizationProviders); // Pasar como List<Object>
    }

    private void showAboutDialog() {
        Alert aboutAlert = new Alert(Alert.AlertType.INFORMATION);
        aboutAlert.setTitle("About");
        aboutAlert.setHeaderText("Data Capture and Visualization");
        aboutAlert.setContentText("Application for managing capture and visualization plugins.");
        aboutAlert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
