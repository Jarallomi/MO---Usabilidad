package mo.core.plugin;

import mo.core.Utils;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import mo.core.DirectoryWatcher;
import mo.core.WatchHandler;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class PluginRegistry {
    
    public static final String FILE_NOT_FOUND = "FileNotFound";
    public static final String FILE_IS_DIRECTORY = "FileIsDirectory";
    public static final String FILE_CANNOT_BE_DELETED = "FileCannotBeDeleted";
    public static final String PLUGIN_NOT_THIRD_PARTY_PLUGIN = "PluginNotThirdPartyPlugin";
    public static final String PLUGIN_BEING_USED = "PluginBeingUsed";
    public static final String PLUGIN_DELETED_OK = "PluginDeletedOk";

    private static PluginRegistry pg;    
    
    private List<IUpdatable> observers;

    private final String pluginsFolder
            = Utils.getBaseFolder() + "/plugins";

    private final String APP_PACKAGE = "mo/";

    private final List<String> pluginFolders;

    private URLClassLoader cl;

    private final DirectoryWatcher dirWatcher;   
    
    private final PluginData pluginData;
    
    private boolean testingJar = false;
    

    private final Logger logger
            = Logger.getLogger(PluginRegistry.class.getName());
    
    private ArrayList<ClassLoader> classLoaders;

    private PluginRegistry() {
        
        observers = new ArrayList<>();
        pluginFolders = new ArrayList<>();
        pluginData = new PluginData();        
        dirWatcher = new DirectoryWatcher();
        
        File folder = new File(pluginsFolder);
        if (!folder.isDirectory()) {
            if (!folder.mkdir()) {
                logger.log(
                        Level.WARNING, 
                        "Can not create plugins folder \"{0}\"", pluginsFolder
                );
            }
        }

        pluginFolders.add(pluginsFolder);        

        dirWatcher.addDirectory(folder.toPath(), true);
                
    }
    
    
    public Path getPluginsFolder(){
        return Paths.get(pluginsFolder);
    }
    
    
    public void subscribePluginsChanges(IUpdatable obs){
        observers.add(obs);
    }
    
    private void notifyChanges(){
        for(IUpdatable obs : observers){
            obs.update();
        }
    }
    
    
    public PluginData getPluginData(){
        return pluginData;
    }
       
  
    

    public synchronized static PluginRegistry getInstance() {
        if (pg == null) {
            pg = new PluginRegistry();            
            
            try{

                //look for plugins in app jar
                pg.processAppJar();

                //look for plugins in folders
                pg.processPluginFolders();

            } catch(Exception e){
                e.printStackTrace();
            }
            
            for(Plugin p : pg.pluginData.getPlugins()){
                if(p.isThirdParty()){
                    pg.assignXMLData(p);
                }
            }

            pg.initDirWatcher();

            pg.pluginData.checkDependencies();

        }

        return pg;
    }
    
    
    
    private void assignXMLData(Plugin plugin){
        if(plugin == null) return;

        Path xmlPath = null;
        
        Path path = plugin.getPath();
        
        if(path == null) return;
        
        Path root = path.getParent();
        
        if(root == null){
            xmlPath = Paths.get("plugin.xml");
        } else {
            xmlPath = Paths.get(root.toString(), "plugin.xml");
        }

            
        try {

            File fXmlFile = xmlPath.toFile();
            
            
            
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            Element e = doc.getDocumentElement();

            try {
                String val = e.getElementsByTagName("homepage").item(0).getTextContent().trim();
                if(val != null && val.length() != 0){
                    plugin.setWebsite(val);
                }
            } catch(Exception ex) {}
            
            try {
                String val = e.getElementsByTagName("author").item(0).getTextContent().trim();
                if(val != null && val.length() != 0){
                    plugin.setAuthor(val);
                }
            } catch(Exception ex) {}
            
            try {
                String val = e.getElementsByTagName("description").item(0).getTextContent().trim();
                if(val != null && val.length() != 0){
                    plugin.setDescription(val);
                }
            } catch(Exception ex) {}
            
            try {
                String val = e.getElementsByTagName("name").item(0).getTextContent().trim();
                if(val != null && val.length() != 0){
                    plugin.setName(val);
                }
            } catch(Exception ex) {}
            
            try {
                String val = e.getElementsByTagName("contact").item(0).getTextContent().trim();
                if(val != null && val.length() != 0){
                    plugin.setContact(val);
                }
            } catch(Exception ex) {}


        } catch (Exception e) {
            System.out.println("No XML file.");
            //e.printStackTrace();
        }

    }
    
    
    private void initDirWatcher(){

        pg.dirWatcher.addWatchHandler(new WatchHandler() {
            @Override
            public synchronized void onCreate(File file) {
                try {
                    processFile(file);
                    
                    Plugin plugin = pluginData.getPluginByPath(file.getAbsolutePath());
                    assignXMLData(plugin);
                } catch(Exception e){
                    e.printStackTrace();
                }        
                notifyChanges();
            }

            @Override
            public synchronized void onDelete(File file) {
               
                Plugin plugin = pluginData.getPluginByPath(file.getPath());
                
                if(plugin == null) return;
                
                pluginData.unregisterPlugin(plugin);

                notifyChanges();
            }

            @Override
            public synchronized void onModify(File file) {
                try {
                    processFile(file);
                    
                    Plugin plugin = pluginData.getPluginByPath(file.getAbsolutePath());
                    assignXMLData(plugin);
                } catch(Exception e){
                }
                notifyChanges();
            }
        });            

        pg.dirWatcher.start();
        
        
    }
    
    
    public List<Plugin> getPluginsFor(String extensionPointId) {
        return pluginData.getPluginsFor(extensionPointId, ">=0.0.0");
    }

    public List<Plugin> getPluginsFor(String extensionPointId, String version) {
        return pluginData.getPluginsFor(extensionPointId, version);
    }
    
  
    
    private void processFile(File file) throws IOException {

        if (file.isFile()) {
            if (file.getName().endsWith(".class")) {
                FileInputStream in = new FileInputStream(file);
                
                pg.processClassAsInputStream(in, null);
                
                in.close();

            } else if (file.getName().endsWith(".jar")) {
                pg.processJarFile(file);
            }
        } else {
            pg.processFolder(file.getAbsolutePath());
        }
        pg.pluginData.checkDependencies();
    }
    

    private void processAppJar() throws IOException {
        File jarFile = new File(PluginRegistry.class.getProtectionDomain()
                .getCodeSource().getLocation().getFile());

        String[] packages = {APP_PACKAGE};

        if (jarFile.getName().endsWith(".jar")) {
            processJarFile(jarFile, packages);
        } else {
            // working only with classes (development)(Netbeans)
            pluginFolders.add(0, jarFile.getAbsolutePath());
        }
    }

    private void processClassAsInputStream(InputStream classIS, String path) throws IOException {

        ExtensionScanner exScanner = new ExtensionScanner(Opcodes.ASM5);
        exScanner.setClassLoader(cl);
        ClassReader cr = new ClassReader(classIS);
        cr.accept(exScanner, 0);
        
        if(testingJar){
            return;
        }

        if (exScanner.getPlugin() != null) {
            Plugin newPlugin = exScanner.getPlugin();
            
            newPlugin.setPath(path);
            pg.pluginData.addPlugin(newPlugin);

        } else if (exScanner.getExtPoint() != null) {
            pg.pluginData.addExtensionPoint(exScanner.getExtPoint());
        }
        
    }
    
        
    
    public synchronized String uninstallPlugin(Plugin plugin){
        
        if(!plugin.isThirdParty()){
            return PLUGIN_NOT_THIRD_PARTY_PLUGIN;
        }
        
        File file = plugin.getPath().toFile();

        if(!file.exists()) {
            return FILE_NOT_FOUND;
        }
        
        if(file.isDirectory()) { 
            return FILE_IS_DIRECTORY;
        }
        
        if(2==1){
            return PLUGIN_BEING_USED;
        }
     
        try {
            boolean deleted = file.delete();
            
            if(deleted){
                pluginData.unregisterPlugin(plugin);
                return PLUGIN_DELETED_OK;
            }

        } catch(Exception e){
        }       
        
        return FILE_CANNOT_BE_DELETED;
        
    }


    private void processJarFile(File jar) throws IOException {
        processJarFile(jar, null);
    }

    
    private void processJarFile(File jar, String[] packages) {

        try (JarFile jarFile = new JarFile(jar)) {

            cl = URLClassLoader.newInstance(new URL[] {jar.toURI().toURL()}, PluginRegistry.class.getClassLoader());
            
            Enumeration entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) entries.nextElement();
                String entryName = jarEntry.getName();

                if (entryName.endsWith(".class")) {
                    if (packages != null) {
                        for (String p : packages) {
                            if (entryName.startsWith(p)) {
                                processClassAsInputStream(jarFile
                                        .getInputStream(jarEntry), jar.getAbsolutePath());
                            }
                        }
                    } else {
                        processClassAsInputStream(
                                jarFile.getInputStream(jarEntry), jar.getAbsolutePath());
                    }
                }
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }
    
    /*
    private void processJarFile(File jar, String[] packages) throws IOException {

        JarFile jarFile = new JarFile(jar);
        
        cl = URLClassLoader.newInstance(new URL[] {jar.toURI().toURL()}, PluginRegistry.class.getClassLoader());
        
        Enumeration entries = jarFile.entries();
        
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) entries.nextElement();
            String entryName = jarEntry.getName();
            if (entryName.endsWith(".class")) {
                if (packages != null) {
                    for (String p : packages) {
                        if (entryName.startsWith(p)) {
                            InputStream in = jarFile.getInputStream(jarEntry);
                            processClassAsInputStream(in, jar.getAbsolutePath());
                            in.close();
                        }
                    }
                } else {
                    InputStream in = jarFile.getInputStream(jarEntry);
                    processClassAsInputStream(in, jar.getAbsolutePath());
                    in.close();
                }
            }
        }
        
        cl.close();
        
        jarFile.close();        
    }*/

    
    
    public Exception checkPlugin(File file){
        
        testingJar = true;        
        
        try{            
            processFile(file);
            return null;            
        } catch(Exception e){            
            return e;            
        } finally {
            testingJar = false;
        }        
    }

  
    public void copyPluginToFolder(File file) throws IOException{
        Path src = Paths.get(file.getAbsolutePath());
        Path dest = Paths.get(pluginsFolder+"/");        
        Files.copy(src, dest.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
    }
    

    private void processPluginFolders() throws IOException {
        for (String pluginFolder : pluginFolders) {
            processFolder(pluginFolder);
        }
    }
    

    private void processFolder(String pluginFolder) throws IOException, FileNotFoundException, MalformedURLException {
        String[] extensions = {"class", "jar"};
        ClassLoader classLoader = PluginRegistry.class.getClassLoader();
        URLClassLoader urlCL;
        List<URL> urls = new ArrayList<>();
        Collection<File> files = FileUtils
                .listFiles(new File(pluginFolder), extensions, true);
        for (File f : files) {
            if (f.getName().endsWith(".class")) {
                urls.add(f.getParentFile().toURI().toURL());
            } else if (f.getName().endsWith(".jar")) {
                urls.add(f.toURI().toURL());
            }
        }

        urlCL = new URLClassLoader(
                (URL[]) urls.toArray(new URL[urls.size()]), classLoader);
        
        if (classLoaders == null) {
            classLoaders = new ArrayList<>();
        }
        classLoaders.add(urlCL);
        
        cl = urlCL;

        for (File file : files) {
            if (file.getName().endsWith(".class")) {
                FileInputStream in = new FileInputStream(file);
                processClassAsInputStream(in, null);
                in.close();
            } else if (file.getName().endsWith(".jar")) {
                processJarFile(file);
            }
        }

    }


    
    
    public List<ClassLoader> getClassLoaders() {
        return classLoaders;
    }

    public Class<?> getClassForName(String clazzStr) throws ClassNotFoundException {
        
        ClassLoader cd;
        
        cd = PluginRegistry.class.getClassLoader();
        
        Class<?> c = null;
        
        try {
            c = cd.loadClass(clazzStr);
        } catch (ClassNotFoundException ex) {
            for (ClassLoader classLoader : classLoaders) {
                try {
                    c = classLoader.loadClass(clazzStr);
                } catch (ClassNotFoundException ex1) {
                    // blank
                }
                if (c != null) {
                    break;
                }
            }
        }
        
        if (c == null) {
            throw new ClassNotFoundException(clazzStr);
        }
        
        return c;
    }
}
