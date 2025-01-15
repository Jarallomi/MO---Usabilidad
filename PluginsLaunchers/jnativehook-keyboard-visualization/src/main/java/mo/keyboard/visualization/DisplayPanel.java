package mo.keyboard.visualization;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class DisplayPanel {
    
    private final TextArea text;
    private final VBox internal;
    private Scene scene;
    

    public DisplayPanel() {
        text = new TextArea();
        text.setEditable(false);
        text.setWrapText(true);
        text.setText("");

        internal = new VBox();
        internal.getChildren().add(text);
        
    }
    
    public Scene getScene() {
        return scene;
    }

    public VBox getPanel() {
        return internal;
    }
    public void display(KeyboardEvent event) {
        if (event.type == KeyboardEventType.NATIVE_KEY_TYPED) {
            String currentText = text.getText();
            if (event.keyChar != ' ' || !currentText.endsWith(" ")) {
                text.appendText(Character.toString(event.keyChar));
            }
        }
    }



    public void clear() {
        text.clear();
    }
}
