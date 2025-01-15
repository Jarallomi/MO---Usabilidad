package mo.keyboard.visualization;

public enum KeyboardEventType {
    NATIVE_KEY_PRESSED,
    NATIVE_KEY_TYPED,
    NATIVE_KEY_RELEASED,
    NATIVE_KEY_FIRST,
    NATIVE_KEY_LAST,
    UNKNOWN;
    
    public static KeyboardEventType getEventTypeFromString(String s) {
        KeyboardEventType t;
        switch (s) {
            case "NATIVE_KEY_PRESSED":
                t = NATIVE_KEY_PRESSED;
                break;

            case "NATIVE_KEY_TYPED":
                t = NATIVE_KEY_TYPED;
                break;

            case "NATIVE_KEY_RELEASED":
                t = NATIVE_KEY_RELEASED;
                break;

            case "NATIVE_KEY_FIRST":
                t = NATIVE_KEY_FIRST;
                break;

            case "NATIVE_KEY_LAST":
                t = NATIVE_KEY_LAST;
                break;

            default:
                t = UNKNOWN;
                break;
        }
        return t;
    }
}
