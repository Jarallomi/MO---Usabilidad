/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.core.v2.Vizualization;

import java.util.ArrayList;
import javax.swing.JPanel;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.DockablesRegistry;
import mo.core.v2.model.Organization;
import mo.organization.Configuration;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;
import mo.organization.StageAction;
import mo.organization.StageModule;
import mo.organization.StagePlugin;
import mo.visualization.VisualizationDialog2;
import mo.visualization.VisualizationPlayer;

/**
 *
 * @author Francisco
 */
public class VisualizeActionV2 implements StageAction {
    
    Organization model;
    
    public VisualizeActionV2(Organization org){
        this.model = org;
    }
    
    @Override
    public String getName() {
        return "Visualize";
    }

    @Override
    public void init(ProjectOrganization organization, Participant participant, StageModule stage) {
        ArrayList<Configuration> configs = new ArrayList<>();
        for (StagePlugin plugin : stage.getPlugins()) {
            for (Configuration configuration : plugin.getConfigurations()) {
                configs.add(configuration);
            }
        }
        VisualizationDialogV2 d = new VisualizationDialogV2(configs, organization.getLocation(), model);
        boolean accept = d.show();
        if (accept) {
            VisualizationPlayer p = new VisualizationPlayer(d.getConfigurations());
            //p.getDockable().setVisible(true);
            DockablesRegistry.getInstance()
                    .addDockableInProjectGroup(
                            organization.getLocation().getAbsolutePath(),
                            p.getDockable());
        }
    }
}
