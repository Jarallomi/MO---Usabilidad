package mo.communication.chat;

import java.util.HashMap;

public class ChatEntry {
        public String name;
        public String content;

        // For type 0=sent, 1=received.
        public int type;

        public ChatEntry(String name, String content, int type) {
            this.name = name;
            this.content = content;
            this.type = type;
        }
        
        public ChatEntry(HashMap<String,Object> map, int type) {
            this.name = (String)map.get("name");
            this.content = (String)map.get("msg");
            this.type = type;
        }
    }