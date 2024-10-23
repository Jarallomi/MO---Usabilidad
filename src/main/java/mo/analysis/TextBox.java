package mo.analysis;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.KeyEvent;

import java.awt.event.KeyListener;

public class TextBox extends JPanel  implements KeyListener {
	final private JTextArea textArea;
	final private JScrollPane textAreaScrollPane;

	public TextBox() {
        textArea = new JTextArea(2,1);
        textArea.getDocument().putProperty("filterNewlines", Boolean.TRUE);
        textArea.addKeyListener(this);

        textAreaScrollPane = new JScrollPane(textArea);
        textAreaScrollPane.setPreferredSize(new Dimension(150,30));
        textAreaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		add(textAreaScrollPane);

        this.setBounds(0,0,150,30);
		this.setOpaque(false);
        this.setVisible(false);
	}

	public void showme() {
		setVisible(true);
		requestFocus();
	}

	public void hideme() {
		setVisible(false);
	}

	public void setText(String text) {
		textArea.setText(text);
	}

	public String getText() {
		return textArea.getText();
	}

	@Override
	public void	keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void	keyTyped(KeyEvent e) {}
}