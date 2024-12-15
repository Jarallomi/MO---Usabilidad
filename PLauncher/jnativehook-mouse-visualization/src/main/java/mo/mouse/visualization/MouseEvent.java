package mo.mouse.visualization;

public class MouseEvent {

    int x, y, clickCount, button;
    long time;
    MouseEventType type;

    @Override
    public String toString() {
        return time + " (" + x + "," + y + ") " + type.name() + " " + button;
    }

}
