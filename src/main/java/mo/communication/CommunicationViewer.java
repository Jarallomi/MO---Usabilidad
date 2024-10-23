/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.communication;

import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.core.ui.GridBConstraints;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.DockablesRegistry;
import mo.core.ui.dockables.IDockableElementProvider;

@Extension(
        xtends = {
            @Extends(
                    extensionPointId = "mo.core.ui.dockables.IDockableElementProvider"
            )
        }
)
public class CommunicationViewer implements IDockableElementProvider{
    
    //PANEL
    private DockableElement dockable;
    private JPanel mainPanel;
    
    private static CommunicationViewer cv;
    boolean registered = false;
    
    private CommunicationViewer(){
        
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout()); 
        
        
        GridBConstraints c = new GridBConstraints();
        c.f(GridBConstraints.HORIZONTAL).gx(0).gy(0).wx(1).wy(0.1);
        
        c.f(GridBConstraints.BOTH);
        
        dockable = new DockableElement("CommunicationViewer");
        dockable.add(mainPanel);
    }
    
    public static CommunicationViewer getInstance(){
        if(cv == null)
            cv = new CommunicationViewer();
        return cv;
    }
    
    public void establishedConnection() {
        if (!registered) {
            DockablesRegistry dr = DockablesRegistry.getInstance();
            dr.addAppWideDockable(dockable);
            registered = true;
        }
    }

    @Override
    public DockableElement getElement() {
        return dockable;
    }

    @Override
    public String getDockableGroup() {
        return null;
    }
}
