package mo.communication.streaming.capture;

import java.io.Serializable;
import java.util.HashMap;

public class CaptureConfig implements Serializable{
    private static final long serialVersionUID = 5950169519310163575L;
    String configID;
    String creator;
    HashMap<String,Object> initialConfig;

    public CaptureConfig(String creator, String configID, HashMap<String, Object> initialConfig) {
        this.creator = creator;
        this.configID = configID;
        this.initialConfig = initialConfig;
    }

    public String getCreator() {
        return creator;
    }

    public String getConfigID() {
        return configID;
    }

    public HashMap<String, Object> getInitialConfig() {
        return initialConfig;
    }
    
    @Override
    public String toString() {
        return "CaptureConfig{" + "creator=" + creator + ", configID=" + configID + ", initialConfig=" + initialConfig + '}';
    }
}
