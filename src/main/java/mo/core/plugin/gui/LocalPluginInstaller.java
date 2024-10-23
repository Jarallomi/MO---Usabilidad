package mo.core.plugin.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import mo.core.plugin.PluginRegistry;
import mo.core.ui.Utils;

/**
 *
 * @author felo
 */
public class LocalPluginInstaller extends JPanel {
    
   
    
    private boolean confirmPluginAdd(List<File> files){        
       
        int dialogResult = JOptionPane.showConfirmDialog (null, "Add " + files.size() + " plugins?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return dialogResult == JOptionPane.YES_OPTION;
        
    }
    
    
    private void addPlugins(File[] files){        
        addPlugins(Arrays.asList(files));
    }
    
    
    private boolean checkFiles(List<File> files){
    
        for(File file : files){
            
            String msg = null;
            
            if(!file.isFile()){            
                msg = "File " + file.getName() + " doesn't exist.";
            }
            else if(!(file.getName().endsWith(".class") || file.getName().endsWith(".jar"))){
                
                msg = "File " + file.getName() + " doesn't end with .class or .jar. No plugins were added.";
            } else {
                
                Exception error = PluginRegistry.getInstance().checkPlugin(file);
                
                if(error != null){                
                    msg = "File " + file.getName() + " doesn't appear to be a valid plugin. No plugins were added.\n\nError details:\n\n" + error.toString();                
                }
            }
            
            if(msg != null){
            
                JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }            
        }
        
        return true;    
    }
    
    
    private void addPlugins(List<File> files){
        

        // Check files before confirmation dialog box
        if(!checkFiles(files)){
            return;
        }
   
        
        if(confirmPluginAdd(files)){
            
            // Check files again in case they changed during the confirm dialog
            
            if(!checkFiles(files)){
                return;
            }
            
            int success = 0;
            int fails = 0;
            
            for(File f : files){
                try{
                    PluginRegistry.getInstance().copyPluginToFolder(f);
                    success++;
                }catch(IOException e){
                    fails++;
                    e.printStackTrace();
                }                
            }
            
            JOptionPane.showMessageDialog(null, success + " plugins added/updated, " + fails + " errors.", "Results", JOptionPane.INFORMATION_MESSAGE);
            
        }
    }
    

    public LocalPluginInstaller(){        
       
        JPanel dragDropPanel = new JPanel();        

        JPanel dragActive = new JPanel();
        JLabel dragText = new JLabel("");
        dragActive.add(dragText);
        dragActive.setVisible(false);                
        
        JPanel dragInactive = new JPanel();        
        
        ImageIcon interfaceIcon = Utils.createImageIcon("images/dragndrop.png", getClass());        
        JLabel dragDropImage = new JLabel("", interfaceIcon, JLabel.CENTER);
        dragInactive.add(dragDropImage, BorderLayout.CENTER);
        dragInactive.add(new JLabel("Drag and drop here"));

        JButton addPluginBtn = new JButton("Add new plugin", UIManager.getIcon("FileView.fileIcon"));
        addPluginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setMultiSelectionEnabled(true);
                int fcState = fc.showDialog(null, "Select plugin");
                
                if(fcState == JFileChooser.APPROVE_OPTION){
                   
                    addPlugins(fc.getSelectedFiles());
                    
                } else if(fcState == JFileChooser.ERROR_OPTION){
                    // Error
                }
            }
        });

        dragDropPanel.setBorder(BorderFactory.createDashedBorder(null, 2, 3));
        dragDropPanel.setPreferredSize(new Dimension(250, 100));        

        
        dragDropPanel.add(dragInactive, BorderLayout.CENTER);
        dragDropPanel.add(dragActive, BorderLayout.CENTER);        

        dragDropPanel.setDropTarget(new DropTarget() {            
            
            
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                dragActive.setVisible(false);
                dragInactive.setVisible(true);
                try {
                    
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt
                            .getTransferable().getTransferData(
                                    DataFlavor.javaFileListFlavor);
                    
                    
                    addPlugins(droppedFiles);

                } catch (UnsupportedFlavorException | IOException ex) {
                }
            }
            
            @Override
            public synchronized void dragOver(DropTargetDragEvent dtde){
                List<String> files = null;                
                
                Transferable t = dtde.getTransferable();
                if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    try {
                        Object td = t.getTransferData(DataFlavor.javaFileListFlavor);
                        if (td instanceof List) {
                            files = new ArrayList<>();
                            
                            for (Object value : ((List) td)) {
                                if (value instanceof File) {
                                    files.add(((File) value).getName());
                                }
                            }
                        }
                    } catch (UnsupportedFlavorException | IOException ex) {
                    }
                }
                
                if(files == null){
                    return;
                }
                
                dragActive.setVisible(true);
                dragText.setText("Drop " + files.size() + " files...");
                dragInactive.setVisible(false);
                
            }
            
            @Override
            public synchronized void dragExit(DropTargetEvent dte){             
                dragActive.setVisible(false);
                dragInactive.setVisible(true);
            }            
            
        });

                
                
        
        add(addPluginBtn);
        add(new JLabel("or"));
        add(dragDropPanel);
    }
}
