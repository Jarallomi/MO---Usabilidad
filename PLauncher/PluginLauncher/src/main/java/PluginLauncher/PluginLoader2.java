package PluginLauncher;

import mo.capture.CaptureProvider;
import mo.visualization.VisualizationProvider;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader2 {

    private final File pluginDirectory;

    public PluginLoader2(File pluginDirectory) {
        this.pluginDirectory = pluginDirectory;
    }

    public List<Object> loadPlugins() {
        List<Object> plugins = new ArrayList<>();
        File[] pluginFiles = pluginDirectory.listFiles((dir, name) -> name.endsWith(".jar"));

        if (pluginFiles != null) {
            for (File pluginFile : pluginFiles) {
                try {
                    URL[] urls = {pluginFile.toURI().toURL()};
                    URLClassLoader pluginClassLoader = new URLClassLoader(urls, this.getClass().getClassLoader());

                    Thread.currentThread().setContextClassLoader(pluginClassLoader);

                    // Si el plugin tiene Main-Class en el manifiesto, cargarlo
                    if (hasMainClass(pluginFile)) {
                        loadPluginFromManifest(pluginFile, pluginClassLoader, plugins);
                    } else {
                        // Buscar clases que implementen CaptureProvider o VisualizationProvider
                        loadPluginFromClasses(pluginFile, pluginClassLoader, plugins);
                    }

                } catch (Exception e) {
                    System.err.println("Error loading plugin: " + pluginFile.getName());
                    e.printStackTrace();
                }
            }
        }
        return plugins;
    }

    private void loadPluginFromManifest(File pluginFile, URLClassLoader pluginClassLoader, List<Object> plugins) throws Exception {
        try (JarFile jarFile = new JarFile(pluginFile)) {
            String mainClassName = jarFile.getManifest().getMainAttributes().getValue("Main-Class");
            if (mainClassName != null) {
                Class<?> pluginClass = pluginClassLoader.loadClass(mainClassName);
                Object pluginInstance = pluginClass.getDeclaredConstructor().newInstance();
                plugins.add(pluginInstance);
                System.out.println("Loaded plugin with Main-Class: " + mainClassName);
            }
        }
    }

    private void loadPluginFromClasses(File pluginFile, URLClassLoader pluginClassLoader, List<Object> plugins) {
        try (JarFile jarFile = new JarFile(pluginFile)) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName()
                            .replace("/", ".")
                            .replace(".class", "");

                    try {
                        Class<?> clazz = pluginClassLoader.loadClass(className);

                        // Verificar si la clase implementa CaptureProvider o VisualizationProvider
                        if (CaptureProvider.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
                            plugins.add(clazz.getDeclaredConstructor().newInstance());
                            System.out.println("Loaded CaptureProvider: " + className);
                        } else if (VisualizationProvider.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
                            plugins.add(clazz.getDeclaredConstructor().newInstance());
                            System.out.println("Loaded VisualizationProvider: " + className);
                        }
                    } catch (ClassNotFoundException | NoClassDefFoundError e) {
                        // Ignorar clases que no puedan cargarse
                        System.err.println("Failed to load class: " + className);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading jar file: " + pluginFile.getName());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error instantiating plugin class in jar: " + pluginFile.getName());
            e.printStackTrace();
        }
    }

    public boolean hasMainClass(File jarFile) {
        try (JarFile jar = new JarFile(jarFile)) {
            return jar.getManifest().getMainAttributes().getValue("Main-Class") != null;
        } catch (IOException e) {
            return false;
        }
    }

    public void addToClasspath(File jarFile) throws Exception {
        URL jarUrl = jarFile.toURI().toURL();
        URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        addUrlMethod.setAccessible(true);
        addUrlMethod.invoke(sysLoader, jarUrl);
    }
}