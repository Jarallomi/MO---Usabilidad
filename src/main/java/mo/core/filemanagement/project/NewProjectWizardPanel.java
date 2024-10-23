package mo.core.filemanagement.project;

import mo.core.Utils;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mo.core.I18n;
import mo.core.ui.WizardDialog;

public class NewProjectWizardPanel extends JPanel {
    WizardDialog wizard;
    JFileChooser fileChooser;
    private final JTextField locationField;
    private final JTextField nameField;
    private final JTextField folderField;
    private I18n inter;
    
    public NewProjectWizardPanel(WizardDialog wizard) {
        this.wizard = wizard;
        inter = new I18n(NewProjectWizardPanel.class);
        super.setName(inter.s("NewProjectWizardPanel.newProjectWizardStep"));
        super.setLayout(new GridBagLayout());
        
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        GridBagConstraints c = new GridBagConstraints();
        
        JLabel nameLabel = new JLabel(inter.s("NewProjectWizardPanel.projectNameLabel"));
        nameField = new JTextField();
        nameField.getDocument().addDocumentListener(new DocumentListener() {
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
        
        JLabel locationLabel = new JLabel(inter.s("NewProjectWizardPanel.projectLocationLabel"));
        locationField = new JTextField();
        locationField.setText(System.getProperty("user.dir"));
        locationField.getDocument().addDocumentListener(new DocumentListener() {
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
        
        JLabel folderLabel = new JLabel(inter.s("NewProjectWizardPanel.projectFolderLabel"));
        folderField = new JTextField(locationField.getText());
        folderField.setEditable(false);
        
        JButton browseBtn = new JButton(inter.s("NewProjectWizardPanel.browseLabel"));
        browseBtn.addActionListener(this::selectLocation);
        
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 5, 2, 5);
        c.anchor = GridBagConstraints.LINE_START;
        super.add(nameLabel, c);
        
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(10, 5, 2, 5);
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        super.add(nameField, c);
        
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.insets = new Insets(2, 5, 2, 5);
        c.fill = GridBagConstraints.NONE;
        super.add(locationLabel, c);
        
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        super.add(locationField, c);
        
        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        super.add(browseBtn, c);
        
        c.gridx = 0;
        c.gridy = 2;
        super.add(folderLabel, c);
        
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        super.add(folderField, c);
        
        c.gridy = 3;
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 3;
        c.ipadx = 450;
        c.ipady = 150;
        super.add(new JLabel(""), c);
        
        wizard.setWarningMessage(inter.s("NewProjectWizardPanel.warningName"));
    }
    
    
    private void selectLocation(ActionEvent e) {
        fileChooser.setCurrentDirectory(new File(Utils.getBaseFolder()));
        int returnVal = fileChooser.showOpenDialog(fileChooser);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            locationField.setText(file.getPath());
            //This is where a real application would open the file.
            updateState();
        } else {

        }
    }
    
    private void updateState(){
        File folder = new File(locationField.getText());
        File newFolder = new File(locationField.getText()+"/"+nameField.getText());
        folderField.setText(newFolder.getPath());
        if (nameField.getText().isEmpty()){
            wizard.setWarningMessage(inter.s("NewProjectWizardPanel.warningName"));
            wizard.nullResult();
            wizard.disableFinish();
        } else if (locationField.getText().isEmpty()) {
            wizard.setWarningMessage(inter.s("NewProjectWizardPanel.warningLocation"));
            wizard.nullResult();
            wizard.disableFinish();
        } else if (!folder.isDirectory()) {
            wizard.setWarningMessage(inter.s("NewProjectWizardPanel.warningLocationExistence"));
            wizard.nullResult();
            wizard.disableFinish();
        } else if (newFolder.exists()){
            wizard.disableFinish();
            wizard.setWarningMessage(inter.s("NewProjectWizardPanel.warningLocationExists"));
            wizard.nullResult();
        } else {
            wizard.addResult("projectFolder", newFolder.getPath());
            wizard.setWarningMessage("");
            wizard.enableFinish();
        }
    }
}
