package mo.core.plugin.gui;

import mo.core.ui.Utils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author felo
 */
public class RemotePluginInstallerTest {
    
    @Test
    public void cleansUrlCorrectly(){

        RemotePluginInstaller r = new RemotePluginInstaller();        
      
        assertEquals(Utils.cleanServerUrl("http://localhost:3000"), "http://localhost:3000");
        assertEquals(Utils.cleanServerUrl("http://localhost:3000/"), "http://localhost:3000");
        
        assertEquals(Utils.cleanServerUrl("   http://localhost:3000"), "http://localhost:3000");
        assertEquals(Utils.cleanServerUrl("http://localhost:3000/   "), "http://localhost:3000");
        assertEquals(Utils.cleanServerUrl("   http://localhost:3000   "), "http://localhost:3000");
        
        assertEquals(Utils.cleanServerUrl("https://localhost:3000"), "https://localhost:3000");
        assertEquals(Utils.cleanServerUrl("https://localhost:3000/"), "https://localhost:3000");


    }
    
}
