package mo.communication;

import mo.core.filemanagement.project.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import mo.core.I18n;
import mo.core.MultimodalObserver;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.core.preferences.AppPreferencesWrapper;
import mo.core.preferences.PreferencesManager;
import mo.core.ui.WizardDialog;
import mo.core.ui.menubar.IMenuBarItemProvider;
import mo.core.filemanagement.FileRegistry;
import mo.core.plugin.Plugin;
import mo.core.plugin.PluginRegistry;
import mo.core.ui.dockables.DockableElement;
import mo.core.ui.dockables.IDockableElementProvider;
import static mo.core.ui.menubar.MenuItemLocations.UNDER;
import mo.organization.StagePlugin;
import mo.visualization.VisualizationProvider;
import mo.visualization.VisualizeAction;

@Extension(
        xtends = {
            @Extends(
                    extensionPointId = "mo.core.ui.menubar.IMenuBarItemProvider"
            )
        }
)
public class CommunicationManagement implements IMenuBarItemProvider{

    private JMenu connectionMenu;
    private JMenuItem newConnection, openProject, closeProject;
    private I18n inter;
    

    public CommunicationManagement() {
        inter = new I18n(CommunicationManagement.class);
        
        connectionMenu = new JMenu();
        connectionMenu.setName("connection");
        connectionMenu.setText(inter.s("CommunicationManagement.communicationMenu"));
        
        newConnection = new JMenuItem();
        newConnection.setName("new connection...");
        newConnection.setText(inter.s("CommunicationManagement.newServerMenuItem"));
        
//        openProject = new JMenuItem();
//        openProject.setName("open project...");
//        openProject.setText(inter.s("ProjectManagement.openProjectMenuItem"));

        newConnection.addActionListener(this::newConnection);

//        openProject.addActionListener((ActionEvent e) -> {
//            openProject();
//        });

        connectionMenu.add(newConnection);
//        projectMenu.add(openProject);
    }

    private void saveProjectInAppPreferences(Project project) {
        PreferencesManager pm = new PreferencesManager();
        AppPreferencesWrapper app = (AppPreferencesWrapper) pm.loadOrCreate(AppPreferencesWrapper.class, new File(MultimodalObserver.APP_PREFERENCES_FILE));
        app.addOpenedProject(project.getFolder().getAbsolutePath());
        pm.save(app, new File(MultimodalObserver.APP_PREFERENCES_FILE));
        //System.out.println(app);
    }

    
    private List<StagePlugin> plugins;
    private void newConnection(ActionEvent e) {
        if(!ServerConnection.getInstance().getConnectionState()){
            WizardDialog w = new WizardDialog(
                null, inter.s("ServerConfigurationWizardPanel.serverConfigurationWizardTitle"));
            try {
                w.addPanel(new NewConnectionWizardPanel(w));
            } catch (UnknownHostException ex) {
                Logger.getLogger(CommunicationManagement.class.getName()).log(Level.SEVERE, null, ex);
            }
            HashMap<String, Object> result = w.showWizard();
            if (result != null) {
                System.out.println("MANAGEMENT RECIBIO \n"+result);
    //            int udpPort = Integer.parseInt((String) result.get("portUDP"));
    //            int tcpPort = Integer.parseInt((String) result.get("portTCP"));
                //int rtpPort = Integer.parseInt((String) result.get("portRTP"));
    //            ServerConnection.getInstance().setPortTCP(tcpPort).setPortUDP(udpPort);//.setPortRTP(rtpPort);
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        if(!ServerConnection.getInstance().upServer(result)){
                            JOptionPane.showMessageDialog(null, "No se pudo establecer conexión, intente nuevamente");
                            return;
                        }
                        ServerConnection.getInstance().waitForClients();
                        JOptionPane.showMessageDialog(null, "Servidor configurado correctamente");
                    }
                }).start();
            }
        }else{
//            WizardDialog w = new WizardDialog(
//                null, inter.s("ServerConfigurationWizardPanel.serverConfigurationWizardTitle"));
//                try {
//                    w.addPanel(new FinishConnectionWizardPanel(w));
//                } catch (UnknownHostException ex) {
//                    Logger.getLogger(CommunicationManagement.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                HashMap<String, Object> result = w.showWizard();
//                if (result != null) {
//                    
//                }
//            Object[] selections = { "First", "Second", "Third" };
//            Object val = JOptionPane.showInputDialog(null, "Choose one\nXD\nxda\nxdw",
//                "Input", JOptionPane.INFORMATION_MESSAGE, null,
//                selections, selections[0]);
//            if (val != null)
//              System.out.println(val.toString());
            String msg = "Actualmente online." + "\n" + 
                    "IP:   " + ServerConnection.getInstance().getLocalIP() + "\n" +
                    "Port: " + ServerConnection.getInstance().getPortTCP();
//            int result = JOptionPane.showConfirmDialog(null, msg,
//                    "Server configuration", JOptionPane.OK_CANCEL_OPTION);
            
                int result = JOptionPane.showOptionDialog(null, 
                                msg, 
                                "Server configuration", 
                                JOptionPane.OK_CANCEL_OPTION, 
                                JOptionPane.INFORMATION_MESSAGE, 
                                null, 
                                new String[]{"Disconnect", "Cancel"}, // this is the array
                                "default");
                if(result == JOptionPane.OK_OPTION){
                    try {
                        ServerConnection.getInstance().downServer();
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(CommunicationManagement.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            
            
            
            
            
//            if(result == JOptionPane.OK_OPTION){
//                try {
//                    ServerConnection.getInstance().downServer();
//                } catch (ClassNotFoundException ex) {
//                    Logger.getLogger(CommunicationManagement.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
                
        }
            
        
    }
    
    @Override
    public JMenuItem getItem() {
        return connectionMenu;
    }

    @Override
    public int getRelativePosition() {
        return UNDER;
    }

    @Override
    public String getRelativeTo() {
        return "file";
    }
}
