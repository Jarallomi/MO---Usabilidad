package mo.core.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

public class Utils {
    
    private static final Logger logger = Logger.getLogger(Utils.class.getName());

    public static void centerOnScreen(final Component c) {
        final int width = c.getWidth();
        final int height = c.getHeight();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width / 2) - (width / 2);
        int y = (screenSize.height / 2) - (height / 2);
        c.setLocation(x, y);
    }
    
    public static ImageIcon createImageIcon(String path, Class clazz) {
        java.net.URL imgURL = clazz.getClassLoader().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            logger.log(Level.WARNING, String.format("Couldn't find file: %s", path));
            return null;
        }
    }
    
    
    public static boolean validateHTTP_URI(String uri) {
        final URL url;
        try {
            url = new URL(uri);
        } catch (Exception e1) {
            return false;
        }
        return "http".equals(url.getProtocol()) || "https".equals(url.getProtocol());
    }
    
    
    public static String cleanServerUrl(String url){
        
        if(url == null ) return "";
        
        String result = url.trim();
        if(result.charAt(result.length() - 1) == '/'){
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
    
    public static HashMap<String, Object> parseJson(String json){        
        try{
            HashMap<String, Object> map = new HashMap<String, Object>();
            ObjectMapper mapper = new ObjectMapper();
            map = mapper.readValue(json, new TypeReference<HashMap<String, Object>>(){});
            return map;
        } catch(IOException e){
            return new HashMap<String, Object>();
        }
    }
    
    public static ArrayList<HashMap<String, Object>> parseArrayJson(String json){        
        try{
            ArrayList<HashMap<String, Object>> map = new ArrayList<HashMap<String, Object>>();
            ObjectMapper mapper = new ObjectMapper();
            map = mapper.readValue(json, new TypeReference<ArrayList<HashMap<String, Object>>>(){});
            return map;
        } catch(IOException e){
            return new ArrayList<HashMap<String, Object>>();
        }
    }
}
