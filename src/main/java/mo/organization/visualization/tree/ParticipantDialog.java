package mo.organization.visualization.tree;

import mo.organization.Participant;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import mo.core.I18n;
import mo.core.ui.GridBConstraints;
import static mo.core.ui.Utils.centerOnScreen;
import mo.organization.ProjectOrganization;
import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class ParticipantDialog extends JDialog implements DocumentListener {

    private Participant participant;

    JTextField idField;
    JTextField nameField;
    JLabel errorLabel;
    JButton accept;
    List<Participant> orgParticipants;

    boolean isParticipantUpdate = false;
    
    private I18n i18n;

    public ParticipantDialog(ProjectOrganization org) {
        this(org, null);
    }

    public ParticipantDialog(ProjectOrganization org, Participant part) {
        
        super(null, "", Dialog.ModalityType.APPLICATION_MODAL);
        i18n = new I18n(ParticipantDialog.class);
        setTitle(i18n.s("ParticipantDialog.titleNew"));
        orgParticipants = org.getParticipants();

        if (part == null) {
            participant = new Participant();
        } else {
            setTitle(i18n.s("ParticipantDialog.titleEdit"));
            participant = part;
            isParticipantUpdate = true;
        }

        setLayout(new GridBagLayout());
        GridBConstraints g = new GridBConstraints();
        g.a(GridBConstraints.FIRST_LINE_START)
                .f(GridBConstraints.BOTH)
                .i(new Insets(5, 5, 5, 5));

        add(new JLabel(i18n.s("ParticipantDialog.id")), g.gx(0).gy(0));
        add(new JLabel(i18n.s("ParticipantDialog.name")), g.gy(1));
        add(new JLabel(i18n.s("ParticipantDialog.date")), g.gy(2));
        add(new JLabel(i18n.s("ParticipantDialog.notes")), g.gy(3));

        idField = new JTextField();

        if (participant.id != null) {
            idField.setText(participant.id);
            idField.setEnabled(false);
        }

        nameField = new JTextField();

        if (participant.name != null) {
            nameField.setText(participant.name);
        }

        JTextArea notesArea = new JTextArea();
        if (participant.notes != null) {
            notesArea.setText(participant.notes);
        }
        notesArea.setRows(7);
        JScrollPane scroll = new JScrollPane(notesArea);

        Properties prop = toProperties(ResourceBundle.getBundle("org.jdatepicker.i18n.Text", Locale.getDefault()));

        UtilDateModel model = new UtilDateModel();
        if (participant.date != null) {
            model.setValue(participant.date);
        } else {
            model.setValue(new Date());
        }
        JDatePanelImpl datePanel = new JDatePanelImpl(model, prop);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateComponentFormatter());

        add(idField, g.gx(1).gy(0).wx(1.0));
        add(nameField, g.gy(1));
        add(datePicker, g.gy(2));
        add(scroll, g.gx(0).gy(4).gw(2));

        if (participant.id == null) {
            errorLabel = new JLabel(i18n.s("ParticipantDialog.idNameExistence"));
        } else {
            errorLabel = new JLabel();
        }

        errorLabel.setForeground(Color.red);
        add(errorLabel, g.gx(0).gy(5).gw(2));

        JPanel buttonsPanel = new JPanel(new GridBagLayout());

        accept = new JButton(i18n.s("ParticipantDialog.accept"));
        accept.addActionListener((ActionEvent e) -> {
            if (!idField.getText().isEmpty()) {
                participant.id = idField.getText();
                participant.name = nameField.getText();
                participant.date = (Date) datePanel.getModel().getValue();
                participant.notes = notesArea.getText();
                setVisible(false);
                dispose();
            } else {

            }
        });
        accept.setEnabled(false);

        JButton cancel = new JButton(i18n.s("ParticipantDialog.cancel"));
        cancel.addActionListener((ActionEvent e) -> {
            participant = null;
            setVisible(false);
            dispose();
        });

        g.clear().i(new Insets(5, 5, 15, 5));
        buttonsPanel.add(accept, g.gx(0).gy(0));
        buttonsPanel.add(cancel, g.gx(1).gy(0).i(new Insets(5, 5, 15, 15)));

        g.clear().gx(0).gy(6).a(GridBConstraints.LAST_LINE_END).wx(1).wy(1).gw(2);
        add(buttonsPanel, g);

        setPreferredSize(new Dimension(300, 400));
        pack();
        centerOnScreen(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                participant = null;
                super.windowClosing(e);
            }

        });

        idField.getDocument().addDocumentListener(this);
        updateState();
    }

    public static Properties toProperties(ResourceBundle resource) {
        Properties result = new Properties();
        Enumeration<String> keys = resource.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            result.put(key, resource.getString(key));
        }
        return result;
    }

    public Participant showDialog() {
        setVisible(true);
        return participant;
    }

    private void updateState() {
        if (idField.getText().isEmpty()) {
            errorLabel.setText(i18n.s("ParticipantDialog.idExistence"));
            accept.setEnabled(false);
        } else if (isParticipantUpdate || isParticipantIdUnique(idField.getText())) {
            errorLabel.setText("");
            accept.setEnabled(true);
            SwingUtilities.getRootPane(accept).setDefaultButton(accept);
        } else {
            errorLabel.setText(i18n.s("ParticipantDialog.idUniqueness"));
            accept.setEnabled(false);

        }
    }

    private boolean isParticipantIdUnique(String id) {
        for (Participant p : orgParticipants) {
            if (p.id.equals(id)) {
                return false;
            }
        }
        return true;
    }

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
}
