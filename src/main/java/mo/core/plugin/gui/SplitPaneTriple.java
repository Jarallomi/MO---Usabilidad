/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.core.plugin.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author felo
 */
public class SplitPaneTriple extends JSplitPane {
    
    private JSplitPane split2;
    
    SplitPaneTriple(){        
        
        split2 = new JSplitPane();
        setRightComponent(split2);
        
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();

        p1.setBorder(new EmptyBorder(0, 0, 0, 0));
        p2.setBorder(new EmptyBorder(0, 0, 0, 0));
        p3.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        p1.setPreferredSize(new Dimension(200, 300));
        p2.setPreferredSize(new Dimension(350, 300));

        //p3.setPreferredSize(new Dimension(100, 100));
        
    
        p1.setLayout(new BorderLayout());
        p2.setLayout(new BorderLayout());
        p3.setLayout(new BorderLayout());

        setLeft(p1);
        setCenter(p2);
        setRight(p3);        
    }
    
    public void setLeft(Component c){
        setLeftComponent(c);        
    }
    
    public void setCenter(Component c){
        split2.setLeftComponent(c);
    }
    
    public void setRight(Component c){
        split2.setRightComponent(c);
    }
    
    public JPanel getLeft(){
        return (JPanel)this.getLeftComponent();
    }
    
    public JPanel getCenter(){
        return (JPanel)((JSplitPane)this.getRightComponent()).getLeftComponent();
    }
    
    public JPanel getRight(){
        return (JPanel)((JSplitPane)this.getRightComponent()).getRightComponent();
    }
    
    public void setTagSpinner(Spinner sp){        
        getLeft().removeAll();
        sp.startLoading();
        getLeft().add(sp);        
        getLeft().revalidate();
        getLeft().repaint();
    }
    
    public void setPluginSpinner(Spinner sp){       
        getCenter().removeAll();
        sp.startLoading();
        getCenter().add(sp);
        getCenter().revalidate();
        getCenter().repaint();
    }
    
}
