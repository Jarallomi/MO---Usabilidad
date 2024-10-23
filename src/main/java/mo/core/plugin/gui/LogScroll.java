package mo.core.plugin.gui;

import java.awt.Point;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author felo
 */
public class LogScroll extends JScrollPane {
    
    
    private JTextArea textArea;
    
    LogScroll(){     
        
        super();
        
        textArea = new JTextArea(15, 30);      
        textArea.setOpaque(false);
        textArea.setLineWrap(true);
        textArea.setText("");
        textArea.setEditable(false);
        
        this.setViewportView(textArea);
    }
    
    public void addLine(String text){        
        textArea.append(text + "\n");
        
        this.getViewport().setViewPosition(new Point(0, textArea.getDocument().getLength()));

    }
    
    
    
}
