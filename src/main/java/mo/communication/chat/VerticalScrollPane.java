package mo.communication.chat;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.border.Border;

public class VerticalScrollPane extends JPanel implements Scrollable {

        private static final long serialVersionUID = 7477168367035025136L;

        public VerticalScrollPane() {
            this(new GridLayout(0, 1));
        }

        public VerticalScrollPane(LayoutManager lm) {
            super(lm);
        }

        public VerticalScrollPane(Component comp) {
            this();
            add(comp);
//            ((JPanel)comp).add(this);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect,
                int orientation, int direction) {
            return 10;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect,
                int orientation, int direction) {
            return 100;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
        
        

    }