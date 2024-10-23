/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.core.plugin.gui;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *
 * @author felo
 */
public class Title extends JLabel {
    
    Title(String title){
        
        super(title, SwingConstants.LEFT);
        
        setFont(new Font("", Font.BOLD, 20));
        Dimension d = getPreferredSize();
        d.height = 25;
        setPreferredSize(d);
        
    }
    
}
