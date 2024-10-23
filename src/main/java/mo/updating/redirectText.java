package mo.updating;

import java.io.IOException;
import java.io.OutputStream;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class redirectText extends OutputStream {
    private final TextArea textArea;

    public redirectText(TextArea textArea) {
        this.textArea = textArea;
    }

    /**
     * Metodo que permite la redireccion del texto de la salida estandar a la interfaz gráfica
     */
    @Override
    public void write(int b) throws IOException {
        // Redirige un byte a la TextArea
        Platform.runLater(() -> textArea.appendText(String.valueOf((char) b)));
    }
}
