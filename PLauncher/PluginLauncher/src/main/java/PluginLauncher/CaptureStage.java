package PluginLauncher;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import mo.capture.CaptureProvider;
import mo.organization.Configuration;
import mo.organization.ProjectOrganization;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

public class CaptureStage {

    private final List<CaptureProvider> captureProviders;
    private final ProjectOrganization organization;
    private VBox capturePane; // Contenedor principal para captura
    private TabPane tabPane;

    public CaptureStage(List<CaptureProvider> captureProviders, ProjectOrganization organization) {
        this.captureProviders = captureProviders;
        this.organization = organization;
    }

    public void setTabPane(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public void createCapturePane() {
        // Inicializar la variable de instancia capturePane
        capturePane = new VBox(10);
        capturePane.setId("capturePane"); // Asignar ID
        capturePane.setPadding(new javafx.geometry.Insets(10));

        // Envolver el VBox en un ScrollPane
        ScrollPane scrollPane = new ScrollPane(capturePane);
        scrollPane.setFitToWidth(true);

        // Crear la pestaña de captura y agregar el ScrollPane como contenido
        Tab captureTab = new Tab("Capture");
        captureTab.setClosable(false);
        captureTab.setContent(scrollPane);

        // Agregar la pestaña al TabPane
        if (tabPane == null) {
            throw new IllegalStateException("TabPane is not initialized in CaptureStage");
        }
        tabPane.getTabs().add(captureTab);
    }

    // Actualizar la pestaña de captura
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

    // Crear un panel para cada proveedor
    private TitledPane createCaptureProviderPane(CaptureProvider provider) {
        VBox content = new VBox(10);

        Button newConfigButton = new Button("New Configuration");
        newConfigButton.setOnAction(e -> createNewCaptureConfiguration(provider, content));

        content.getChildren().add(newConfigButton);

        TitledPane pane = new TitledPane(provider.getName(), content);
        pane.setExpanded(false);

        return pane;
    }

    // Crear una nueva configuración para un proveedor
    public void createNewCaptureConfiguration(CaptureProvider provider, VBox parent) {
        Configuration configuration = provider.initNewConfiguration(organization);

        if (configuration != null) {
            saveSingleConfiguration(provider, configuration);
            createConfigUI(configuration, parent);
        }
    }

    // Crear la interfaz de usuario para una configuración
    private void createConfigUI(Configuration configuration, VBox parent) {
        Label configLabel = new Label("Configuration: " + configuration.getId());
        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");

        startButton.setOnAction(e -> {
            try {
                if (configuration instanceof mo.capture.RecordableConfiguration) {
                    mo.capture.RecordableConfiguration recordable = (mo.capture.RecordableConfiguration) configuration;

                    File stageFolder = getStageFolder(recordable);

                    Method setupMethod = recordable.getClass().getMethod(
                            "setupRecording", File.class, ProjectOrganization.class, mo.organization.Participant.class);
                    setupMethod.invoke(recordable, stageFolder, organization, null);

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

    // Obtener la carpeta del plugin
    private File getStageFolder(Object plugin) {
        if (organization == null || organization.getLocation() == null) {
            System.err.println("Organization or its location is null!");
            return new File("unknown");
        }

        try {
            String pluginName;

            try {
                Method getNameMethod = plugin.getClass().getMethod("getName");
                pluginName = (String) getNameMethod.invoke(plugin);

                if (pluginName == null || pluginName.isEmpty()) {
                    pluginName = plugin.getClass().getSimpleName();
                }
            } catch (NoSuchMethodException e) {
                System.err.println("Method 'getName' not found, using class name instead: " + plugin.getClass().getSimpleName());
                pluginName = plugin.getClass().getSimpleName();
            }

            pluginName = pluginName.replaceAll("\\s+", "_").toLowerCase();
            File stageFolder = new File(organization.getLocation(), "stage/" + pluginName);
            if (!stageFolder.exists()) {
                stageFolder.mkdirs();
                System.out.println("Created stage folder: " + stageFolder.getAbsolutePath());
            }

            return stageFolder;
        } catch (Exception e) {
            System.err.println("Error determining stage folder for plugin: " + e.getMessage());
            e.printStackTrace();
            return new File(organization.getLocation(), "stage/unknown");
        }
    }

    // Guardar una configuración
    private void saveSingleConfiguration(CaptureProvider provider, Configuration config) {
        try {
            File captureDir = new File("build/libs/DefaultProject/capture");
            if (!captureDir.exists()) {
                captureDir.mkdirs();
            }

            String fileName = provider.getName() + "_" + config.getId() + ".xml";
            File configFile = new File(captureDir, fileName);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element rootElement = doc.createElement("Configuration");
            doc.appendChild(rootElement);

            rootElement.setAttribute("provider", provider.getName());
            rootElement.setAttribute("id", config.getId());

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            StreamResult result = new StreamResult(configFile);
            transformer.transform(source, result);

            System.out.println("Configuration saved: " + configFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
