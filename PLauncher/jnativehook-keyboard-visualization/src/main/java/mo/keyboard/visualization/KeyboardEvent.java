package mo.keyboard.visualization;

public class KeyboardEvent {
    
    long time;
    KeyboardEventType type;
    int keyCode;
    String keyText;
    char keyChar;
    int rawCode;
    KeyLocation keyLocation;
    String modifiers;

    @Override
    public String toString() {
        return String.format("%d %s %d %s %c %d %s %s", new Object[]{time, type, keyCode, keyText, keyChar, rawCode, keyLocation, modifiers});
    }
    
}
