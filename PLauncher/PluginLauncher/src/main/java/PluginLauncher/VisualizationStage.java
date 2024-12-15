package PluginLauncher;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import mo.organization.ProjectOrganization;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mo.visualization.Playable;
import mo.visualization.VisualizationProvider;

public class VisualizationStage {
    
    private VBox visualizationPane; // Contenedor principal para la visualización
    private TabPane tabPane;
    private final List<VisualizationProvider> visualizationProviders = new ArrayList<>();
    private final ProjectOrganization organization;

    public VisualizationStage(List<Object> plugins, ProjectOrganization organization) {
        // Filtrar solo los plugins de tipo VisualizationProvider
        for (Object plugin : plugins) {
            if (plugin instanceof VisualizationProvider) {
                visualizationProviders.add((VisualizationProvider) plugin);
            }
        }
        this.organization = organization;
    }
    
    public void setTabPane(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    
    public void createVisualizationPane() {
        visualizationPane = new VBox(10); // Inicializar la variable de instancia
        visualizationPane.setId("visualizationPane"); // Asignar ID
        visualizationPane.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(visualizationPane); // Envolver en ScrollPane
        scrollPane.setFitToWidth(true);

        Tab visualizationTab = new Tab("Visualization");
        visualizationTab.setClosable(false);
        visualizationTab.setContent(scrollPane);

        tabPane.getTabs().add(visualizationTab); // Agregar la pestaña al TabPane
    }
    
    private File getFileForConfig(Object config) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");

        // Directorio predeterminado
        File defaultDirectory = new File("build/libs/DefaultProject/stage");
        if (!defaultDirectory.exists()) {
            defaultDirectory.mkdirs(); // Crear el directorio si no existe
            System.out.println("Default directory created: " + defaultDirectory.getAbsolutePath());
        }

        // Establecer el directorio inicial
        fileChooser.setInitialDirectory(defaultDirectory);

        // Configurar extensiones compatibles
        List<String> extensions = getSupportedExtensions(config);
        if (!extensions.isEmpty()) {
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Supported Files", extensions);
            fileChooser.getExtensionFilters().add(filter);
        }

        // Mostrar el cuadro de diálogo para seleccionar archivos
        return fileChooser.showOpenDialog(null);
    }

    public void refreshVisualizationPane(List<Object> visualizationProviders) {
        if (visualizationPane == null) {
            System.err.println("Visualization pane not initialized!");
            return;
        }

        // Limpiar el contenido del contenedor principal
        visualizationPane.getChildren().clear();

        // Iterar sobre los plugins de visualización y añadirlos al contenedor
        for (Object plugin : visualizationProviders) {
            try {
                TitledPane pluginPane = createVisualizationProviderPane(plugin); // Crear el panel para el plugin
                visualizationPane.getChildren().add(pluginPane); // Añadir al contenedor
            } catch (Exception e) {
                System.err.println("Error while creating plugin pane: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private TitledPane createVisualizationProviderPane(Object plugin) throws Exception {
        String pluginName = (String) plugin.getClass().getMethod("getName").invoke(plugin);

        VBox pluginContent = new VBox(10);
        Button newConfigButton = new Button("New Configuration");
        newConfigButton.setOnAction(e -> createNewVisualizationConfiguration(plugin));

        pluginContent.getChildren().add(newConfigButton);

        // Obtener configuraciones existentes
        Method getConfigurationsMethod = plugin.getClass().getMethod("getConfigurations");
        List<?> configurations = (List<?>) getConfigurationsMethod.invoke(plugin);

        for (Object config : configurations) {
            createConfigUI(pluginContent, plugin, config);
        }

        TitledPane pluginPane = new TitledPane(pluginName, pluginContent);
        pluginPane.setExpanded(false); // Inicia contraído
        return pluginPane;
    }

    private void createConfigUI(VBox pluginBox, Object plugin, Object config) {
        try {
            // Obtener ID de la configuración
            Method getIdMethod = config.getClass().getMethod("getId");
            String configId = (String) getIdMethod.invoke(config);
            Label configLabel = new Label("Configuration: " + configId);

            // Selector de archivos
            ComboBox<File> fileComboBox = new ComboBox<>();
            fileComboBox.setPromptText("Select a file...");
            Button browseButton = new Button("Browse");
            browseButton.setOnAction(e -> {
                File selectedFile = getFileForConfig(config);
                if (selectedFile != null) {
                    fileComboBox.getItems().add(selectedFile);
                    fileComboBox.setValue(selectedFile);
                }
            });

            // Botón para reproducir
            Button visualizeButton = new Button("Play");
            visualizeButton.setDisable(true);
            fileComboBox.valueProperty().addListener((obs, oldFile, newFile) -> visualizeButton.setDisable(newFile == null));

            visualizeButton.setOnAction(e -> {
                File selectedFile = fileComboBox.getValue();
                if (selectedFile != null) {
                    startVisualizationWithFile(plugin, config, selectedFile);
                }
            });

            // Añadir controles al contenedor del plugin
            HBox fileSelectionBox = new HBox(10, fileComboBox, browseButton);
            pluginBox.getChildren().addAll(configLabel, fileSelectionBox, visualizeButton);
        } catch (Exception ex) {
            System.err.println("Error creating configuration controls: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void createNewVisualizationConfiguration(Object plugin) {
        try {
            Method initNewConfigMethod = plugin.getClass().getMethod("initNewConfiguration", ProjectOrganization.class);
            Object newConfig = initNewConfigMethod.invoke(plugin, organization);

            if (newConfig != null) {
                System.out.println("New visualization configuration created.");
                saveSingleVisualizationConfiguration(plugin, newConfig);

                // Actualizar directamente el contenedor existente
                ScrollPane scrollPane = (ScrollPane) tabPane.lookup(".scroll-pane");
                if (scrollPane == null || !(scrollPane.getContent() instanceof VBox)) {
                    System.err.println("Visualization pane not found!");
                    return;
                }

                VBox visualizationPane = (VBox) scrollPane.getContent();
                for (javafx.scene.Node node : visualizationPane.getChildren()) {
                    if (node instanceof TitledPane) {
                        TitledPane titledPane = (TitledPane) node;
                        if (titledPane.getText().equals((String) plugin.getClass().getMethod("getName").invoke(plugin))) {
                            VBox pluginContent = (VBox) titledPane.getContent();
                            createConfigUI(pluginContent, plugin, newConfig); // Agregar la nueva configuración
                            return;
                        }
                    }
                }

                // Si no se encuentra el plugin, agrega uno nuevo
                TitledPane pluginPane = createVisualizationProviderPane(plugin);
                visualizationPane.getChildren().add(pluginPane);
            } else {
                System.out.println("No configuration created.");
            }
        } catch (Exception e) {
            System.err.println("Error creating new visualization configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startVisualizationWithFile(Object plugin, Object config, File file) {
        try {
            Method addFileMethod = config.getClass().getMethod("addFile", File.class);
            addFileMethod.invoke(config, file);

            Method getPlayerMethod = config.getClass().getMethod("getPlayer");
            Object player = getPlayerMethod.invoke(config);

            if (player instanceof mo.visualization.Playable) {
                integrateControlsAndVisualization(plugin, player);
            } else {
                System.err.println("Player is not an instance of Playable.");
            }
        } catch (Exception e) {
            System.err.println("Error starting visualization with file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveSingleVisualizationConfiguration(Object plugin, Object config) {
        try {
            File visualizationDir = new File("build/libs/DefaultProject/visualization");
            if (!visualizationDir.exists()) {
                visualizationDir.mkdirs();
            }

            Method getIdMethod = config.getClass().getMethod("getId");
            String configId = (String) getIdMethod.invoke(config);

            Method getNameMethod = plugin.getClass().getMethod("getName");
            String providerName = (String) getNameMethod.invoke(plugin);

            String fileName = providerName + "_" + configId + ".xml";
            File configFile = new File(visualizationDir, fileName);

            // Guardar configuración (simulado, dependerá de tus plugins)
            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write("<configuration id=\"" + configId + "\" provider=\"" + providerName + "\"></configuration>");
            }

            System.out.println("Visualization configuration saved: " + configFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error saving visualization configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<String> getSupportedExtensions(Object config) {
        try {
            Method getCompatibleCreatorsMethod = config.getClass().getMethod("getCompatibleCreators");
            List<String> creators = (List<String>) getCompatibleCreatorsMethod.invoke(config);

            // Mapear creadores a extensiones de archivo
            List<String> supportedExtensions = new ArrayList<>();
            for (String creator : creators) {
                if (creator.contains("Keyboard") || creator.contains("Mouse")) {
                    supportedExtensions.add("*.txt");
                } else if (creator.contains("Audio")) {
                    supportedExtensions.add("*.wav");
                } else if (creator.contains("Video")) {
                    supportedExtensions.add("*.mp4");
                }
            }
            return supportedExtensions;
        } catch (Exception e) {
            System.err.println("Error getting supported extensions: " + e.getMessage());
            e.printStackTrace();
            return Arrays.asList("*.*"); // Predeterminado a permitir todos los archivos si hay un error
        }
    }

    private void integrateControlsAndVisualization(Object plugin, Object player) {
        try {
            // Verificar que el player sea una instancia de Playable
            if (!(player instanceof mo.visualization.Playable)) {
                throw new IllegalArgumentException("Player is not an instance of Playable");
            }

            Playable playable = (Playable) player; // Cast seguro
            long start = playable.getStart();
            long end = playable.getEnd();
            AtomicLong currentTime = new AtomicLong(start);

            // Obtener el panel de visualización desde el plugin
            javafx.scene.Node visualizationPanel = (javafx.scene.Node) invokeMethod(player, "getPaneNode");
            if (visualizationPanel == null) {
                throw new IllegalStateException("Visualization panel is null.");
            }

            // Configurar dimensiones iniciales y restricciones
            if (visualizationPanel instanceof Region) {
                Region region = (Region) visualizationPanel;
                region.setPrefSize(800, 400); // Tamaño preferido
                region.setMinHeight(200); // Altura mínima
                // Permitir que el nodo de visualización crezca, pero sin empujar a los controles
                VBox.setVgrow(region, Priority.ALWAYS);
            }

            // Crear controles de reproducción
            Button playButton = new Button("Play");
            Button pauseButton = new Button("Pause");
            Button stopButton = new Button("Stop");

            AtomicBoolean isPlaying = new AtomicBoolean(false);

            // Configurar acciones de los botones
            playButton.setOnAction(e -> {
                if (!isPlaying.get()) {
                    isPlaying.set(true);
                    playable.play(currentTime.get());
                }
            });

            pauseButton.setOnAction(e -> {
                if (isPlaying.get()) {
                    isPlaying.set(false);
                    playable.pause();
                }
            });

            stopButton.setOnAction(e -> {
                isPlaying.set(false);
                currentTime.set(start);
                playable.stop();
            });

            // Layout para los controles
            HBox controls = new HBox(10);
            controls.getChildren().addAll(playButton, pauseButton, stopButton);
            controls.setAlignment(Pos.CENTER);
            controls.setPadding(new Insets(10));
            controls.setStyle("-fx-background-color: #f4f4f4;");
            controls.setMinHeight(50); // Altura mínima para asegurar visibilidad

            // Crear BorderPane y asignar nodos
            BorderPane mainLayout = new BorderPane();
            mainLayout.setCenter(visualizationPanel); // Nodo de visualización en el centro
            mainLayout.setBottom(controls); // Controles en la parte inferior

            // Asegurar que los controles estén centrados y con margen
            BorderPane.setAlignment(controls, Pos.CENTER);
            BorderPane.setMargin(controls, new Insets(0, 0, 10, 0));

            // Crear y configurar la escena
            Scene scene = new Scene(mainLayout, 800, 600);
            Stage playerStage = new Stage();
            playerStage.setTitle("Playback - Visualization Plugin");
            playerStage.setResizable(true);
            playerStage.setScene(scene);

            // Manejar el cierre de la ventana
            playerStage.setOnCloseRequest(e -> {
                isPlaying.set(false);
                playable.stop();
            });

            // Mostrar la ventana
            playerStage.show();

        } catch (Exception ex) {
            System.err.println("Error integrating controls and visualization: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private Object invokeMethod(Object target, String methodName, Object... args) {
        try {
            Class<?>[] parameterTypes = Arrays.stream(args).map(arg -> {
                if (arg instanceof Long) {
                    return long.class; // Manejar long explícitamente
                }
                return arg.getClass();
            }).toArray(Class<?>[]::new);

            Method method = target.getClass().getMethod(methodName, parameterTypes);
            return method.invoke(target, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
