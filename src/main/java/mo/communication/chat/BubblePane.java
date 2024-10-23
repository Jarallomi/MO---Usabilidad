package mo.communication.chat;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class BubblePane extends JTextArea {

        private static final long serialVersionUID = -6113801969569504295L;

        private int radius = 10;
        private int strokeThickness = 3;
        private int padding = strokeThickness / 2;
        private JPanel mParent;

        public BubblePane(JPanel parent, String text) {
            mParent = parent;

            setOpaque(false);
            setLineWrap(true);
            setWrapStyleWord(true);
            setEditable(false);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setText(text);
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(getBackground());
            int x = padding + strokeThickness;
            int width = getWidth() - (strokeThickness * 2);
            int bottomLineY = getHeight() - strokeThickness;
            g2d.fillRect(x, padding, width, bottomLineY);
            g2d.setRenderingHints(new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON));
            g2d.setStroke(new BasicStroke(strokeThickness));
            RoundRectangle2D.Double rect = new RoundRectangle2D.Double(x, padding,
                    width, bottomLineY, radius, radius);
            Area area = new Area(rect);
            g2d.draw(area);


            int lc = countLines(this);
            GridBagLayout gbl = (GridBagLayout) mParent.getLayout();
            GridBagConstraints constraints = gbl.getConstraints(this);
            if (lc == 1) {
                if (constraints.fill == GridBagConstraints.HORIZONTAL) {
                    constraints.fill = GridBagConstraints.NONE;
                    gbl.setConstraints(this, constraints);
                    this.setSize(
                            getFontMetrics(getFont()).stringWidth(getText()) + 
                            this.getBorder().getBorderInsets(this).left +
                            this.getBorder().getBorderInsets(this).right, 
                            getHeight() +
                            this.getBorder().getBorderInsets(this).top + 
                            this.getBorder().getBorderInsets(this).bottom);
                }
            } else {
                if (constraints.fill == GridBagConstraints.NONE) {
                    constraints.fill = GridBagConstraints.HORIZONTAL;
                    gbl.setConstraints(this, constraints);

                }
            }

            super.paintComponent(g);
        }

        private int countLines(JTextArea textArea) {
            AttributedString text = new AttributedString(textArea.getText());
            FontRenderContext frc = textArea.getFontMetrics(textArea.getFont())
                    .getFontRenderContext();
            AttributedCharacterIterator charIt = text.getIterator();
            LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(charIt, frc);
            float formatWidth = (float) textArea.getSize().width;
            lineMeasurer.setPosition(charIt.getBeginIndex());

            int noLines = 0;
            while (lineMeasurer.getPosition() < charIt.getEndIndex()) {
                lineMeasurer.nextLayout(formatWidth);
                noLines++;
            }

            return noLines;
        }
    }