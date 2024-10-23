package mo.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mo.organization.Configuration;
import mo.visualization.Playable;
import mo.visualization.VisualizableConfiguration;
import mo.analysis.AnalyzableConfiguration;
import mo.organization.ProjectOrganization;
import mo.organization.Participant;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.DockablesRegistry;
import javax.swing.JPanel;
import java.io.IOException;
import java.nio.file.Path;

import static mo.core.DataFileFinder.findFileCreatedFor;
import static mo.core.DataFileFinder.findFilesCreatedFor;

import mo.organization.FileDescription;

public class NotesAnalysisConfig implements PlayableAnalyzableConfiguration {

    private final String[] creators = {};
    
    private List<File> files;
    private String id;
    private NotesPlayer player;

    private List<NotesVisualization> playables;
    private List<NotesVisualization> visualizables;
    
    private static final Logger logger = Logger.getLogger(NotesAnalysisConfig.class.getName());
    private boolean stopped;

    private NotesRecorder recorder;

    private ProjectOrganization org;
    private File stageFolder;
    private Participant participant;

    private List<NotesRecorder> notesRecorders;

    public NotesAnalysisConfig() {
        playables = new ArrayList<>();
        visualizables = new ArrayList<>();
        files = new ArrayList<>();
    }

    public void addPlayable(NotesVisualization playable) {
        playables.add(playable);
    }

    public void addVisualizable(NotesVisualization visualizable) {
        visualizables.add(visualizable);
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public List<String> getCompatibleCreators() {
        return Arrays.asList(creators);
    }

    @Override
    public void addFile(File file) {
        if (!files.contains(file)) {
            files.add(file);
        }
    }

    @Override
    public void removeFile(File file) {
        File toRemove = null;
        for (File f : files) {
            if (f.equals(file)) {
                toRemove = f;
            }
        }
        
        if (toRemove != null) {
            files.remove(toRemove);
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public File toFile(File parent) {
        File f = new File(parent, "notes-analysis_"+id+".xml");
        try {
            f.createNewFile();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        return f;
    }

    @Override
    public Configuration fromFile(File file) {
        String fileName = file.getName();

        if (fileName.contains("_") && fileName.contains(".")) {
            String name = fileName.substring(fileName.indexOf("_")+1, fileName.lastIndexOf("."));
            NotesAnalysisConfig config = new NotesAnalysisConfig ();
            config.id = name;
            return config;
        }
        return null;
    }

    @Override
    public void setupAnalysis(File stageFolder, ProjectOrganization org, Participant participant) {
        this.stageFolder = stageFolder;
        this.org = org;
        this.participant = participant;
    }

    @Override 
    public void startAnalysis() {
        notesRecorders = new ArrayList<>();
        String projectPath = org.getLocation().toString();
        String relativeCaptureFilePath = "";
        File file;
        FileDescription desc;
        List<File> files = new ArrayList<>();
        for(NotesVisualization visualizable : visualizables) {
            relativeCaptureFilePath = new File(projectPath).toURI().relativize(new File(visualizable.getFilePath()).toURI()).getPath();
            file = findFileCreatedFor(stageFolder, relativeCaptureFilePath, visualizable.getConfiguration());
            recorder = new NotesRecorder(stageFolder, this);
            
            if (file == null) {
                recorder.createFile();
            } else {
                recorder.setFile(file);
            }

            file = recorder.getFile();
            desc = new FileDescription(file, "mo.analysis.NotesRecorder", relativeCaptureFilePath, visualizable.getConfiguration());
            notesRecorders.add(recorder);
        }

        List<File> generatedFiles = new ArrayList<>();
        for(NotesVisualization playable : playables) {
            relativeCaptureFilePath = new File(projectPath).toURI().relativize(new File(playable.getFilePath()).toURI()).getPath();
            file = findFileCreatedFor(stageFolder, relativeCaptureFilePath, playable.getConfiguration());
            recorder = new NotesRecorder(stageFolder, this);

            if (file == null) {
                recorder.createFile();
            } else {
                recorder.setFile(file);
            }
            file = recorder.getFile();
            desc = new FileDescription(file, "mo.analysis.NotesRecorder", relativeCaptureFilePath, playable.getConfiguration());
            notesRecorders.add(recorder);

            generatedFiles = findFilesCreatedFor(stageFolder, playable.getConfiguration(), "mo.analysis.NotesRecorder", relativeCaptureFilePath);

            for (File f : generatedFiles) {
                recorder = new NotesRecorder(stageFolder,this);
                recorder.setFile(f);
                notesRecorders.add(recorder);
            }
        }
    }

    @Override
    public void cancelAnalysis() {
        Thread.currentThread().interrupt();
    }

    @Override
    public Playable getPlayer() {
        if (player == null) {
            player = new NotesPlayer(notesRecorders);
        }

        return player;
    }
}
