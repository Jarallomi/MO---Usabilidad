/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.core.v2.Analysis;

import com.google.inject.Inject;
import com.google.inject.Injector;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import mo.analysis.AnalyzableConfiguration;
import mo.analysis.AnalyzeAction;
import mo.analysis.NotPlayableAnalyzableConfiguration;
import mo.analysis.NotesPlayer;
import mo.analysis.PlayableAnalyzableConfiguration;
import mo.core.ui.dockables.DockablesRegistry;
import mo.core.v2.model.Organization;
import mo.organization.Configuration;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;
import mo.organization.StageAction;
import mo.organization.StageModule;
import mo.organization.StagePlugin;
import mo.visualization.VisualizableConfiguration;
import mo.visualization.VisualizationPlayer;

/**
 *
 * @author Francisco
 */
public class AnalyzeActionV2 implements StageAction{

    private File storageFolder;
    private ProjectOrganization org;
    private Thread[] threads;
    private JButton cancelButon;
    private JButton okButon;
    private boolean accepted;
    private StagePlugin notesPlugin;

    private Participant participant;

    private boolean remote;

    private long current, start, end;

    private List<AnalyzableConfiguration> analyzableConfigurations;

    private List<PlayableAnalyzableConfiguration> playableConfigurations = new ArrayList<>();
    
    Organization  model;
    
    File storeFile;
    
    private static final Logger logger = Logger.getLogger(AnalyzeAction.class.getName());

    public static void main(String[] args) {
        
    }
    
    public AnalyzeActionV2(Organization model){
        this.remote = false;
        this.model = model;
    }
    
    public AnalyzeActionV2(boolean remote){
        this.remote = remote;
    }
    
    @Override
    public String getName() {
        return "Analyze";
    }
    
    public void initRemoteNotes(){
        
    }

    @Override    
    public void init(ProjectOrganization organization, Participant participant, StageModule stage) {
        if(remote){
            initRemoteNotes();
            return;
        }
        ArrayList<Configuration> configs = new ArrayList<>();
        analyzableConfigurations = new ArrayList<>();
        for (StageModule astage : organization.getStages()) {
            if(astage.getCodeName().equals("visualization")) {
                for(StagePlugin aplugin : astage.getPlugins()) {
                    for(Configuration aconfiguration : aplugin.getConfigurations()) {
                        System.out.println("1");
                        configs.add(aconfiguration);
                    }
                }
            }
        }
        
        this.org = organization;
        this.participant = participant;

        storageFolder = new File(org.getLocation(),"participant-" + participant.id + "/" + stage.getCodeName().toLowerCase());
        storageFolder.mkdirs();
        
        for (StagePlugin plugin : stage.getPlugins()) {
            if(plugin.getName().equals("Notes plugin")) {
                notesPlugin = plugin;
                //continue;
            }
            for (Configuration configuration : plugin.getConfigurations()) {
                configs.add(configuration);
                analyzableConfigurations.add((AnalyzableConfiguration) configuration);
            }
        }
        AnalysisDialogV2 analysisDialog = new AnalysisDialogV2(notesPlugin, configs, organization.getLocation(), model, storeFile);
        boolean accept = analysisDialog.show();
        
        if (accept) {
            List<PlayableAnalyzableConfiguration> playableConfigurations = analysisDialog.getPlayableConfigurations();
            List<NotPlayableAnalyzableConfiguration> notPlayableConfigurations = analysisDialog.getNotPlayableConfigurations();
            List<VisualizableConfiguration> visualizableConfiguration = analysisDialog.getVisualizableConfigurations();
        
            analyzableConfigurations = new ArrayList<>(playableConfigurations);
            analyzableConfigurations.addAll(notPlayableConfigurations);
            
            List<AnalyzableConfiguration> analyzableList = new ArrayList(analyzableConfigurations);
            analyzableList.add(analysisDialog.getNotesConfiguration());
            
            JDialog waitDialog = new JDialog();
            JLabel label = new JLabel("Procesando, por favor espere.");
            
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());

            JPanel buttonsPanel = new JPanel();
            
            cancelButon = new JButton("Cancelar");
            cancelButon.setEnabled(true);
            cancelButon.addActionListener((ActionEvent e) -> {
                accepted = false;
                waitDialog.setVisible(false);
                waitDialog.dispose();

                cancelAnalysis(analyzableList);
            });
            
            okButon = new JButton("Aceptar");
            okButon.setEnabled(false);
            okButon.addActionListener((ActionEvent e) -> {

                accepted = true;
                waitDialog.setVisible(false);
                waitDialog.dispose();

            });
            
            buttonsPanel.add(okButon);
            buttonsPanel.add(cancelButon);

            waitDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            waitDialog.setLocationRelativeTo(null);
            waitDialog.setTitle("Please Wait...");

            panel.add(label, BorderLayout.NORTH);
            panel.add(buttonsPanel, BorderLayout.SOUTH);
            waitDialog.add(panel);

            waitDialog.pack();
            waitDialog.setVisible(false);
            
            SwingWorker<Void, Void> mySwingWorker = new SwingWorker<Void, Void>(){
                @Override
                protected Void doInBackground() throws Exception {
                threads = new Thread[analyzableList.size()];
                int i = 0;
                for (AnalyzableConfiguration config : analyzableList) {
                    threads[i] = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            config.setupAnalysis(storageFolder, org, participant);
                            config.startAnalysis();
                        }
                    });
                    threads[i].start();
                    threads[i].join();
                    i++;
                }
                label.setText("Analisis listo");
                cancelButon.setEnabled(false);
                okButon.setEnabled(true);

                return null;
                }
            };
            
            mySwingWorker.execute();
            waitDialog.setVisible(true);
            
            if(accepted){
                List<VisualizableConfiguration> vlista = (List<VisualizableConfiguration>) (List<?>) analysisDialog.getPlayableConfigurations();
                vlista.addAll(visualizableConfiguration);
                obtainMinAndMaxTime(vlista);
                ((NotesPlayer) analysisDialog.getNotesConfiguration().getPlayer()).setStart(start);
                ((NotesPlayer) analysisDialog.getNotesConfiguration().getPlayer()).setEnd(end);
                vlista.add(analysisDialog.getNotesConfiguration());
                VisualizationPlayer player = new VisualizationPlayer(vlista);
                DockablesRegistry.getInstance().addDockableInProjectGroup(organization.getLocation().getAbsolutePath(),player.getDockable());
            }
        }
    }
    
    public void cancelAnalysis(List<AnalyzableConfiguration> analyzables) {
        for(Thread thread : threads) {
            thread.interrupt();
        }

        for (AnalyzableConfiguration config : analyzables) {
            config.cancelAnalysis();
        }
    }
    
    public void startAnalysis(List<AnalyzableConfiguration> configurations) {
        try {
            for (AnalyzableConfiguration config : configurations) {
                config.setupAnalysis(storageFolder,org,participant);
                config.startAnalysis();
            }
        } catch(Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private void obtainMinAndMaxTime(List<VisualizableConfiguration> configs) {
        long min = Long.MAX_VALUE, max = Long.MIN_VALUE;
        for (VisualizableConfiguration config : configs) {
            if (config.getPlayer().getStart() < min) {
                min = config.getPlayer().getStart();
            }
            if (config.getPlayer().getEnd() > max) {
                max = config.getPlayer().getEnd();
            }
        }
        if (min == Long.MAX_VALUE) {
            min = 0;
        }
        if (max == Long.MIN_VALUE) {
            max = 100000;
        }

        current = start = min;
        end = max;
    }
    
    public File getFile(){
        return this.storeFile;
    }
}
