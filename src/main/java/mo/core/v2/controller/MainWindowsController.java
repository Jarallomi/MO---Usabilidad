/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.core.v2.controller;

import com.google.inject.Inject;
import com.google.inject.Injector;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import mo.core.MultimodalObserver;
import mo.core.filemanagement.project.Project;
import mo.core.preferences.AppPreferencesWrapper;
import mo.core.preferences.PreferencesManager;
import mo.core.v2.model.Organization;
import mo.organization.ProjectOrganization;

/**
 * FXML Controller class
 *
 * @author Francisco
 */
public class MainWindowsController implements Initializable {
    private final String CONTROLLER_KEY = "controller";
    
    @FXML
    private BorderPane mainPane;
    @FXML
    private Pane menuBar;
    @FXML
    private ImageView moIcon;
    @FXML
    private Button buttonNewProject;
    @FXML
    private Button buttonMyProjects;
    @FXML
    private Button buttonPlugins;
    @FXML
    private Button buttonSettings;
    @FXML
    private Button buttonExit;
    @FXML
    private Pane centerPane;
    @FXML
    private Text textWelcome;
    private GridPane projectsGrid = new GridPane();
    private ScrollPane scrollPane = new ScrollPane();
    private ImageView iconProject;
    private Text name = new Text(), name2 = new Text();
    private Text numParticipants = new Text(), numParticipants2 = new Text();
    private Text numCaptures = new Text(), numCaptures2 = new Text();
    private Text numAnalysis = new Text(), numAnalysis2 = new Text();
    private Text numVisualization = new Text(), numVisualization2 = new Text();
    public Button seeButton = new Button("See");
    private Line line;
    @Inject
    public Injector injector;
    @Inject 
    public Organization model;
    public MyProjectsControllerAux mpca;
    public List<ProjectOrganization> projectsOrgs = new ArrayList<>();
    public int row = 0, colum = 0;
    private int confirm = -1;
    @FXML
    private Text textWelcome1;
    @FXML
    private Text textWelcome11;
    @FXML
    private Text textWelcome111;
    @FXML
    private Text textWelcome1111;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        Class c = AppPreferencesWrapper.class;
        File prefFile = new File(MultimodalObserver.APP_PREFERENCES_FILE);
        AppPreferencesWrapper preferences = (AppPreferencesWrapper) PreferencesManager.loadOrCreate(c, prefFile);
        model.newProyect=1;
        List<String> projectsNotFound = new ArrayList<>();
        preferences.getOpenedProjects().stream().forEach((openedProject) -> {
            File f = new File(openedProject.getLocation());
            if (f.exists()) {
                //addFile(openedProject.getLocation()));
                
            } else {
                projectsNotFound.add(openedProject.getLocation());
            }

        });
        for (String projectPath : projectsNotFound) {
            preferences.removeOpenedProject(projectPath);
        }
        PreferencesManager.save(preferences, prefFile);
    }   
    
    @FXML
    private void returnToBegining(MouseEvent event) {
        centerPane.getChildren().clear();
        textWelcome.setVisible(true);
        centerPane.getChildren().add(textWelcome);
        centerPane.getChildren().add(textWelcome1);
        centerPane.getChildren().add(textWelcome11);
        centerPane.getChildren().add(textWelcome111);
        centerPane.getChildren().add(textWelcome1111);
    }

    @FXML
    void newProject(MouseEvent event) {
        if(model.newProyect==0){
            if(confirm == 1){
                newProject();
                confirm = -1;
            }
            else{
                centerPane.getChildren().clear();
                textWelcome.setVisible(true);
                centerPane.getChildren().add(textWelcome);
                centerPane.getChildren().add(textWelcome1);
                centerPane.getChildren().add(textWelcome11);
                centerPane.getChildren().add(textWelcome111);
                centerPane.getChildren().add(textWelcome1111);
                model.newProyect = 1;
            }
        }
        if(model.newProyect==1){
            name();
            if(confirm == 1){
                newProject();
            }
            else{
                model.newProyect=-1;
                newProject(event);
            }
        }
        if(model.newProyect==-1){
            newProject();
        }
    }

    @FXML
    public void myProjects(ActionEvent event) {
        
        row=0;
        colum=0;
        mpca = new MyProjectsControllerAux();
        this.projectsOrgs = mpca.getDataByPath();
        //projectsOrgs = 
        centerPane.getChildren().clear();
        projectsGrid.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        projectsGrid.setPrefWidth(600);
        projectsGrid.setPrefHeight(400);
        projectsGrid.setAlignment(Pos.CENTER);
        projectsGrid.getChildren().clear();
        projectsGrid.getColumnConstraints().add(new ColumnConstraints(300));
        projectsGrid.getColumnConstraints().add(new ColumnConstraints(300));
        for(int i=0; i<3;i++){
            RowConstraints rowAux2 = new RowConstraints(133);
            rowAux2.vgrowProperty().set(Priority.NEVER);
            projectsGrid.getRowConstraints().add(rowAux2);
        }
        scrollPane.setPrefWidth(600);
        scrollPane.setPrefHeight(400);
        //scrollPane.setAlignment(Pos.CENTER);
        scrollPane.fitToWidthProperty().set(true);
        scrollPane.fitToHeightProperty().set(true);
        scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setContent(projectsGrid);
        for(RowConstraints o : projectsGrid.getRowConstraints()){
            //o.setPrefHeight(200);
            //o.setMinHeight(200);
            //o.minHeightProperty().set(200);
        }
        //initModules();
        showInfo();
        centerPane.getChildren().add(scrollPane);
    }

    @FXML
    public void plugins(ActionEvent event) {
        try {
            textWelcome.setVisible(false);
            centerPane.getChildren().clear();
            final JavaFXBuilderFactory builderFactory = new JavaFXBuilderFactory();
            final Callback<Class<?>, Object> callback = (clazz) -> injector.getInstance(clazz);
            FXMLLoader loaderOpen= new FXMLLoader(MainWindowsController.class.
                    getResource("/fxml/core/ui/Plugins.fxml"), null,
                    builderFactory, callback);
            
            Parent openParent = loaderOpen.load();
            openParent.getProperties().put(CONTROLLER_KEY, loaderOpen.getController());
            centerPane.getChildren().add(openParent);
        } catch (IOException ex) {
            Logger.getLogger(MainWindowsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void settings(ActionEvent event) {
        try{
            textWelcome.setVisible(false);
            centerPane.getChildren().clear();
            final JavaFXBuilderFactory builderFactory = new JavaFXBuilderFactory();
            final Callback<Class<?>, Object> callback = (clazz) -> injector.getInstance(clazz);
            FXMLLoader loaderOpen= new FXMLLoader(MainWindowsController.class.
                    getResource("/fxml/core/ui/Settings.fxml"), null,
                    builderFactory, callback);
            Parent openParent = loaderOpen.load();
            
            openParent.getProperties().put(CONTROLLER_KEY, loaderOpen.getController());
            centerPane.getChildren().add(openParent);
        }
        catch (IOException ex){
            Logger.getLogger(MainWindowsController.class
          .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void exit(ActionEvent event) {
        saveProjectInAppPreferences(model.getProject());
        //Salir
        Stage stage = (Stage) this.buttonExit.getScene().getWindow();
        stage.close();
        System.exit(0);
    }  
    
    //My projects
    private void initModules(){
        iconProject = new javafx.scene.image.ImageView("/images/folder.png");
        iconProject.setFitHeight(25);
        iconProject.setFitWidth(25);
        iconProject.setTranslateX(5);
        iconProject.setTranslateY(-20);
        projectsGrid.add(iconProject, colum, row);
        
        /*name.setText("Name: ");
        name.setTranslateX(5);
        name.setTranslateY(-24);
        projectsGrid.add(name, 0, 0);*/
        
        name2.setText("---");
        name2.setTranslateX(35);
        name2.setTranslateY(-20);
        name2.setStyle("-fx-font-weight: bold");
        projectsGrid.add(name2, 0, 0);
        
        numParticipants.setText("Participants: ");
        numParticipants.setTranslateX(5);
        numParticipants.setTranslateY(10);
        projectsGrid.add(numParticipants, 0, 0);
        
        numParticipants2.setText("0");
        numParticipants2.setTranslateX(85);
        numParticipants2.setTranslateY(10);
        projectsGrid.add(numParticipants2, 0, 0);
        
        numCaptures.setText("Captures: ");
        numCaptures.setTranslateX(5);
        numCaptures.setTranslateY(30);
        projectsGrid.add(numCaptures, 0, 0);
        
        numCaptures2.setText("0");
        numCaptures2.setTranslateX(85);
        numCaptures2.setTranslateY(30);
        projectsGrid.add(numCaptures2, 0, 0);
        
        numAnalysis.setText("Analysis: ");
        numAnalysis.setTranslateX(5);
        numAnalysis.setTranslateY(50);
        projectsGrid.add(numAnalysis, 0, 0);
        
        numAnalysis2.setText("0");
        numAnalysis2.setTranslateX(85);
        numAnalysis2.setTranslateY(50);
        projectsGrid.add(numAnalysis2, 0, 0);
        
        numVisualization.setText("Visualizations: ");
        numVisualization.setTranslateX(5);
        numVisualization.setTranslateY(70);
        projectsGrid.add(numVisualization, 0, 0);
        
        numVisualization2.setText("0");
        numVisualization2.setTranslateX(85);
        numVisualization2.setTranslateY(70);
        projectsGrid.add(numVisualization2, 0, 0);
        
        seeButton.setStyle("-fx-background-color: #3D3D3D40; ");
        seeButton.setTranslateX(210);
        seeButton.setTranslateY(60);
        projectsGrid.add(seeButton, 0, 0);   
        //colum++;
        
        line = new Line();
        line.setEndX(600);
        line.setStartX(0);
        line.setTranslateY(80);
        line.setStyle("-fx-stroke: #3D3D3D40; ");
        projectsGrid.add(line, 0, 0);
        
    }
    
    private void showInfo(){
        int aux = 0;
        numCaptures2.setText("0");
        numAnalysis2.setText("0");
        numVisualization2.setText("0");
        for(int i=0; i<projectsOrgs.size(); i++){
            ProjectOrganization PO = projectsOrgs.get(i);
            if(row>projectsGrid.getRowConstraints().size()){
                RowConstraints rowAux = new RowConstraints(100);
                projectsGrid.getRowConstraints().add(rowAux);
            }
            if(colum<2){
                createAndAddToGrid(PO,row,colum);
                if(colum==1){
                    colum=0;
                    row++;
                }
                else{
                    colum++;
                }
            }
            //}
        }
    }
    
    public void createAndAddToGrid(ProjectOrganization PO, int row, int colum){
        javafx.scene.image.ImageView icon2 = new javafx.scene.image.ImageView("/images/folder.png");
        String idAux = PO.getLocation().getName();
        icon2.setId(idAux);
        //icon2.setImage(iconProject.getImage());
        icon2.setFitHeight(25);
        icon2.setFitWidth(25);
        icon2.setTranslateX(5);
        icon2.setTranslateY(-20);
        projectsGrid.add(icon2, colum, row);
        
        /*Text name2 = new Text ("Name:");
        name2.setId(idAux);
        name2.setTranslateX(5);
        name2.setTranslateY(-24);
        projectsGrid.add(name2, colum, row);*/

        Text nameLabel2 = new Text(PO.getLocation().getName());
        nameLabel2.setId(idAux);
        nameLabel2.setTranslateX(35);
        nameLabel2.setTranslateY(-20);
        nameLabel2.setStyle("-fx-font-weight: bold");
        projectsGrid.add(nameLabel2, colum, row);

        Text participant2 = new Text("Participants:");
        participant2.setId(idAux);
        participant2.setTranslateX(5);
        participant2.setTranslateY(10);
        projectsGrid.add(participant2, colum, row);

        int a = PO.getParticipants().size();
        Text participantLabel2 = new Text(String.valueOf(a));
        participantLabel2.setId(idAux);
        participantLabel2.setTranslateX(85);
        participantLabel2.setTranslateY(10);
        projectsGrid.add(participantLabel2, colum, row);

        Text capture2 = new Text("Captures:");
        capture2.setId(idAux);
        capture2.setTranslateX(5);
        capture2.setTranslateY(30);
        projectsGrid.add(capture2, colum, row);
        
        Text analysis2 = new Text("Analysis:");
        analysis2.setId(idAux);
        analysis2.setTranslateX(5);
        analysis2.setTranslateY(50);
        projectsGrid.add(analysis2, colum, row);
        
        Text visualization2 = new Text("Visualizations:");
        visualization2.setId(idAux);
        visualization2.setTranslateX(5);
        visualization2.setTranslateY(70);
        projectsGrid.add(visualization2, colum, row);
        
        Text captureLabel2 = new Text("0");
        captureLabel2.setId(idAux);
        captureLabel2.setTranslateX(85);
        captureLabel2.setTranslateY(30);
        projectsGrid.add(captureLabel2, colum, row);

        Text analysisLabel2 = new Text("0");
        analysisLabel2.setId(idAux);
        analysisLabel2.setTranslateX(85);
        analysisLabel2.setTranslateY(50);
        projectsGrid.add(analysisLabel2, colum, row);

        Text visualizationLabel2 = new Text("0");
        visualizationLabel2.setId(idAux);
        visualizationLabel2.setTranslateX(85);
        visualizationLabel2.setTranslateY(70);
        projectsGrid.add(visualizationLabel2, colum, row);
        
        Button seeButton2 = new Button("See");
        seeButton2.setStyle("-fx-background-color: #3D3D3D40; ");
        seeButton2.setTranslateX(210);
        seeButton2.setTranslateY(60);
        projectsGrid.add(seeButton2, colum, row);
        seeButton2.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    model.setOrg(PO);
                    model.newProyect=0;
                    confirm = 1;
                    this.newProject(event);
                });
        
        line = new Line();
        line.setEndX(600);
        line.setStartX(0);
        line.setTranslateY(80);
        line.setStyle("-fx-stroke: #3D3D3D40; ");
        projectsGrid.add(line, colum, row);
        
        if(PO.getStages().isEmpty()){
            numCaptures2.setText("0");
            numAnalysis2.setText("0");
            numVisualization2.setText("0");
        }
        else{
            for(int i=0; i<PO.getStages().size(); i++){
                int aux = 0;
                for(int j=0; j<PO.getStages().get(i).getPlugins().size(); j++){
                    if(!PO.getStages().get(i).getPlugins().get(j).getConfigurations().isEmpty()){
                        aux = aux + PO.getStages().get(i).getPlugins().get(j).getConfigurations().size();
                    }
                }
                if(PO.getStages().get(i).getName().equals(model.getCaptureStage().getName())){
                    captureLabel2.setText(Integer.toString(aux));
                }
                if(PO.getStages().get(i).getName().equals(model.getAnalysisStage().getName())){
                    analysisLabel2.setText(String.valueOf(aux));
                }
                if(PO.getStages().get(i).getName().equals(model.getVisualizationStage().getName())){
                    visualizationLabel2.setText(String.valueOf(aux));
                }               
            }
        }
    }
    
    private void saveProjectInAppPreferences(Project project){
        PreferencesManager pm = new PreferencesManager();
        AppPreferencesWrapper app = (AppPreferencesWrapper) pm.loadOrCreate(AppPreferencesWrapper.class,new File(MultimodalObserver.APP_PREFERENCES_FILE));
        app.addOpenedProject(project.getFolder().getAbsolutePath());
        pm.save(app, new File(MultimodalObserver.APP_PREFERENCES_FILE));
    } 
    
    private void name(){
        try{
            Stage popUp = new Stage();
            final JavaFXBuilderFactory builderFactory = new JavaFXBuilderFactory();
            final Callback<Class<?>, Object> callback = (clazz) -> injector.getInstance(clazz);
            FXMLLoader loaderOpen = new FXMLLoader(ProjectMenuController.class
                    .getResource("/fxml/core/ui/NewProject.fxml"), null,
                    builderFactory, callback);
            Parent openParent = loaderOpen.load();
            openParent.getProperties()
                    .put(CONTROLLER_KEY, loaderOpen.getController());
            popUp.initModality(Modality.APPLICATION_MODAL);
            popUp.setScene(new Scene(openParent));
            popUp.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
                model.newProyect = -1;
            });
            popUp.showAndWait();
        }
        catch(IOException ex){
            Logger.getLogger(MainWindowsController.class
          .getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void newProject(){
        try{
            textWelcome.setVisible(false);
            centerPane.getChildren().clear();
            final JavaFXBuilderFactory builderFactory = new JavaFXBuilderFactory();
            final Callback<Class<?>, Object> callback = (clazz) -> injector.getInstance(clazz);
            FXMLLoader loaderOpen = new FXMLLoader(MainWindowsController.class
                .getResource("/fxml/core/ui/ProjectMenu.fxml"), null,
                builderFactory, callback);
            Parent openParent = loaderOpen.load();
            openParent.getProperties()
                .put(CONTROLLER_KEY, loaderOpen.getController());
            centerPane.getChildren().add(openParent);
        }
        catch (IOException ex) {
            Logger.getLogger(MainWindowsController.class
          .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
