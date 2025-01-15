package PluginLauncher;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import mo.analysis.AnalysisProvider;
import mo.analysis.PlayableAnalyzableConfiguration;
import mo.organization.Configuration;
import mo.organization.ProjectOrganization;
import mo.organization.Participant;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mo.visualization.Playable;

public class AnalysisPlatform {

    private final List<AnalysisProvider> analysisProviders;
    private final ProjectOrganization organization;
    private final Participant defaultParticipant; 
    private VBox analysisPane;

    public AnalysisPlatform(List<AnalysisProvider> analysisProviders, ProjectOrganization organization, Participant defaultParticipant) {
        this.analysisProviders = analysisProviders;
        this.organization = organization;
        this.defaultParticipant = defaultParticipant; 
    }

    public VBox createAnalysisPane() {
        analysisPane = new VBox(10);
        analysisPane.setPadding(new javafx.geometry.Insets(10));
        refreshAnalysisPane();
        return analysisPane;
    }

    public List<AnalysisProvider> getAnalysisProviders() {
        return analysisProviders;
    }

    public void refreshAnalysisPane() {
        analysisPane.getChildren().clear();

        for (AnalysisProvider provider : analysisProviders) {
            TitledPane providerPane = createProviderPane(provider);
            analysisPane.getChildren().add(providerPane);
        }
    }

    public void manageProviderAnalysis(AnalysisProvider provider) {
        List<Configuration> configs = provider.getConfigurations();
        if (configs.isEmpty()) {
            showAlert("No hay configuraciones disponibles para este proveedor.");
            return;
        }

        ChoiceDialog<Configuration> dialog = new ChoiceDialog<>(configs.get(0), configs);
        dialog.setTitle("Manage Configurations");
        dialog.setHeaderText("Select a configuration to manage:");
        dialog.setContentText("Configurations:");

        dialog.showAndWait().ifPresent(selectedConfig -> {
            if (selectedConfig instanceof PlayableAnalyzableConfiguration) {
                File file = selectFile(defaultParticipant);
                if (file != null) {
                    startAnalysisWithPlayback(selectedConfig, file);
                } else {
                    showAlert("No se seleccionó ningún archivo.");
                }
            } else {
                showAlert("Esta configuración no soporta análisis reproducible.");
            }
        });
    }


    private TitledPane createProviderPane(AnalysisProvider provider) {
        VBox content = new VBox(10);

        Button newConfigButton = new Button("New Configuration");
        newConfigButton.setOnAction(e -> {
            Configuration config = getOrCreateConfiguration(provider);
            if (config != null) {
                addConfigUI(config, content, provider);
            } else {
                showAlert("No se pudo crear una nueva configuración.");
            }
        });

        Button manageAnalysisButton = new Button("Manage Analysis");
        manageAnalysisButton.setOnAction(e -> manageProviderAnalysis(provider));

        HBox buttonBox = new HBox(10, newConfigButton, manageAnalysisButton);
        content.getChildren().add(buttonBox);

        TitledPane pane = new TitledPane(provider.getName(), content);
        pane.setExpanded(false);

        return pane;
    }

    
    private void addConfigUI(Configuration config, VBox parent, AnalysisProvider provider) {
        Label configLabel = new Label("Configuration: " + config.getId());
        Button browseButton = new Button("Browse");
        ComboBox<File> fileComboBox = new ComboBox<>();
        fileComboBox.setPromptText("Select a file...");
        Button analyzeButton = new Button("Manage Analysis");

        browseButton.setOnAction(e -> {
            File file = selectFile(defaultParticipant);
            if (file != null) {
                fileComboBox.getItems().add(file);
                fileComboBox.setValue(file);
            } else {
                showAlert("No se seleccionó ningún archivo.");
            }
        });

        Button playButton = new Button("Analyze");
        playButton.setDisable(true);

        fileComboBox.valueProperty().addListener((obs, oldFile, newFile) -> playButton.setDisable(newFile == null));

        playButton.setOnAction(e -> {
            File selectedFile = fileComboBox.getValue();
            if (selectedFile != null) {
                startAnalysisWithPlayback(config, selectedFile);
            }
        });

        HBox fileSelectionBox = new HBox(10, fileComboBox, browseButton, playButton);
        VBox configBox = new VBox(5, configLabel, fileSelectionBox);

        parent.getChildren().add(configBox);
    }
    
    private void startAnalysisWithPlayback(Configuration config, File file) {
        try {
            if (config instanceof PlayableAnalyzableConfiguration) {
                PlayableAnalyzableConfiguration analyzableConfig = (PlayableAnalyzableConfiguration) config;

                analyzableConfig.addFile(file);
                analyzableConfig.setupAnalysis(organization.getLocation(), organization, defaultParticipant);
                analyzableConfig.startAnalysis();

                Playable player = analyzableConfig.getPlayer();
                if (player != null) {
                    integrateControlsAndPlayback(player);
                } else {
                    showAlert("El análisis no generó un reproductor visualizable.");
                }
            } else {
                showAlert("La configuración seleccionada no soporta análisis.");
            }
        } catch (Exception ex) {
            showAlert("Error durante el análisis: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void integrateControlsAndPlayback(Playable player) {
        try {
            long start = player.getStart();
            long end = player.getEnd();
            AtomicLong currentTime = new AtomicLong(start);

            Node playbackPanel = (Node) invokeMethod(player, "getPaneNode");
            if (playbackPanel == null) {
                throw new IllegalStateException("Playback panel is null.");
            }

            Button playButton = new Button("Play");
            Button pauseButton = new Button("Pause");
            Button stopButton = new Button("Stop");

            AtomicBoolean isPlaying = new AtomicBoolean(false);

            playButton.setOnAction(e -> {
                if (!isPlaying.get()) {
                    isPlaying.set(true);
                    player.play(currentTime.get());
                }
            });

            pauseButton.setOnAction(e -> {
                if (isPlaying.get()) {
                    isPlaying.set(false);
                    player.pause();
                }
            });

            stopButton.setOnAction(e -> {
                isPlaying.set(false);
                currentTime.set(start);
                player.stop();
            });

            HBox controls = new HBox(10);
            controls.getChildren().addAll(playButton, pauseButton, stopButton);
            controls.setAlignment(Pos.CENTER);
            controls.setPadding(new Insets(10));
            controls.setStyle("-fx-background-color: #f4f4f4;");

            BorderPane layout = new BorderPane();
            layout.setCenter(playbackPanel);
            layout.setBottom(controls);

            Scene scene = new Scene(layout, 800, 600);
            Stage stage = new Stage();
            stage.setTitle("Playback - Analysis Plugin");
            stage.setScene(scene);

            stage.setOnCloseRequest(e -> {
                isPlaying.set(false);
                player.stop();
            });

            stage.show();

        } catch (Exception ex) {
            showAlert("Error integrating playback controls: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    private Configuration getOrCreateConfiguration(AnalysisProvider provider) {
        List<Configuration> configs = provider.getConfigurations();
        if (!configs.isEmpty()) {
            return configs.get(0); 
        }

        return provider.initNewConfiguration(organization);
    }
    
    
    private Object invokeMethod(Object target, String methodName, Object... args) {
        try {
            Class<?>[] parameterTypes = Arrays.stream(args)
                    .map(arg -> arg == null ? Object.class : arg.getClass())
                    .toArray(Class<?>[]::new);

            Method method = target.getClass().getMethod(methodName, parameterTypes);
            return method.invoke(target, args);
        } catch (Exception e) {
            System.err.println("Error invoking method " + methodName + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private File selectFile(Participant participant) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar un archivo para análisis");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Todos los archivos", "*.*"));

        File participantFolder = new File(organization.getLocation(), participant.folder + "/capture");
        if (participantFolder.exists()) {
            fileChooser.setInitialDirectory(participantFolder);
        } else {
            System.err.println("La carpeta del participante no existe: " + participantFolder.getAbsolutePath());
            fileChooser.setInitialDirectory(new File("."));
        }

        return fileChooser.showOpenDialog(null);
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
