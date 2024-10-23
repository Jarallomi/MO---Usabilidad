package mo.communication.chat;

import mo.communication.CommunicationConfiguration;
import mo.communication.CommunicationProvider1;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.organization.Configuration;

@Extension(
        xtends = {
            @Extends(extensionPointId = "mo.communication.CommunicationProvider1")
        }
)
public class ChatPlugin implements CommunicationProvider1{

    CommunicationConfiguration config;
    @Override
    public String getName() {
        return "Chat";
    }

    @Override
    public CommunicationConfiguration initNewConfiguration(String id) {
        config = new ChatWindow(id);
        return config;
    }
}
