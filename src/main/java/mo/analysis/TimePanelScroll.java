package mo.analysis;

import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JScrollPane;

import java.awt.event.AdjustmentListener;

import java.awt.event.AdjustmentEvent;

public class TimePanelScroll extends JScrollPane implements AdjustmentListener {
	private BufferedImage bufferedImage;
	private Graphics2D g2d;
	private int height;
	private int width;
	private Dimension panelDimension;
    private JScrollBar horizontalScrollBar;

	public TimePanelScroll() {
		bufferedImage = new BufferedImage(100,100, BufferedImage.TYPE_INT_ARGB);
		g2d = bufferedImage.createGraphics();
	}

	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);

	    Dimension panelDimension = getSize();
	    width = (int) panelDimension.getWidth();
	    height = (int) panelDimension.getHeight();
        g.setColor(Color.red);
        g.fillRect(0, 0, width, height);
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
    }

}