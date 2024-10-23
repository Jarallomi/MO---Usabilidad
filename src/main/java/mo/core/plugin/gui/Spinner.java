package mo.core.plugin.gui;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import mo.core.ui.Utils;

/**
 *
 * @author felo
 */
public class Spinner extends JLabel {
    
    private int completedSteps;
    private int totalSteps;
    
    Spinner(int steps){
        totalSteps = steps;
        ImageIcon spinner = Utils.createImageIcon("images/spinner.gif", getClass());      
        this.setIcon(spinner);   
        this.setVisible(false);
    }
    
    public synchronized void startLoading(){        
        completedSteps = 0;
        this.setVisible(true);        
    }
    
    public synchronized void completeLoad(){
        completedSteps = 0;
        this.setVisible(false);
    }
    
    public synchronized void completeStep(){
        
        if(completedSteps == totalSteps) return;
        
        completedSteps++;
        
        if(completedSteps == totalSteps){
            this.setVisible(false);            
        }
        
    }
    
}
