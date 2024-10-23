package mo.core.plugin;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author felo
 */
public class ExtPointTest {
    
    ExtPoint ext;
    Plugin A, B, C, D, E;
    
    @Before
    public void setUp() {
        ext = new ExtPoint();        
        A = new Plugin();
        A.setId("A");        
        B = new Plugin();
        B.setId("B");
        C = new Plugin();
        C.setId("C");        
        D = new Plugin();
        D.setId("D");        
        E = new Plugin();
        E.setId("E");        
        ext.addPlugin(A);
        ext.addPlugin(B);
        ext.addPlugin(C);
        ext.addPlugin(D);
        ext.addPlugin(E);
    }
    
    
    @Test
    public void testAdd(){
        assertEquals(ext.getPlugins().size(), 5);
        assertEquals(ext.getPlugins().get(0), A);
        assertEquals(ext.getPlugins().get(1), B);
        assertEquals(ext.getPlugins().get(2), C);
        assertEquals(ext.getPlugins().get(3), D);
        assertEquals(ext.getPlugins().get(4), E);
    }
    
    
    @Test
    public void testRemovePlugin(){       
        
        ext.removePlugin(A);
        assertEquals(ext.getPlugins().size(), 4);
        assertEquals(ext.getPlugins().get(0), B);
        assertEquals(ext.getPlugins().get(1), C);
        assertEquals(ext.getPlugins().get(2), D);
        assertEquals(ext.getPlugins().get(3), E);      
        
        ext.removePlugin(E);
        assertEquals(ext.getPlugins().size(), 3);
        assertEquals(ext.getPlugins().get(0), B);
        assertEquals(ext.getPlugins().get(1), C);
        assertEquals(ext.getPlugins().get(2), D);
        
        ext.removePlugin(C);
        assertEquals(ext.getPlugins().size(), 2);
        assertEquals(ext.getPlugins().get(0), B);
        assertEquals(ext.getPlugins().get(1), D);
        
        ext.removePlugin(C);
        assertEquals(ext.getPlugins().size(), 2);
        assertEquals(ext.getPlugins().get(0), B);
        assertEquals(ext.getPlugins().get(1), D);
        
        ext.removePlugin(B);
        assertEquals(ext.getPlugins().size(), 1);
        assertEquals(ext.getPlugins().get(0), D);
        
        ext.removePlugin(D);
        assertEquals(ext.getPlugins().size(), 0);
        
    }
    
}
