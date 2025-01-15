package PluginLauncher;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import mo.capture.CaptureProvider;
import mo.organization.Configuration;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

public class CapturePlatform {

    private final List<CaptureProvider> captureProviders;
    private final ProjectOrganization organization;
    private VBox capturePane;
    private TabPane tabPane;
    private final Participant defaultParticipant; 

    public CapturePlatform(List<CaptureProvider> captureProviders, ProjectOrganization organization, Participant defaultParticipant) {
        this.captureProviders = captureProviders;
        this.organization = organization;
        this.defaultParticipant = defaultParticipant;
    }

    public VBox getCapturePane() {
        return this.capturePane;
    }

    public VBox createCapturePane() {
        capturePane = new VBox(10);
        capturePane.setId("capturePane");
        capturePane.setPadding(new javafx.geometry.Insets(10));

        ScrollPane scrollPane = new ScrollPane(capturePane);
        scrollPane.setFitToWidth(true);

        Tab captureTab = new Tab("Capture");
        captureTab.setClosable(false);
        captureTab.setContent(scrollPane);

        if (tabPane != null) {
            tabPane.getTabs().add(captureTab);
        }

        return capturePane; 
    }

    public void refreshCapturePane() {
        if (capturePane != null) {
            capturePane.getChildren().clear();

            for (CaptureProvider provider : captureProviders) {
                TitledPane providerPane = createCaptureProviderPane(provider);
                capturePane.getChildren().add(providerPane);
            }
        } else {
            System.err.println("Capture pane is not initialized!");
        }
    }

    private TitledPane createCaptureProviderPane(CaptureProvider provider) {
        VBox content = new VBox(10);

        Button newConfigButton = new Button("New Configuration");
        newConfigButton.setOnAction(e -> createNewCaptureConfiguration(provider, content));

        content.getChildren().add(newConfigButton);

        TitledPane pane = new TitledPane(provider.getName(), content);
        pane.setExpanded(false);

        return pane;
    }

    public void createNewCaptureConfiguration(CaptureProvider provider, VBox parent) {
        Configuration configuration = provider.initNewConfiguration(organization);

        if (configuration != null) {
            saveConfigurationFile(provider, configuration);
            createConfigUI(configuration, parent);
        }
    }

    private void createConfigUI(Configuration configuration, VBox parent) {
        Label configLabel = new Label("Configuration: " + configuration.getId());
        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");

        startButton.setOnAction(e -> {
            try {
                if (configuration instanceof mo.capture.RecordableConfiguration) {
                    mo.capture.RecordableConfiguration recordable = (mo.capture.RecordableConfiguration) configuration;

                    File captureFolder = new File(organization.getLocation(), defaultParticipant.folder + "/capture");

                    Method setupMethod = recordable.getClass().getMethod(
                            "setupRecording", File.class, ProjectOrganization.class, Participant.class);
                    setupMethod.invoke(recordable, captureFolder, organization, defaultParticipant);

                    Method startRecordingMethod = recordable.getClass().getMethod("startRecording");
                    startRecordingMethod.invoke(recordable);

                    startButton.setDisable(true);
                    stopButton.setDisable(false);

                    System.out.println("Recording started for configuration: " + configuration.getId());
                }
            } catch (Exception ex) {
                System.err.println("Error starting recording: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        stopButton.setOnAction(e -> {
            try {
                if (configuration instanceof mo.capture.RecordableConfiguration) {
                    Method stopRecordingMethod = configuration.getClass().getMethod("stopRecording");
                    stopRecordingMethod.invoke(configuration);

                    startButton.setDisable(false);
                    stopButton.setDisable(true);

                    System.out.println("Recording stopped for configuration: " + configuration.getId());
                }
            } catch (Exception ex) {
                System.err.println("Error stopping recording: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        stopButton.setDisable(true);

        HBox controls = new HBox(10, startButton, stopButton);
        VBox configBox = new VBox(5, configLabel, controls);

        parent.getChildren().add(configBox);
    }

    private void saveConfigurationFile(CaptureProvider provider, Configuration config) {
        try {
            File captureDir = new File(organization.getLocation(), "capture");
            if (!captureDir.exists()) {
                captureDir.mkdirs();
            }

            String fileName = provider.getName() + "_" + config.getId() + ".xml";
            File configFile = new File(captureDir, fileName);

            if (!configFile.exists()) {
                configFile.createNewFile();
            }

            System.out.println("Configuration saved: " + configFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error saving configuration file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
