package mo.core;

import es.eucm.i18n.I18N;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

public class I18n {

    private I18N _i18n;

    private final String BASE_NAME = "Translations";

    private final String i18nFolder = "i18n";

    private final List<String> baseNames;

    private static final Logger logger = Logger.getLogger(I18n.class.getName());

    public I18n(Class clazz) {
        Locale locale = Locale.getDefault();
        baseNames = Arrays.asList(new String[]{BASE_NAME});
        _i18n = new I18N();

        String packageName = clazz.getPackage().getName();

        List<ResourceBundle> bundles = new ArrayList<>();

        for (String baseName : baseNames) {

            //option 1: properties file in resources folder "basename" and 
            //          filename "package.baseName[_[locale]].properties" 
            String baseFileName = packageName + "." + baseName;

            File source = new File(clazz.getProtectionDomain()
                    .getCodeSource().getLocation().getFile());

            //production
            if (source.getName().endsWith(".jar")) {

                try (JarFile jarFile = new JarFile(source)) {

                    Enumeration entries = jarFile.entries();

                    while (entries.hasMoreElements()) {
                        JarEntry jarEntry = (JarEntry) entries.nextElement();
                        String entryName = jarEntry.getName();

                        if (entryName.contains(baseFileName)) {

                            LocalizablePropertyResourceBundle b
                                    = new LocalizablePropertyResourceBundle(jarFile.getInputStream(jarEntry));
                            String localeStr = "";
                            int startOfLocaleStr = entryName.lastIndexOf(baseFileName) + baseFileName.length();
                            int indexOfPointAndExtension = entryName.lastIndexOf('.');

                            if (startOfLocaleStr != indexOfPointAndExtension) {
                                localeStr = entryName.substring(
                                        startOfLocaleStr + 1, indexOfPointAndExtension
                                );
                            }
                            b.setLocale(localeStr);
                            bundles.add(b);

                        }

                    }
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }

            } else {
                //development

                source = source.getParentFile().getParentFile();
                String[] extensions = {"properties"};

                Collection<File> files = FileUtils
                        .listFiles(new File(source, "resources/main/" + i18nFolder), extensions, true);

                for (File file : files) {
                    if (file.getName().startsWith(baseFileName)) {
                        try {
                            String name = file.getName();
                            String localeStr = name.substring(
                                    name.indexOf(baseFileName) + baseFileName.length()
                                    + (name.contains("_") ? 1 : 0),
                                    name.lastIndexOf('.'));
                            LocalizablePropertyResourceBundle b = new LocalizablePropertyResourceBundle(new FileInputStream(file));
                            b.setLocale(localeStr);
                            //printLocale(b);
                            bundles.add(b);
                        } catch (FileNotFoundException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }

                    }
                }
            }

            //Option 2: class or properties files in folder i18n + package as folders
            try {
                ResourceBundle b = ResourceBundle.getBundle(i18nFolder + "." + packageName + "." + baseName, locale);
                bundles.add(b);
                //printLocale(b);
            } catch (Exception ex) {
                //logger.log(Level.INFO, null, ex);
            }

            //Option 3: class or properties files in package
            try {
                ResourceBundle b = ResourceBundle.getBundle(packageName + "." + baseName, locale);
                bundles.add(b);
                //printLocale(b);
            } catch (Exception e) {
                //logger.log(Level.INFO, null, e);
            }

            //Option 4: properties files in i18n folder in the same folder of the jar
            if (source.getName().endsWith(".jar")) {
                String[] extensions = {"properties"};
                Collection<File> files = FileUtils
                        .listFiles(new File(source.getParentFile(), i18nFolder), extensions, true);

                //System.out.println("---" + clazz);

                for (File file : files) {
                    File parent = source.getParentFile();
                    //System.out.println("filename| " + file);

                    //System.out.println("src   " + (new File(source, i18nFolder)).toPath());
                    Path relative = (new File(parent, i18nFolder)).toPath().relativize(file.toPath());

                    //System.out.println("rel   " + relative);

                    String pathString = relative.toString().replace(File.separator, ".");
                    //|| file.get.contains(packageName.replace("/", ".") + "/" + baseName)

                    String fileName = file.getName();

                    if (fileName.startsWith(baseFileName)) {
                        //System.out.println("filename            > " + file);
                        try {

                            String localeStr = fileName.substring(
                                    fileName.lastIndexOf(baseFileName) + baseFileName.length()
                                    + (fileName.contains("_") ? 1 : 0),
                                    fileName.lastIndexOf('.'));
                            LocalizablePropertyResourceBundle b = new LocalizablePropertyResourceBundle(new FileInputStream(file));
                            b.setLocale(localeStr);
                            //printLocale(b);
                            bundles.add(b);
                        } catch (FileNotFoundException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }

                    } else if (pathString.startsWith(baseFileName)) {

                        String localeStr = fileName.substring(
                                fileName.lastIndexOf(baseName)
                                + baseName.length()
                                + (fileName.contains("_") ? 1 : 0),
                                fileName.lastIndexOf(".properties")
                        );
                        try {
                            LocalizablePropertyResourceBundle b = new LocalizablePropertyResourceBundle(new FileInputStream(file));
                            b.setLocale(localeStr);
                            //printLocale(b);
                            bundles.add(b);
                        } catch (FileNotFoundException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }

        int[] matchsLevels = new int[bundles.size()];
        for (int i = 0; i < bundles.size(); i++) {
            matchsLevels[i] = matchLevel(locale, bundles.get(i).getLocale());
        }

        for (int i = 3; i > -1; i--) {
            for (int j = 0; j < bundles.size(); j++) {
                if (matchsLevels[j] == i) {
                    ResourceBundle b = bundles.get(j);
                    Set<String> keySet = b.keySet();
                    for (String key : keySet) {
                        if (!_i18n.getMessages().containsKey(key)) {
                            _i18n.setMessage(key, b.getString(key));
                        }
                    }
                }
            }
        }
    }

    public void addBaseName(String baseName) {
        if (!baseNames.contains(baseName)) {
            baseNames.add(baseName);
        }
    }

    public void removeBaseName(String baseName) {
        baseNames.remove(baseName);
    }

    private class MatchLevels {

        public static final int NONE = 0,
                LANGUAGE = 1,
                COUNTRY = 2,
                VARIANT = 3;
    }

    private int matchLevel(Locale goal, Locale candidate) {
        String gLan = goal.getLanguage();
        String gCou = goal.getCountry();
        String gVar = goal.getVariant();

        String cLan = candidate.getLanguage();
        String cCou = candidate.getCountry();
        String cVar = candidate.getVariant();

        int match = MatchLevels.NONE;

        if (gLan.equals(cLan)) {
            match = MatchLevels.LANGUAGE;
            if (gCou.equals(cCou)) {
                match = MatchLevels.COUNTRY;
                if (gVar.equals(cVar)) {
                    match = MatchLevels.VARIANT;
                }
            }
        }

        return match;
    }

    public String s(String key) {
        return _i18n.m(key);
    }

    public String s(String key, Object... args) {
        return _i18n.m(key, args);
    }

    public String s(int cardinality, String keyOne, String keyMany) {
        return _i18n.m(cardinality, keyOne, keyMany);
    }

    public String s(int cardinality, String keyOne, String keyMany, Object... args) {
        return _i18n.m(cardinality, keyOne, keyMany, args);
    }

    static void asd() {
        Path p = (new File("F:\\downloads\\3DNes_32bit\\3dnes_Data\\Mono\\etc\\mono\\config")).toPath();
        for (Iterator<Path> iterator = p.iterator(); iterator.hasNext();) {
            Path next = iterator.next();
            System.out.println(next.getFileName());
        }

    }

    public static void main(String[] args) {
        asd();
    }

    private class LocalizablePropertyResourceBundle
            extends PropertyResourceBundle {

        private Locale locale;

        public LocalizablePropertyResourceBundle(InputStream stream) throws IOException {
            super(stream);
        }

        public void setLocale(String localeString) {
            String[] parts = localeString.split("_");
            if (parts.length == 0) {
                locale = new Locale(localeString);
            } else if (parts.length == 1) {
                locale = new Locale(parts[0]);
            } else if (parts.length == 2) {
                locale = new Locale(parts[0], parts[1]);
            } else if (parts.length > 2) {
                locale = new Locale(parts[0], parts[1], parts[2]);
            }
        }

        @Override
        public Locale getLocale() {
            return locale;
        }
    }
}
