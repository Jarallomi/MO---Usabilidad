package mo.communication;

public interface ConnectionSender {
    public void subscribeListener(ConnectionListener c);
    public void unsubscribeListener(ConnectionListener c);
}
