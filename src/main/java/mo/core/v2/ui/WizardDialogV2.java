/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.core.v2.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import mo.core.v2.model.Organization;

/**
 *
 * @author Francisco
 */
public class WizardDialogV2 extends JDialog {
    
    private ArrayList<JPanel> panels;
    private final JPanel cards, stepsPanel;
    private final ArrayList<JLabel> stepsLabels;
    private HashMap<String, Object> result;
    private final JButton backButton;
    private final JButton nextButton;
    private final JButton finishButton;
    private final JLabel warningLabel;
    private final JLabel stepTitleLabel;
    private int currentStepIndex = 0;

    private List<WizardListener> listeners;

    Font defFont = (new JLabel()).getFont();
    Font boldFont = new Font(defFont.getName(), Font.BOLD, defFont.getSize());
    
    public WizardDialogV2(JFrame parent, String title, Organization model) {
        super(parent, title);
        super.setModalityType(ModalityType.APPLICATION_MODAL);
        this.panels = new ArrayList<>();
        result = new HashMap<>();
        stepsLabels = new ArrayList<>();
        listeners = new ArrayList<>();

        //setLayout(new BoxLayout(dialogPanel, BoxLayout.LINE_AXIS));
        JLabel stepsLabel = new JLabel("Steps", JLabel.LEFT);
        stepsLabel.setFont(boldFont);

        stepsLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JSeparator sep = new JSeparator();
        sep.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        stepsPanel = new JPanel();
        stepsPanel.setBackground(Color.WHITE);
        stepsPanel.setLayout(new BoxLayout(stepsPanel, BoxLayout.Y_AXIS));
        stepsPanel.add(stepsLabel);
        stepsPanel.add(sep);

        JPanel externalStepsPanel = new JPanel();
        externalStepsPanel.setBackground(Color.WHITE);
        externalStepsPanel.add(stepsPanel);

        backButton = new JButton("< Back");
        backButton.setEnabled(false);
        backButton.addActionListener((ActionEvent e) -> {
            showPrevPanel();
        });

        nextButton = new JButton("Next >");
        nextButton.setEnabled(false);
        nextButton.addActionListener((ActionEvent e) -> {
            showNextPanel();
        });

        finishButton = new JButton("Finish");
        finishButton.setEnabled(false);
        finishButton.addActionListener((ActionEvent e) -> {
            setVisible(false);
            dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((ActionEvent e) -> {
            result = null;
            setVisible(false);
            dispose();
        });

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
        //buttonsPanel.setBorder(new EmptyBorder(20, 10, 10, 10));
        buttonsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(20, 10, 10, 10)));
        buttonsPanel.add(Box.createGlue());
        buttonsPanel.add(backButton);
        buttonsPanel.add(Box.createHorizontalStrut(10));
        buttonsPanel.add(nextButton);
        buttonsPanel.add(Box.createHorizontalStrut(10));
        buttonsPanel.add(finishButton);
        buttonsPanel.add(Box.createHorizontalStrut(10));
        buttonsPanel.add(cancelButton);

        stepTitleLabel = new JLabel("A Title");
        stepTitleLabel.setFont(boldFont);
        stepTitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JSeparator stepTitleSep = new JSeparator();
        stepTitleSep.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel panelForTitle = new JPanel();
        panelForTitle.setLayout(new BoxLayout(panelForTitle, BoxLayout.Y_AXIS));
        panelForTitle.add(stepTitleLabel);
        panelForTitle.add(stepTitleSep);

        cards = new JPanel(new CardLayout());

        warningLabel = new JLabel("A warning");
        warningLabel.setForeground(Color.red);
        warningLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        mainPanel.add(panelForTitle, BorderLayout.NORTH);

        mainPanel.add(cards, BorderLayout.CENTER);
        mainPanel.add(warningLabel, BorderLayout.SOUTH);

        //JPanel stepsPanel = new JPanel();
        Container contentPane = super.getContentPane();
        contentPane.add(externalStepsPanel, BorderLayout.WEST);
        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(buttonsPanel, BorderLayout.PAGE_END);
        //contentPane.add(buttons, BorderLayout.PAGE_END);

        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                result = null;
            }

            @Override
            public void windowClosing(WindowEvent e) {
                result = null;
            }
        });
    }
    
    private void notifyListeners() {
        for (WizardListener listener : listeners) {
            listener.onStepChanged();
        }
    }

    public void addPanel(JPanel panel) {

        if (panels == null) {
            panels = new ArrayList<>();
        }

        panels.add(panel);
    }

    public void addPanels(Collection<JPanel> panelsToAdd) {

        if (panels == null) {
            panels = new ArrayList<>();
        }

        panels.addAll(panelsToAdd);
    }

    public HashMap showWizard(int panelIndex) {
        for (int i = 0; i < panels.size(); i++) {
            JPanel p = panels.get(i);
            cards.add(p.getName(), p);

            JLabel label = new JLabel((i + 1) + ". " + p.getName());
            label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            stepsLabels.add(label);
            stepsPanel.add(stepsLabels.get(i));
        }
        stepsPanel.add(Box.createVerticalGlue());
        showPanel(panelIndex);
        //setStepTitle("1.  "+panels.get(0).getName());
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        return result;
    }

    public HashMap showWizard() {
        return showWizard(0);
    }

    public void addResult(String k, Object v) {
        if (result == null) {
            result = new HashMap<>();
        }

        if (result.containsKey(k)) {
            result.replace(k, v);
        } else {
            result.put(k, v);
        }
    }

    public void removeResult(String k) {
        if (result != null) {
            result.remove(k);
        }
    }

    public void nullResult() {
        result = null;
    }

    public void enableNext() {
        nextButton.setEnabled(true);
    }

    public void enableBack() {
        backButton.setEnabled(true);
    }

    public void enableFinish() {
        finishButton.setEnabled(true);
        SwingUtilities.getRootPane(finishButton).setDefaultButton(finishButton);
    }

    public void disableNext() {
        nextButton.setEnabled(false);
    }

    public void disableBack() {
        backButton.setEnabled(false);
    }

    public void disableFinish() {
        finishButton.setEnabled(false);
    }
    
    public void setTextFinishButton(String name) {
        finishButton.setText(name);
    }

    public void showPanel(int index) {
        if (index < 0) {
            currentStepIndex = 0;
        } else if (index >= panels.size()) {
            currentStepIndex = panels.size() - 1;
        }

        for (int i = 0; i < stepsLabels.size(); i++) {
            if (i != currentStepIndex) {
                stepsLabels.get(i).setFont(defFont);
            } else {
                stepsLabels.get(i).setFont(boldFont);
            }
        }

        CardLayout cl = (CardLayout) (cards.getLayout());
        cl.show(cards, panels.get(currentStepIndex).getName());

        stepTitleLabel.setText((currentStepIndex + 1) + ".  " + panels.get(currentStepIndex).getName());
    }

    public void showNextPanel() {
        int old = currentStepIndex;
        incrementCurrentStepIndex();
        showPanel(currentStepIndex);
        if (old != currentStepIndex) {
            notifyListeners();
        }
    }

    public void showPrevPanel() {
        int old = currentStepIndex;
        decrementCurrentStepIndex();
        showPanel(currentStepIndex);
        if (old != currentStepIndex) {
            notifyListeners();
        }
    }

    public void setWarningMessage(String message) {
        warningLabel.setText(message);
    }

    public void setStepTitle(String title) {
        stepTitleLabel.setText(title);
    }

    private void incrementCurrentStepIndex() {
        currentStepIndex++;
        if (currentStepIndex >= panels.size()) {
            currentStepIndex--;
        }
    }

    private void decrementCurrentStepIndex() {
        currentStepIndex--;
        if (currentStepIndex < 0) {
            currentStepIndex = 0;
        }
    }

    public int getCurrentStep() {
        return currentStepIndex;
    }

    public void addActionListener(WizardListener listener) {
        listeners.add(listener);

    }
    
    public interface WizardListener {

        void onStepChanged();
    }
}
