package mo.core.plugin;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author felo
 */
public class PluginDataTest {
    
    PluginData pluginData;
    static Plugin p1, p2, p3, p4, p5, p6, p12, p22, p32;
    
    static ExtPoint ext1, ext2, ext3, ext4, ext5;
    static Dependency dep1, dep2, dep3, dep4, dep5;
    
    
    
    @Before
    public void setUp() {
        pluginData = new PluginData();
        
        p1 = new Plugin();
        p2 = new Plugin();        
        p3 = new Plugin();
        p4 = new Plugin();
        p5 = new Plugin();
        
        p12 = new Plugin();
        p22 = new Plugin();
        p32 = new Plugin();
        
        p1.setId("plugin-1");
        p1.setVersion("0.0.1");   
        
        p2.setId("plugin-2");
        p2.setVersion("2.0.1");    
        
        p3.setId("plugin-3");
        p3.setVersion("0.2.1"); 
        
        p4.setId("plugin-4");
        p4.setVersion("2.0.1");
        
        p5.setId("plugin-5");
        p5.setVersion("3.2.1");
        
        p12.setId("plugin-1");
        p12.setVersion("0.0.2");
        
        p22.setId("plugin-2");
        p22.setVersion("3.0.1");   
        
        p32.setId("plugin-3");
        p32.setVersion("0.3.1");
        
        pluginData.addExtensionPoint(ext1);
        pluginData.addExtensionPoint(ext2);
        pluginData.addExtensionPoint(ext3);
        pluginData.addExtensionPoint(ext4);
        pluginData.addExtensionPoint(ext5);
        
    }
    
    @BeforeClass
    public static void initData(){        
        
        
        ext1 = new ExtPoint();
        ext1.setId("extension-1");
        ext1.setVersion("1.0.0");
        
        ext2 = new ExtPoint();
        ext2.setId("extension-1");
        ext2.setVersion("1.0.1");
        
        ext3 = new ExtPoint();
        ext3.setId("extension-2");
        ext3.setVersion("1.0.0");
        
        ext4 = new ExtPoint();
        ext4.setId("extension-2");
        ext4.setVersion("1.1.0");
        
        ext5 = new ExtPoint();
        ext5.setId("extension-2");
        ext5.setVersion("5.5.5");
        
        dep1 = new Dependency();
        dep1.setId("extension-1");
        
        dep2 = new Dependency();
        dep2.setId("extension-2");
        
        dep3 = new Dependency();
        dep3.setId("extension-3");
        
        dep4 = new Dependency();
        dep4.setId("extension-4");
        
        dep5 = new Dependency();
        dep5.setId("extension-5");
        
    }
    
    
    @Test
    public void testPluginIsRegistered_NoVersion(){
        
        pluginData.addPlugin(p1);
        assertEquals(pluginData.pluginIsRegistered(p1), true);
        assertEquals(pluginData.pluginIsRegistered(p2), false);
        assertEquals(pluginData.pluginIsRegistered(p3), false);
        assertEquals(pluginData.pluginIsRegistered(p4), false);
        assertEquals(pluginData.pluginIsRegistered(p5), false);
        
        pluginData.addPlugin(p2);
        assertEquals(pluginData.pluginIsRegistered(p1), true);
        assertEquals(pluginData.pluginIsRegistered(p2), true);
        assertEquals(pluginData.pluginIsRegistered(p3), false);
        assertEquals(pluginData.pluginIsRegistered(p4), false);
        assertEquals(pluginData.pluginIsRegistered(p5), false);
        
        pluginData.addPlugin(p4);
        assertEquals(pluginData.pluginIsRegistered(p1), true);
        assertEquals(pluginData.pluginIsRegistered(p2), true);
        assertEquals(pluginData.pluginIsRegistered(p3), false);
        assertEquals(pluginData.pluginIsRegistered(p4), true);
        assertEquals(pluginData.pluginIsRegistered(p5), false);
        
        pluginData.addPlugin(p5);
        assertEquals(pluginData.pluginIsRegistered(p1), true);
        assertEquals(pluginData.pluginIsRegistered(p2), true);
        assertEquals(pluginData.pluginIsRegistered(p3), false);
        assertEquals(pluginData.pluginIsRegistered(p4), true);
        assertEquals(pluginData.pluginIsRegistered(p5), true);
        
        pluginData.addPlugin(p3);
        assertEquals(pluginData.pluginIsRegistered(p1), true);
        assertEquals(pluginData.pluginIsRegistered(p2), true);
        assertEquals(pluginData.pluginIsRegistered(p3), true);
        assertEquals(pluginData.pluginIsRegistered(p4), true);
        assertEquals(pluginData.pluginIsRegistered(p5), true);      

    }
    
    
    @Test
    public void testPluginIsRegistered_YesVersion(){
        
        pluginData.addPlugin(p1);
        assertEquals(pluginData.pluginIsRegistered(p1, "0.0.0"), false);
        assertEquals(pluginData.pluginIsRegistered(p1, "0.0.1"), true);
        assertEquals(pluginData.pluginIsRegistered(p1, "0.0.2"), false);
        
        pluginData.addPlugin(p2);
        assertEquals(pluginData.pluginIsRegistered(p1, "0.0.0"), false);
        assertEquals(pluginData.pluginIsRegistered(p1, "0.0.1"), true);
        assertEquals(pluginData.pluginIsRegistered(p1, "0.0.2"), false);
        assertEquals(pluginData.pluginIsRegistered(p2, "2.0.0"), false);
        assertEquals(pluginData.pluginIsRegistered(p2, "2.0.1"), true);
        assertEquals(pluginData.pluginIsRegistered(p2, "2.0.2"), false);

    }
    
    @Test
    public void testPluginIsRegistered_VersionUpdate_Minor(){
        
        pluginData.addPlugin(p1);
        assertEquals(pluginData.pluginIsRegistered(p1, "0.0.0"), false);
        assertEquals(pluginData.pluginIsRegistered(p1, "0.0.1"), true);
        assertEquals(pluginData.pluginIsRegistered(p1, "0.0.2"), false);
        
        // Version 0.0.1 gets removed after adding 0.0.2
        pluginData.addPlugin(p12);
        assertEquals(pluginData.pluginIsRegistered(p1, "0.0.0"), false);
        assertEquals(pluginData.pluginIsRegistered(p1, "0.0.1"), false);
        assertEquals(pluginData.pluginIsRegistered(p12, "0.0.2"), true);
        assertEquals(pluginData.pluginIsRegistered(p1), false);
        assertEquals(pluginData.pluginIsRegistered(p12), true);   

        assertEquals(pluginData.getPlugins().size(), 1);
    }
    
    @Test
    public void testPluginIsRegistered_VersionUpdate_Middle(){
        
        pluginData.addPlugin(p3);
        assertEquals(pluginData.pluginIsRegistered(p3, "0.2.0"), false);
        assertEquals(pluginData.pluginIsRegistered(p3, "0.2.1"), true);
        assertEquals(pluginData.pluginIsRegistered(p3, "0.2.2"), false);
        
        pluginData.addPlugin(p32);
        assertEquals(pluginData.pluginIsRegistered(p3, "0.2.0"), false);
        assertEquals(pluginData.pluginIsRegistered(p3, "0.2.1"), true);
        assertEquals(pluginData.pluginIsRegistered(p32, "0.3.1"), true);
        assertEquals(pluginData.pluginIsRegistered(p3), true);
        assertEquals(pluginData.pluginIsRegistered(p32), true);
        
        assertEquals(pluginData.getPlugins().size(), 2);
    }
    
    @Test
    public void testPluginIsRegistered_VersionUpdate_Major(){
        
        pluginData.addPlugin(p2);
        assertEquals(pluginData.pluginIsRegistered(p2, "2.0.0"), false);
        assertEquals(pluginData.pluginIsRegistered(p2, "2.0.1"), true);
        assertEquals(pluginData.pluginIsRegistered(p2, "2.0.2"), false);
        
        // On major version updates, 2.0.1 won't get deleted after adding 3.0.1
        pluginData.addPlugin(p22);
        assertEquals(pluginData.pluginIsRegistered(p2, "2.0.0"), false);
        assertEquals(pluginData.pluginIsRegistered(p2, "2.0.1"), true);
        assertEquals(pluginData.pluginIsRegistered(p22, "3.0.1"), true);
        assertEquals(pluginData.pluginIsRegistered(p2), true);
        assertEquals(pluginData.pluginIsRegistered(p22), true);
        
        assertEquals(pluginData.pluginIsRegistered(p2, "2.0.1"), true);
        assertEquals(pluginData.pluginIsRegistered(p22, "3.0.1"), true);
        
        assertEquals(pluginData.getPlugins().size(), 2);
    }
    
    
    @Test
    public void testPluginRemoved(){
        
        pluginData.addPlugin(p1);
        assertEquals(pluginData.pluginIsRegistered(p1), true);
        pluginData.unregisterPlugin(p1);
        assertEquals(pluginData.pluginIsRegistered(p1), false);
        
    }
    
    @Test
    public void testPluginRemoved_OnlyOneVersion(){
        
        pluginData.addPlugin(p1);
        pluginData.addPlugin(p12);
        assertEquals(pluginData.pluginIsRegistered(p1), false);
        assertEquals(pluginData.pluginIsRegistered(p12), true);
        pluginData.unregisterPlugin(p12);
        assertEquals(pluginData.pluginIsRegistered(p1), false);
        assertEquals(pluginData.pluginIsRegistered(p12), false);        
    }
    
    @Test
    public void testPluginRemoved_OnlyOneVersion2(){
        
        pluginData.addPlugin(p2);
        pluginData.addPlugin(p22);
        assertEquals(pluginData.pluginIsRegistered(p2), true);
        assertEquals(pluginData.pluginIsRegistered(p22), true);
        pluginData.unregisterPlugin(p2);
        assertEquals(pluginData.pluginIsRegistered(p2), false);
        assertEquals(pluginData.pluginIsRegistered(p22), true);        
    }
    
    @Test
    public void testPluginRemoved_OnlyOneVersion3(){
        
        pluginData.addPlugin(p3);
        pluginData.addPlugin(p32);
        assertEquals(pluginData.pluginIsRegistered(p3), true);
        assertEquals(pluginData.pluginIsRegistered(p32), true);
        pluginData.unregisterPlugin(p3);
        assertEquals(pluginData.pluginIsRegistered(p3), false);
        assertEquals(pluginData.pluginIsRegistered(p32), true);        
    }
    
    
    
    @Test
    public void testExtPoints(){
        
        p1.addDependency(dep1);
        p1.addDependency(dep3);
        p2.addDependency(dep1);        
   
        pluginData.addPlugin(p1);
        pluginData.addPlugin(p2);
        
        pluginData.checkDependencies();        
        
        assertEquals(pluginData.getExtPoints().size(), 2);
        assertEquals(pluginData.getExtPoints().get(0).getId(), "extension-1");
        assertEquals(pluginData.getExtPoints().get(1).getId(), "extension-2");
       
        List<Plugin> pluginsFor = pluginData.getPluginsFor(dep1.getExtensionPoint().getId());
        
        assertEquals(pluginsFor.size(), 2);        
        
    }
    
    @Test
    public void testExtPoints2(){
        
        p1.addDependency(dep1);
        p1.addDependency(dep2);
        p2.addDependency(dep1);        
   
        pluginData.addPlugin(p1);
        pluginData.addPlugin(p2);
        
        pluginData.checkDependencies();        
        
        assertEquals(pluginData.getExtPoints().size(), 2);
       
        List<Plugin> pluginsFor = pluginData.getPluginsFor(dep2.getExtensionPoint().getId());
        
        assertEquals(pluginsFor.size(), 1);
        assertEquals(pluginsFor.get(0).getDependencies().size(), 2);
        
        pluginsFor = pluginData.getPluginsFor(dep1.getExtensionPoint().getId());
        
        assertEquals(pluginsFor.size(), 2);
        assertEquals(pluginsFor.get(0).getDependencies().size(), 2);
        assertEquals(pluginsFor.get(1).getDependencies().size(), 1);
        
        assertEquals(pluginsFor.get(0).getDependencies().get(0).getId(), "extension-1");
        assertEquals(pluginsFor.get(0).getDependencies().get(1).getId(), "extension-2");
        
    }
    
    @Test
    public void testExtPoints3(){
        
        p1.addDependency(dep1);
        p1.addDependency(dep2);
        p1.addDependency(dep3);
        pluginData.addPlugin(p1);
       
        pluginData.checkDependencies();
        
        assertEquals(pluginData.getExtPoints().size(), 2);
        assertEquals(pluginData.getExtPoints().get(0).getId(), "extension-1");
        assertEquals(pluginData.getExtPoints().get(1).getId(), "extension-2");
        
        List<Plugin> pluginsFor = pluginData.getPluginsFor(dep1.getExtensionPoint().getId());
        assertEquals(pluginsFor.size(), 1);
        assertEquals(pluginsFor.get(0).getDependencies().size(), 3);
        assertEquals(pluginsFor.get(0).getDependencies().get(0).getExtensionPoint().getId(), "extension-1");
        assertEquals(pluginsFor.get(0).getDependencies().get(1).getExtensionPoint().getId(), "extension-2");
        assertEquals(pluginsFor.get(0).getDependencies().get(2).getExtensionPoint(), null); 
        
    }
    
    @Test
    public void testExtPoint_Unregister(){
        
        p1.addDependency(dep1);
        p2.addDependency(dep1);
   
        pluginData.addPlugin(p1);
        pluginData.addPlugin(p2);
        
        pluginData.checkDependencies();      
        
        assertEquals(pluginData.getPluginsFor(dep1.getId()).size(), 2);
        
        pluginData.unregisterPlugin(p1);
        assertEquals(pluginData.getPluginsFor(dep1.getId()).size(), 1);
        
        pluginData.unregisterPlugin(p2);
        assertEquals(pluginData.getPluginsFor(dep1.getId()).size(), 0);
       
        
    }
    
    @Test
    public void testExtPoint_Unregister2(){
        
        p1.addDependency(dep1);
        p12.addDependency(dep1);
   
        pluginData.addPlugin(p1);
        pluginData.addPlugin(p12);
        
        pluginData.checkDependencies();      
        
        assertEquals(pluginData.getPluginsFor(dep1.getId()).size(), 1);
        
        pluginData.unregisterPlugin(p1);
        assertEquals(pluginData.getPluginsFor(dep1.getId()).size(), 1);
        
        pluginData.unregisterPlugin(p12);
        assertEquals(pluginData.getPluginsFor(dep1.getId()).size(), 0);
       
        
    }
    
    @Test
    public void testExtPoint_Unregister3(){
        
        p2.addDependency(dep1);
        p22.addDependency(dep1);
   
        pluginData.addPlugin(p2);
        pluginData.addPlugin(p22);
        
        pluginData.checkDependencies();      
        
        assertEquals(pluginData.getPluginsFor(dep1.getId()).size(), 2);
        
        pluginData.unregisterPlugin(p2);
        assertEquals(pluginData.getPluginsFor(dep1.getId()).size(), 1);
        
        pluginData.unregisterPlugin(p22);
        assertEquals(pluginData.getPluginsFor(dep1.getId()).size(), 0);
       
        
    }
    
    
    @Test
    public void testExtPoint_Unregister4(){
        
        p3.addDependency(dep1);
        p32.addDependency(dep1);
   
        pluginData.addPlugin(p3);
        pluginData.addPlugin(p32);
        
        pluginData.checkDependencies();      
        
        assertEquals(pluginData.getPluginsFor(dep1.getId()).size(), 2);
        
        pluginData.unregisterPlugin(p3);
        assertEquals(pluginData.getPluginsFor(dep1.getId()).size(), 1);
        
        pluginData.unregisterPlugin(p32);
        assertEquals(pluginData.getPluginsFor(dep1.getId()).size(), 0);
       
        
    }
    
    
    
    
}
