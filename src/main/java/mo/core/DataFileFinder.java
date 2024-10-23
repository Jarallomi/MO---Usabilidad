package mo.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataFileFinder {

    private static final Logger logger = Logger.getLogger(DataFileFinder.class.getName());

    public static List<File> findFilesCreatedBy(File root, String creator) {
        ArrayList<String> creators = new ArrayList<>();
        creators.add(creator);
        return findFilesCreatedBy(root, creators);
    }

    public static List<File> findFilesCreatedBy(File root, List<String> creators) {
        ArrayList<File> result = new ArrayList<>();

        try {
            //Files.walkFileTree(root.toPath(), new )
            Properties prop = new Properties();
            Files
                    .walk(root.toPath())
                    .filter((Path t) -> t.getFileName().toString().endsWith(".desc"))
                    .forEach((Path t) -> {
                        try {
                            String s = new String(Files.readAllBytes(t));
                            prop.load(new StringReader(s.replace("\\", "\\\\")));
                            if (prop.containsKey("creator")) {
                                for (String creator : creators) {
                                    if (prop.get("creator").equals(creator)) {
                                        if (prop.containsKey("file")) {
                                            File f = t.resolve(prop.getProperty("file")).normalize().toFile();
                                            if (f.exists()) {
                                                System.out.println("file: " + f.getAbsolutePath());
                                                result.add(f);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    });
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public static List<Path> findFilesDescriptionCreatedBy(File root, String creators) {
        ArrayList<Path> result = new ArrayList<>();

        try {
            Properties prop = new Properties();
            Files
                    .walk(root.toPath())
                    .filter((Path t) -> t.getFileName().toString().endsWith(".desc"))
                    .forEach((Path t) -> {
                        try {
                            String s = new String(Files.readAllBytes(t));
                            prop.load(new StringReader(s.replace("\\", "\\\\")));
                            if (prop.containsKey("creator")) {
                                if(prop.get("creator").equals("mo.analysis.NotesRecorder")) {
                                    result.add(t);
                                }
                            }
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    });
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public static File findFileCreatedFor(File root, String captureFile, String configuration) {
        ArrayList<File> result = new ArrayList<>();
        File file = null;

        try {
            Properties prop = new Properties();
            Files
                    .walk(root.toPath())
                    .filter((Path t) -> t.getFileName().toString().endsWith(".desc"))
                    .forEach((Path t) -> {
                        try {
                            String s = new String(Files.readAllBytes(t));
                            prop.load(new StringReader(s.replace("\\", "\\\\")));
                            if (prop.containsKey("creator")) {
                                if (prop.get("creator").equals("mo.analysis.NotesRecorder")) {
                                    if(prop.containsKey("captureFile")) {
                                        if (prop.get("captureFile").equals(captureFile)) {
                                            if(prop.containsKey("configuration")) {
                                                if (prop.get("configuration").equals(configuration)) {
                                                    if(prop.containsKey("file")) {
                                                        File f = t.resolve(prop.getProperty("file")).normalize().toFile();
                                                        if (f.exists()) {
                                                            result.add(f);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    });
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        if(result.size() > 0) {
            file = result.get(0);
        }

        return file;
    }

    public static List<File> findFilesCreatedFor(File root, String creator, String compatible, String captureFile) {
        ArrayList<File> result = new ArrayList<>();
        File file = null;

        try {
            Properties prop = new Properties();
            Files
                    .walk(root.toPath())
                    .filter((Path t) -> t.getFileName().toString().endsWith(".desc"))
                    .forEach((Path t) -> {
                        try {
                            String s = new String(Files.readAllBytes(t));
                            prop.load(new StringReader(s.replace("\\", "\\\\")));

                            if (prop.containsKey("creator")) {
                                if (prop.get("creator").equals(creator)) {
                                    if(prop.containsKey("captureFile")) {
                                        if (prop.get("captureFile").equals(captureFile)) {
                                            if(prop.containsKey("compatible")) {
                                                if (prop.get("compatible").equals(compatible)) {
                                                    if(prop.containsKey("file")) {
                                                        File f = t.resolve(prop.getProperty("file")).normalize().toFile();
                                                        if (f.exists()) {
                                                            result.add(f);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    });
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        return result;
    }
}
