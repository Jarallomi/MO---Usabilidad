package mo.communication;

import mo.core.filemanagement.project.*;
import mo.core.Utils;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mo.core.I18n;
import mo.core.ui.WizardDialog;

public class DevicesConfigurationWizardPanel extends JPanel {
    WizardDialog wizard;
    private ArrayList<JTextField> portsFields;
    private ArrayList<JCheckBox> checkBoxes;
    private ArrayList<JComboBox> comboBoxes;
    private ArrayList<JLabel> nameDevicesLabels;
    private I18n inter;
    
    public DevicesConfigurationWizardPanel(WizardDialog wizard) throws UnknownHostException {
        this.wizard = wizard;
        inter = new I18n(DevicesConfigurationWizardPanel.class);
        super.setName("Seleccionar dispositivos");
        super.setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        //this.wizard.setTextFinishButton(inter.s("ServerConfigurationWizardPanel.buttonConnect"));
        
        
        
        
        
        
        checkBoxes = new ArrayList<>();
        comboBoxes = new ArrayList<>();
        portsFields = new ArrayList<>();
        nameDevicesLabels = new ArrayList<>();
        
        int x = 0, y = 0;
        
        for(CommunicationProvider cp : ServerConnection.getInstance().getDirectDevs().values()){
            JLabel deviceName = new JLabel(cp.getName());
            JCheckBox enableDevice = new JCheckBox();
            JTextField portDevice = new JTextField();
            JComboBox listDevices = new JComboBox();
            
            
            deviceName.setText(cp.getName());
            enableDevice.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    listDevices.setEnabled(!listDevices.isEnabled());
                    portDevice.setEnabled(!portDevice.isEnabled());
                    updateState();
                }
            });
            
            System.out.println(cp.getName());
            if(cp.getDevices()!= null){
                for(String item : cp.getDevices()){
                    listDevices.addItem(item);
                }
            }
            else{
                listDevices.setVisible(false);
            }
            listDevices.setEnabled(false);
            
            portDevice.setEnabled(false);
            portDevice.setHorizontalAlignment(JTextField.CENTER);
            portDevice.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateState();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateState();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateState();
                }
            });
            
            
            
            //label
            c.gridx = 0;
            c.gridy = y;
            c.insets = new Insets(10, 5, 2, 5);c.weightx = 1;
            c.anchor = GridBagConstraints.LINE_START;
            super.add(deviceName, c);
            nameDevicesLabels.add(deviceName);
            
            //label PORTS
            c.gridx = 1;
            c.gridy = y;
            c.insets = new Insets(10, 5, 2, 5);c.weightx = 1;
            c.anchor = GridBagConstraints.LINE_START;
            super.add(new JLabel("Port:"), c);

            //checkbox
            c.gridx = 0;
            c.gridy = y+1;
            c.insets = new Insets(10, 5, 2, 5);
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            super.add(listDevices, c);
            comboBoxes.add(listDevices);
            
            
            //combo box
            c.gridx = 1;
            c.gridy = y+1;
            c.insets = new Insets(10, 5, 2, 5);
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            super.add(portDevice, c);
            portsFields.add(portDevice);
            
            
            //textfield
            c.gridx = 2;
            c.gridy = y+1;
            c.insets = new Insets(10, 5, 2, 5);
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            super.add(enableDevice, c);
            checkBoxes.add(enableDevice);

            
            y = y + 3;
            
        }
        //System.out.println("HAY "+ServerConnection.getInstance().countDirectDevs()+" dispositivos directos");
        
 
        
        
        c.gridx = 0;
        c.gridy = y+1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 3;
        c.ipadx = 290; //450
        c.ipady = 0; //150
        super.add(new JLabel(""), c);
        
        wizard.setWarningMessage(inter.s("ServerConfigurationWizardPanel.warningFailConnection"));
        
        
    }
    
    private boolean validatePort(final String port) {
        try {
            if(port == null || port.equals("")) return false;
            return 1024 < Integer.parseInt(port) && Integer.parseInt(port) < 10000;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private void updateState(){
        wizard.nullResult();
        for(JCheckBox dev : checkBoxes){
            if(dev.isSelected()){
                int i = checkBoxes.indexOf(dev);
                if(!validatePort(portsFields.get(i).getText()) || portsFields.get(i).getText() == null){
                    wizard.setWarningMessage("warning msg");
                    wizard.nullResult();
                    wizard.disableFinish();
                    return;
                }
                
                // VER QUE NO HAYAN REPETIDOS
            }
        }
        
//        if(){
//            wizard.addResult("localIP", ipField.getText());
        boolean thereIsSelectedDevices = false;
        for(JCheckBox dev : checkBoxes){
            if(dev.isSelected()){
                int i = checkBoxes.indexOf(dev);
                wizard.addResult(nameDevicesLabels.get(i).getText()+" PORT", portsFields.get(i).getText());
                if(comboBoxes.get(i).isVisible()){
                    wizard.addResult(nameDevicesLabels.get(i).getText()+" DEVICE", comboBoxes.get(i).getSelectedItem().toString());
                }
                thereIsSelectedDevices = true;
            }
        }
        
        if(thereIsSelectedDevices){
            wizard.setWarningMessage("");
            wizard.enableFinish();
        }
        else{
            wizard.setWarningMessage("warning msg");
            wizard.nullResult();
            wizard.disableFinish();
        }
        
//        }
    }
    

}
