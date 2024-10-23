package mo.visualization;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import mo.core.ui.GridBConstraints;
import org.apache.commons.lang3.time.FastDateFormat;

public class PlayerControlsPanel {

    private JPanel panel;
    private JSlider slider;
    private JButton play;
    private JCheckBox CheckSync;
    private JLabel LabelSync;
    private JLabel currentTime;
    private JLabel ellapsedTLabel;
    private GridBConstraints gbc;

    private boolean sliderMovedProgrammatically;
    private boolean sync=false;

    private final static String ELLAPSED_FORMAT = "%02d:%02d:%02d:%1d";
    private final FastDateFormat timeF = FastDateFormat.getInstance("yyyy-MM-dd  HH:mm:ss:SSS");

    private final VisualizationPlayer player;

    private static final String PLAY_SYMBOL = "\u25B6";
    private static final String PAUSE_SYMBOL = "||"; //"\u23F8";
    private static final String STOP_SYMBOL = "\u25A0";

    public PlayerControlsPanel(VisualizationPlayer player) {

        this.player = player;

        panel = new JPanel(new GridBagLayout());

        SwingUtilities.invokeLater(() -> {
            gbc = new GridBConstraints();
            System.out.println(player.getEnd()+" --- "+player.getStart());
            slider = new JSlider(0, (int) (player.getEnd() - player.getStart()), 0);
            slider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    if (!slider.getValueIsAdjusting()) {
                        if (sliderMovedByUser()) {
                            sliderMoved();
                        }
                    }
                }

                private boolean sliderMovedByUser() {
                    return !sliderMovedProgrammatically;
                }
            });
            gbc.f(GridBagConstraints.HORIZONTAL);
            gbc.i(new Insets(5, 5, 5, 5)).wx(1);
            panel.add(slider, gbc.gw(5));

            play = new JButton(PLAY_SYMBOL);
            play.addActionListener((ActionEvent e) -> {
                playPressed();
            });
            CheckSync = new JCheckBox();
            CheckSync.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
               if(CheckSync.isSelected()){
                   setSync(true);
               }
               else{
                   setSync(false);
               }
            }

            });
            panel.add(play, gbc.gy(1).gw(1).wx(0));

            ellapsedTLabel = new JLabel("00:00:00:000");
            currentTime = new JLabel("2016-10-15 00:00:00:0");
            LabelSync = new JLabel("Sync");
            panel.add(LabelSync,gbc.gx(1));
            panel.add(CheckSync,gbc.gx(2));
            panel.add(ellapsedTLabel, gbc.gx(3));

            panel.add(currentTime, gbc.gx(4));

        });
    }

    public void setSync(boolean sync){
        this.sync = sync;
    }

    public boolean getSync(){
        return sync;
    }

    private void sliderMoved() {
        int val = slider.getValue();
        long current = player.getStart() + val;
        setTime(current);
        player.seek(current);
    }

    private void playPressed() {
        if (player.isPlaying()) {
            play.setText(PLAY_SYMBOL);
            player.pause();
        } else {
            play.setText(PAUSE_SYMBOL);
            player.play();
            player.sync(sync);
        }
    }

    public void setTime(long time) {
        SwingUtilities.invokeLater(() -> {
            int ellapsed = (int) (time - player.getStart());
            long millis = (ellapsed % 1000) / 100;
            long second = (ellapsed / 1000) % 60;
            long minute = (ellapsed / (1000 * 60)) % 60;
            long hour = (ellapsed / (1000 * 60 * 60)) % 24;
            ellapsedTLabel.setText(
                    String.format(ELLAPSED_FORMAT, hour, minute, second, millis));

            Date d = new Date(time);

            currentTime.setText(timeF.format(d));

            sliderMovedProgrammatically = true;
            slider.setValue(ellapsed);
            sliderMovedProgrammatically = false;

            if (player.getCurrentTime() == player.getEnd()) {
                stop();
            }
        });
    }

    public JPanel getPanel() {
        return panel;
    }

    public void stop() {
        play.setText(PLAY_SYMBOL);
    }
}
