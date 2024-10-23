package mo.visualization.gamelooptest;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import mo.core.ui.GridBConstraints;

public class PlayerPanel extends JPanel {
    private JSlider slider;
    private JButton playButton;
    private JTextField ellapsed;
    private JTextField current;

    public PlayerPanel() {
        GridBConstraints gbc = new GridBConstraints();
        
        slider = new JSlider();
        playButton = new JButton(">");

    }
    
    
}
