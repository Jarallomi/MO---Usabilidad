package mo.keyboard.visualization;

public enum KeyLocation {
    KEY_LOCATION_LEFT,
    KEY_LOCATION_NUMPAD,
    KEY_LOCATION_RIGHT,
    KEY_LOCATION_STANDARD,
    KEY_LOCATION_UNKNOWN;
    
    public static KeyLocation getKeyLocationFromString(String s) {
        KeyLocation l;
        switch (s) {

            case "KEY_LOCATION_STANDARD":
                l = KEY_LOCATION_STANDARD;
                break;

            case "KEY_LOCATION_NUMPAD":
                l = KEY_LOCATION_NUMPAD;
                break;

            case "KEY_LOCATION_LEFT":
                l = KEY_LOCATION_LEFT;
                break;

            case "KEY_LOCATION_RIGHT":
                l = KEY_LOCATION_RIGHT;
                break;

            default:
                l = KEY_LOCATION_UNKNOWN;
                break;
        }
        return l;
    }
}
