package mo.core.plugin.gui;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.net.URL;
import javax.swing.JLabel;

/**
 *
 * @author felo
 */
public class Link extends JLabel {
    
    private String href;
       
    private URI uri;
    
    public Link(String msg){
        this(msg, msg);
    }
    
    
    public Link(String msg, String href){

        this.href = href;        
        
        try{
            URL url = new URL(href);
            uri = url.toURI();
        } catch(Exception e){
        }
        
        
        if(uri != null){
            setText("<html><a href=\"\">" + msg + "</a></html>");

            this.addMouseListener(new MouseListener(){
                @Override
                public void mouseClicked(MouseEvent e) { 
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    openWebpage();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });

            this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
        } else {
            setText(msg);
        }
        
  
    }
    
    
    private void openWebpage() {     
      
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    
    
    public String getHref(){
        return this.href;
    }
    
}
