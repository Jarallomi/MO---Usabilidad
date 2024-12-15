package mo.mouse.visualization;

import java.awt.Dimension;
import java.io.File;
import java.net.URISyntaxException;
import org.junit.*;
import static org.junit.Assert.*;

public class MousePlayerTest {
    
    File file;
    String fileName = "mouseCaptureExample.txt";
    
    public MousePlayerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws URISyntaxException {
        file = new File(this.getClass().getResource(fileName).toURI());
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testSeek() {
        System.out.println("seek");
        long desiredMillis = 1491166497417L;
        MousePlayer instance = new MousePlayer(file);
        instance.seek(desiredMillis);
        assertEquals(1491166497417L, instance.getCurrentTime());
    }


    @Test
    public void testGetStart() {
        System.out.println("getStart");
        MousePlayer instance = new MousePlayer(file);
        long expResult = 1491166494729L;
        long result = instance.getStart();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetEnd() {
        System.out.println("getEnd");
        MousePlayer instance = new MousePlayer(file);
        long expResult = 1491166500209L;
        long result = instance.getEnd();
        assertEquals(expResult, result);
    }
    
}
