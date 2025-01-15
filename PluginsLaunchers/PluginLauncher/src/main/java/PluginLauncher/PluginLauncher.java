package PluginLauncher;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import mo.capture.CaptureProvider;
import mo.visualization.VisualizationProvider;
import mo.analysis.AnalysisProvider;
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;

public class PluginLauncher extends Application {

    private TabPane tabPane;
    private ProjectOrganization organization;
    private Participant defaultParticipant;
    //private List<Participant> participants;
    private VBox captureContent, visualizationContent, analysisContent;
    private AnalysisPlatform analysisStages;

    @Override
    public void start(Stage primaryStage) {
        organization = new ProjectOrganization("DefaultProject");
        new File(organization.getLocation().toString()).mkdirs();

        // participants = new ArrayList<>();
        initializeDefaultParticipant();

        primaryStage.setTitle("MO - Plugin Launcher");
        tabPane = new TabPane();
        setupMainTabs();
        setupMenuBar(primaryStage);

        loadPlugins();

        BorderPane root = new BorderPane(tabPane);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            Platform.exit(); 
            System.exit(0);  
        });
    }

    private void initializeDefaultParticipant() {
        defaultParticipant = new Participant();
        defaultParticipant.id = "default-participant";
        defaultParticipant.folder = "participant-1";

        File participantFolder = new File(organization.getLocation(), defaultParticipant.folder);
        if (!participantFolder.exists()) {
            participantFolder.mkdirs();
        }

        new File(participantFolder, "capture").mkdirs();
        new File(participantFolder, "analysis").mkdirs();
        new File(participantFolder, "visualization").mkdirs();

    }

    private void setupMenuBar(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();

        /*Menu participantsMenu = new Menu("Participants");
        MenuItem addParticipantItem = new MenuItem("Add Participant");
        addParticipantItem.setOnAction(e -> addNewParticipant());

        participantsMenu.getItems().add(addParticipantItem);*/

        Menu analysisMenu = new Menu("Analysis");
        MenuItem manageAnalysisItem = new MenuItem("Administrar análisis");

        manageAnalysisItem.setOnAction(e -> {
            if (analysisStages != null) {
                List<AnalysisProvider> providers = analysisStages.getAnalysisProviders();
                if (providers.isEmpty()) {
                    showAlert("No hay análisis disponibles para administrar.");
                    return;
                }

                ChoiceDialog<AnalysisProvider> providerDialog = new ChoiceDialog<>(providers.get(0), providers);
                providerDialog.setTitle("Seleccionar proveedor de análisis");
                providerDialog.setHeaderText("Selecciona un proveedor para administrar configuraciones:");
                providerDialog.setContentText("Proveedores:");

                providerDialog.showAndWait().ifPresent(selectedProvider -> {
                    analysisStages.manageProviderAnalysis(selectedProvider);
                });
            } else {
                showAlert("No hay análisis disponibles para administrar.");
            }
        });

        analysisMenu.getItems().add(manageAnalysisItem);
        menuBar.getMenus().addAll(/*participantsMenu,*/ analysisMenu);

        BorderPane root = new BorderPane(tabPane);
        root.setTop(menuBar);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
    }

    /*private void addNewParticipant() {
        Participant newParticipant = new Participant();
        newParticipant.id = "participant-" + (participants.size() + 1);
        newParticipant.folder = "participant-" + (participants.size() + 1);

        File participantFolder = new File(organization.getLocation(), newParticipant.folder);
        if (!participantFolder.exists()) {
            participantFolder.mkdirs();
        }

        new File(participantFolder, "capture").mkdirs();
        new File(participantFolder, "analysis").mkdirs();
        new File(participantFolder, "visualization").mkdirs();

        participants.add(newParticipant); 
        showAlert("Nuevo participante añadido: " + newParticipant.id);
    }*/

    private void setupMainTabs() {
        ScrollPane captureScrollPane = createScrollablePane(captureContent = new VBox(10));
        ScrollPane visualizationScrollPane = createScrollablePane(visualizationContent = new VBox(10));
        ScrollPane analysisScrollPane = createScrollablePane(analysisContent = new VBox(10));

        tabPane.getTabs().addAll(
                createTab("Capture", captureScrollPane),
                createTab("Visualization", visualizationScrollPane),
                createTab("Analysis", analysisScrollPane)
        );
    }

    private ScrollPane createScrollablePane(VBox content) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true); 
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); 
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); 
        content.setPadding(new javafx.geometry.Insets(10)); 
        return scrollPane;
    }

    private Tab createTab(String title, ScrollPane scrollPane) {
        Tab tab = new Tab(title, scrollPane);
        tab.setClosable(false); 
        return tab;
    }


    private void loadPlugins() {
        PluginRegistry registry = PluginRegistry.getInstance();
        List<Plugin> plugins = getPluginsUsingReflection(registry);

        if (plugins == null || plugins.isEmpty()) {
            return;
        }

        loadCapturePlugins(plugins);
        loadVisualizationPlugins(plugins);
        loadAnalysisPlugins(plugins);
        addRemainingJarsToClasspath(plugins);
    }

    private void loadCapturePlugins(List<Plugin> plugins) {
        List<CaptureProvider> captureProviders = plugins.stream()
                .filter(plugin -> CaptureProvider.class.isAssignableFrom(plugin.getClazz()))
                .map(Plugin::getInstance)
                .map(instance -> (CaptureProvider) instance)
                .collect(Collectors.toList());

        if (!captureProviders.isEmpty()) {
            CapturePlatform captureStages = new CapturePlatform(captureProviders, organization, defaultParticipant);
            captureContent.getChildren().add(captureStages.createCapturePane());
            captureStages.refreshCapturePane();
        }
    }

    private void loadVisualizationPlugins(List<Plugin> plugins) {
        List<VisualizationProvider> visualizationProviders = plugins.stream()
                .filter(plugin -> VisualizationProvider.class.isAssignableFrom(plugin.getClazz()))
                .map(Plugin::getInstance)
                .map(instance -> (VisualizationProvider) instance)
                .collect(Collectors.toList());

        if (!visualizationProviders.isEmpty()) {
            VisualizationPlatform visualizationStages = new VisualizationPlatform(visualizationProviders, organization, defaultParticipant);
            visualizationContent.getChildren().add(visualizationStages.createVisualizationPane());
            visualizationStages.refreshVisualizationPane(visualizationProviders);
        }
    }

    private void loadAnalysisPlugins(List<Plugin> plugins) {
        List<AnalysisProvider> analysisProviders = plugins.stream()
                .filter(plugin -> AnalysisProvider.class.isAssignableFrom(plugin.getClazz()))
                .map(Plugin::getInstance)
                .map(instance -> (AnalysisProvider) instance)
                .collect(Collectors.toList());

        if (!analysisProviders.isEmpty()) {
            analysisStages = new AnalysisPlatform(analysisProviders, organization, defaultParticipant);
            VBox analysisPane = analysisStages.createAnalysisPane();
            analysisContent.getChildren().add(analysisPane);
            analysisStages.refreshAnalysisPane();
        }
    }

    private void addRemainingJarsToClasspath(List<Plugin> loadedPlugins) {
        try {
            File pluginsFolder = new File("plugins");

            if (!pluginsFolder.exists()) {
                return;
            }

            File[] jarFiles = pluginsFolder.listFiles((dir, name) -> name.endsWith(".jar"));
            if (jarFiles == null) {
                return;
            }

            for (File jar : jarFiles) {
                boolean isAlreadyLoaded = loadedPlugins.stream()
                        .anyMatch(plugin -> plugin.getPath().toString().contains(jar.getName()));

                if (!isAlreadyLoaded) {
                    addJarToClasspath(jar);
                }
            }
        } catch (Exception e) {
            System.err.println("Error trying to add JARs to classpath: " + e.getMessage());
        }
    }

    private void addJarToClasspath(File jarFile) throws Exception {
        URL jarUrl = jarFile.toURI().toURL();
        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        addUrlMethod.setAccessible(true);
        addUrlMethod.invoke(classLoader, jarUrl);
    }

    private List<Plugin> getPluginsUsingReflection(PluginRegistry registry) {
        try {
            Method getPluginDataMethod = registry.getClass().getDeclaredMethod("getPluginData");
            Object pluginData = getPluginDataMethod.invoke(registry);

            Field pluginsField = pluginData.getClass().getDeclaredField("plugins");
            pluginsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<Plugin> plugins = (List<Plugin>) pluginsField.get(pluginData);
            return plugins;
        } catch (Exception e) {
            System.err.println("Error al obtener plugins: " + e.getMessage());
            return null;
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
