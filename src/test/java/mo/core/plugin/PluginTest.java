package mo.core.plugin;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author felo
 */
public class PluginTest {
    
    @Test
    public void testEquality() {
        
        Plugin A = new Plugin();
        A.setId("A");
        A.setName("AAA");
        
        Plugin B = new Plugin();
        B.setId("B");
        B.setName("BBB");
        
        Plugin C = new Plugin();
        C.setId("A");
        C.setName("CCC C CC C C");
        
        assertEquals(A, C);       
        assertEquals(A.equals(C), true);
        
        assertEquals(C, A);       
        assertEquals(C.equals(A), true);        
  
        assertEquals(A.equals(B), false);
        assertEquals(B.equals(C), false);
        assertEquals(B.equals(A), false);
        assertEquals(A.equals(B), false);
        
    }
    
}
