/* This DisplayPanel class works with the original version of MO */

package mo.keyboard.visualization;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class DisplayPanel2{

    private final TextArea text;
    private final VBox internal;
    private final Scene scene;

    public DisplayPanel2() {
        text = new TextArea();
        text.setEditable(false);
        text.setWrapText(true);
        text.setText("");

        internal = new VBox();
        internal.getChildren().add(text);

        scene = new Scene(internal, 600, 150);
    }
    
    public VBox getPanel(){
        return internal;
    }

    public Scene getScene() {
        return scene;
    }

    public void display(KeyboardEvent event) {
        if (event.type.equals(KeyboardEventType.NATIVE_KEY_TYPED)) {
            if (event.rawCode == 8) {
                String currentText = text.getText();
                if (!currentText.isEmpty()) {
                    text.setText(currentText.substring(0, currentText.length() - 1));
                }
            } else {
                text.appendText(Character.toString(event.keyChar));
            }
        }
    }

    public void clear() {
        text.clear();
    }
}
