package mo.visualization;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import mo.core.ui.GridBConstraints;
import mo.core.ui.Utils;
import mo.organization.Configuration;

public class VisualizationDialog extends JDialog {

    ArrayList<JCheckBox> checkBoxs;
    List<VisualizableConfiguration> configurations;
    JButton nextButton;

    public VisualizationDialog(List<Configuration> configs) {
        super(null, "Visualize", JDialog.ModalityType.APPLICATION_MODAL);

        setLayout(new GridBagLayout());
        GridBConstraints g = new GridBConstraints();

        configurations = new ArrayList<>();
        checkBoxs = new ArrayList<>();
        for (Configuration configuration : configs) {
            JCheckBox c = new JCheckBox(configuration.getId());
            checkBoxs.add(c);
            add(c, g);
            c.putClientProperty("configuration", configuration);
            c.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    updateState();
                }
            });
        }


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                configurations = null;
            }
        });
        
        
    }

    List<VisualizableConfiguration> showDialog() {
        setMinimumSize(new Dimension(400, 150));
        setPreferredSize(new Dimension(400, 300));
        pack();
        Utils.centerOnScreen(this);
        setVisible(true);
        return configurations;
    }

    private void updateState() {

        for (JCheckBox checkBox : checkBoxs) {
            VisualizableConfiguration rc
                    = (VisualizableConfiguration) checkBox.getClientProperty("configuration");
            if (checkBox.isSelected()) {
                if (!configurations.contains(rc)) {
                    configurations.add(rc);
                }
            } else if (configurations.contains(rc)) {
                configurations.remove(rc);
            }
        }

        if (configurations.size() > 0) {
            nextButton.setEnabled(true);
        }
        
    }

}
