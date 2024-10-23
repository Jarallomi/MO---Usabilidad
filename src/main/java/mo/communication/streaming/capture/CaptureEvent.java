package mo.communication.streaming.capture;

import java.io.Serializable;

public class CaptureEvent implements Serializable{
    private static final long serialVersionUID = 5950169519310163575L;
    String configId;
    String creator;
    Object content;

    public CaptureEvent(String configId, String creator, Object content) {
        this.configId = configId;
        this.creator = creator;
        this.content = content;
    }

    @Override
    public String toString() {
        return "CaptureEvent{" + "configId=" + configId + ", className=" + creator + ", event=" + content + '}';
    }

    public String getConfigId() {
        return configId;
    }

    public String getCreator() {
        return creator;
    }

    public Object getContent() {
        return content;
    }
}
