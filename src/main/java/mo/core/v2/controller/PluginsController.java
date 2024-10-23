/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mo.core.v2.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import mo.core.v2.model.PluginViewerV2;

/**
 * FXML Controller class
 *
 * @author Francisco
 */
public class PluginsController implements Initializable {

    @FXML
    private AnchorPane pluginsPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        final SwingNode swingNode = new SwingNode();   
        
        createSwingContent(swingNode);
        
        //swingNode.setContent(new JButton("dddf"));
        
        pluginsPane.getChildren().add(swingNode);
    }    

    private void createSwingContent(SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                PluginViewerV2 test = new PluginViewerV2();
                swingNode.setContent(test.mainPanel);
            }
        });
    }
    
}
