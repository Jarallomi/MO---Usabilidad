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
import mo.organization.Participant;
import mo.visualization.Playable;
import mo.visualization.VisualizationProvider;

public class VisualizationPlatform {
    
    private VBox visualizationPane; 
    private TabPane tabPane;
    private final List<VisualizationProvider> visualizationProviders = new ArrayList<>();
    private final ProjectOrganization organization;
    private final Participant defaultParticipant;

    public VisualizationPlatform(List<VisualizationProvider> plugins, ProjectOrganization organization, Participant defaultParticipant) {
        this.visualizationProviders.addAll(plugins);
        this.organization = organization;
        this.defaultParticipant = defaultParticipant;
    }

    public VBox createVisualizationPane() {
        visualizationPane = new VBox(10);
        visualizationPane.setId("visualizationPane");
        visualizationPane.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(visualizationPane);
        scrollPane.setFitToWidth(true);

        Tab visualizationTab = new Tab("Visualization");
        visualizationTab.setClosable(false);
        visualizationTab.setContent(scrollPane);

        if (tabPane != null) {
            tabPane.getTabs().add(visualizationTab);
        }

        return visualizationPane; 
    }

    
    private File getFileForConfig(Object config, Participant defaultParticipant) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");

        File defaultDirectory = new File(organization.getLocation(), defaultParticipant.folder + "/capture");
        if (!defaultDirectory.exists()) {
            defaultDirectory.mkdirs();
            System.out.println("Default directory created: " + defaultDirectory.getAbsolutePath());
        }

        fileChooser.setInitialDirectory(defaultDirectory);

        List<String> extensions = getSupportedExtensions(config);
        if (!extensions.isEmpty()) {
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Supported Files", extensions);
            fileChooser.getExtensionFilters().add(filter);
        }

        return fileChooser.showOpenDialog(null);
    }

    public void refreshVisualizationPane(List<VisualizationProvider> visualizationProviders) {
        if (visualizationPane == null) {
            System.err.println("Visualization pane not initialized!");
            return;
        }

        visualizationPane.getChildren().clear();

        for (VisualizationProvider plugin : visualizationProviders) {
            try {
                TitledPane pluginPane = createVisualizationProviderPane(plugin);
                visualizationPane.getChildren().add(pluginPane);
            } catch (Exception e) {
                System.err.println("Error while creating plugin pane: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }



    private TitledPane createVisualizationProviderPane(VisualizationProvider plugin) throws Exception {
        String pluginName = plugin.getName();

        VBox pluginContent = new VBox(10);
        Button newConfigButton = new Button("New Configuration");
        newConfigButton.setOnAction(e -> createNewVisualizationConfiguration(plugin));

        pluginContent.getChildren().add(newConfigButton);

        List<?> configurations = plugin.getConfigurations();
        for (Object config : configurations) {
            createConfigUI(pluginContent, plugin, config);
        }

        TitledPane pluginPane = new TitledPane(pluginName, pluginContent);
        pluginPane.setExpanded(false);
        return pluginPane;
    }


    private void createConfigUI(VBox pluginBox, Object plugin, Object config) {
        try {
            Method getIdMethod = config.getClass().getMethod("getId");
            String configId = (String) getIdMethod.invoke(config);
            Label configLabel = new Label("Configuration: " + configId);

            ComboBox<File> fileComboBox = new ComboBox<>();
            fileComboBox.setPromptText("Select a file...");
            Button browseButton = new Button("Browse");
            browseButton.setOnAction(e -> {
                File selectedFile = getFileForConfig(config, defaultParticipant);
                if (selectedFile != null) {
                    fileComboBox.getItems().add(selectedFile);
                    fileComboBox.setValue(selectedFile);
                }
            });

            Button visualizeButton = new Button("Play");
            visualizeButton.setDisable(true);
            fileComboBox.valueProperty().addListener((obs, oldFile, newFile) -> visualizeButton.setDisable(newFile == null));

            visualizeButton.setOnAction(e -> {
                File selectedFile = fileComboBox.getValue();
                if (selectedFile != null) {
                    startVisualizationWithFile(plugin, config, selectedFile);
                }
            });

            HBox fileSelectionBox = new HBox(10, fileComboBox, browseButton);
            pluginBox.getChildren().addAll(configLabel, fileSelectionBox, visualizeButton);
        } catch (Exception ex) {
            System.err.println("Error creating configuration controls: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void createNewVisualizationConfiguration(VisualizationProvider plugin) {
        try {
            Object newConfig = plugin.initNewConfiguration(organization);

            if (newConfig != null) {
                System.out.println("New visualization configuration created.");
                saveSingleVisualizationConfiguration(plugin, newConfig);
                refreshVisualizationUI(plugin, newConfig);
            } else {
                System.out.println("No configuration created.");
            }
        } catch (Exception e) {
            System.err.println("Error creating new visualization configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    private void refreshVisualizationUI(VisualizationProvider plugin, Object newConfig) {
        try {
            for (javafx.scene.Node node : visualizationPane.getChildren()) {
                if (node instanceof TitledPane) {
                    TitledPane titledPane = (TitledPane) node;

                    if (titledPane.getText().equals(plugin.getName())) {
                        VBox content = (VBox) titledPane.getContent();
                        createConfigUI(content, plugin, newConfig);
                        System.out.println("UI for new visualization configuration added.");
                        return;
                    }
                }
            }

            TitledPane newPluginPane = createVisualizationProviderPane(plugin);
            visualizationPane.getChildren().add(newPluginPane);
        } catch (Exception e) {
            System.err.println("Error updating visualization UI: " + e.getMessage());
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
            File visualizationDir = new File("DefaultProject/visualization");
            if (!visualizationDir.exists()) {
                visualizationDir.mkdirs();
            }

            Method getIdMethod = config.getClass().getMethod("getId");
            String configId = (String) getIdMethod.invoke(config);

            Method getNameMethod = plugin.getClass().getMethod("getName");
            String providerName = (String) getNameMethod.invoke(plugin);

            String fileName = providerName + "_" + configId + ".xml";
            File configFile = new File(visualizationDir, fileName);

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
            return Arrays.asList("*.*");
        }
    }

    private void integrateControlsAndVisualization(Object plugin, Object player) {
        try {
            if (!(player instanceof mo.visualization.Playable)) {
                throw new IllegalArgumentException("Player is not an instance of Playable");
            }

            Playable playable = (Playable) player; 
            long start = playable.getStart();
            long end = playable.getEnd();
            AtomicLong currentTime = new AtomicLong(start);

            javafx.scene.Node visualizationPanel = (javafx.scene.Node) invokeMethod(player, "getPaneNode");
            if (visualizationPanel == null) {
                throw new IllegalStateException("Visualization panel is null.");
            }

            if (visualizationPanel instanceof Region) {
                Region region = (Region) visualizationPanel;
                region.setPrefSize(800, 400); 
                region.setMinHeight(200); 
                VBox.setVgrow(region, Priority.ALWAYS);
            }

            Button playButton = new Button("Play");
            Button pauseButton = new Button("Pause");
            Button stopButton = new Button("Stop");

            AtomicBoolean isPlaying = new AtomicBoolean(false);

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

            HBox controls = new HBox(10);
            controls.getChildren().addAll(playButton, pauseButton, stopButton);
            controls.setAlignment(Pos.CENTER);
            controls.setPadding(new Insets(10));
            controls.setStyle("-fx-background-color: #f4f4f4;");
            controls.setMinHeight(50);

            BorderPane mainLayout = new BorderPane();
            mainLayout.setCenter(visualizationPanel);
            mainLayout.setBottom(controls);

            BorderPane.setAlignment(controls, Pos.CENTER);
            BorderPane.setMargin(controls, new Insets(0, 0, 10, 0));

            Scene scene = new Scene(mainLayout, 800, 600);
            Stage playerStage = new Stage();
            playerStage.setTitle("Playback - Visualization Plugin");
            playerStage.setResizable(true);
            playerStage.setScene(scene);

            playerStage.setOnCloseRequest(e -> {
                isPlaying.set(false);
                playable.stop();
            });

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
                    return long.class;
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
