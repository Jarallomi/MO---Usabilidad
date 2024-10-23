package mo.communication;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PetitionResponse  implements Serializable{
    private static final long serialVersionUID = 5950169519310163575L;
    private String type;
    private HashMap<String, Object> map;
    
    public PetitionResponse(String type, HashMap<String, Object> map){
        this.type = type;
        this.map = map;
    }

    public String getType() {
        return type;
    }

    public HashMap<String, Object> getHashMap() {
        return map;
    }

    @Override
    public String toString() {
        String s = "type: " + this.type + "\nData: " + this.map;
        return s;
    }
}
