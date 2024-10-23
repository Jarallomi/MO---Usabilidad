package mo.communication;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mo.core.I18n;
import mo.core.ui.WizardDialog;

public class FinishConnectionWizardPanel extends JPanel {
    WizardDialog wizard;
    private final JTextField ipField;
    private final JTextField portTCPField;
    private final JTextField portUDPField;
    private I18n inter;
    
    public FinishConnectionWizardPanel(WizardDialog wizard) throws UnknownHostException {
        this.wizard = wizard;
        inter = new I18n(NewConnectionWizardPanel.class);
        super.setName(inter.s("ServerConfigurationWizardPanel.serverConfigurationStep"));
        super.setLayout(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        //this.wizard.setTextFinishButton(inter.s("ServerConfigurationWizardPanel.buttonConnect"));
        
        JLabel ipAddressLabel = new JLabel("XDD");
        ipField = new JTextField();
        ipField.setEditable(false);
        ipField.setText(InetAddress.getLocalHost().getHostAddress());
        ipField.setHorizontalAlignment(JTextField.CENTER);

        
        JLabel portTCPLabel = new JLabel(inter.s("ServerConfigurationWizardPanel.serverConfigurationPortTCP"));
        portTCPField = new JTextField();
        portTCPField.setHorizontalAlignment(JTextField.CENTER);
        portTCPField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
//                updateState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
//                updateState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
//                updateState();
            }
        });
        
        JLabel portUDPLabel = new JLabel(inter.s("ServerConfigurationWizardPanel.serverConfigurationPortUDP"));
        portUDPField = new JTextField();
        portUDPField.setHorizontalAlignment(JTextField.CENTER);
        portUDPField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
//                updateState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
//                updateState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
//                updateState();
            }
        });
        
//        JLabel portRTPLabel = new JLabel(inter.s("ServerConfigurationWizardPanel.serverConfigurationPortRTP"));
//        portRTPField = new JTextField();
//        portRTPField.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                updateState();
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                updateState();
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                updateState();
//            }
//        });
        
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 5, 2, 5);
        c.anchor = GridBagConstraints.LINE_START;
        super.add(ipAddressLabel, c);
        
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(10, 5, 2, 5);
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        super.add(ipField, c);
        
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.insets = new Insets(2, 5, 2, 5);
        c.fill = GridBagConstraints.NONE;
        super.add(portTCPLabel, c);
        
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        super.add(portTCPField, c);
        
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        c.insets = new Insets(2, 5, 2, 5);
        c.fill = GridBagConstraints.NONE;
        super.add(portUDPLabel, c);
        
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        super.add(portUDPField, c);
        
//        c.gridx = 0;
//        c.gridy = 3;
//        c.weightx = 0;
//        c.insets = new Insets(2, 5, 2, 5);
//        c.fill = GridBagConstraints.NONE;
//        super.add(portRTPLabel, c);
//        
//        c.gridx = 1;
//        c.gridy = 3;
//        c.weightx = 1;
//        c.fill = GridBagConstraints.HORIZONTAL;
//        super.add(portRTPField, c);
        
        c.gridx = 0;
        c.gridy = 4;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 3;
        c.ipadx = 50; //450
        c.ipady = 0; //150
        super.add(new JLabel(""), c);
        
        wizard.setWarningMessage(inter.s("ServerConfigurationWizardPanel.warningFailConnection"));
        
        
        /*
        CONFIGURACION DIRECTS
        
        */
        JButton configDevsButton = new JButton("Configurar dispositivos");
        configDevsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                setupDevs();
            }
        });

        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 0.1;
        c.insets = new Insets(2, 5, 2, 5);
        c.fill = GridBagConstraints.NONE;
        super.add(configDevsButton, c);
    }
}
