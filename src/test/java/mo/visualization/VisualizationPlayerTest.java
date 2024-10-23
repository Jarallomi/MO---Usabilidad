package mo.visualization;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import mo.organization.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class VisualizationPlayerTest {
    
    private DummyVisConfig c;
    ArrayList<VisualizableConfiguration> configs;
    
    public VisualizationPlayerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        c = new DummyVisConfig();
        configs = new ArrayList<>();
        configs.add(c);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testSeek() {
        System.out.println("seek");
        
        VisualizationPlayer instance = new VisualizationPlayer(configs);
        
        long millis = ThreadLocalRandom.current().nextLong(
                instance.getStart(), instance.getEnd());
        
        instance.seek(millis);
        
        assertEquals(c.getCurrentTime(), instance.getCurrentTime());
    }

    /**
     * Test of play method, of class VisualizationPlayer.
     * @throws java.lang.InterruptedException
     */
//    @Test
//    public void testPlay() throws InterruptedException {
//        System.out.println("play");
//        VisualizationPlayer instance = new VisualizationPlayer(configs);
//        instance.play();
//
//        Thread.sleep(5);
//        
//        instance.pause();
//
//        long t = instance.getCurrentTime();
//        assertTrue("time was not " + t, t > 0);
//
//    }

    @Test
    public void testGetStart() {
        System.out.println("getStart");
        VisualizationPlayer instance = new VisualizationPlayer(configs);
        long result = instance.getStart();
        assertEquals(c.getStart(), result);
    }

    @Test
    public void testGetEnd() {
        System.out.println("getEnd");
        VisualizationPlayer instance = new VisualizationPlayer(configs);
        long result = instance.getEnd();
        assertEquals(c.getEnd(), result);
    }

//    @Test
//    public void testPause() throws InterruptedException {
//        System.out.println("pause");
//        VisualizationPlayer instance = new VisualizationPlayer(configs);
//        instance.play();
//        Thread.sleep(5);
//        instance.pause();
//        long t = instance.getCurrentTime();
//        long t2 = c.getCurrentTime();
//        
//        assertTrue(
//                "DummyPlugin time (" + t2 + ") was not the player time (" + t + ")", 
//                t2 == t);
//    }
    
}
