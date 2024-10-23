package mo.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.core.preferences.AppPreferencesWrapper;
import mo.core.preferences.PreferencesManager;
import mo.core.ui.menubar.IMenuBarItemProvider;
import mo.core.ui.menubar.MenuItemLocations;
import org.apache.commons.io.FileUtils;

@Extension(
        xtends = {
            @Extends(
                    extensionPointId = "mo.core.ui.menubar.IMenuBarItemProvider"
            )
        }
)
public class Language implements IMenuBarItemProvider {

    private JMenuItem menu = new JMenuItem("Language");

    private static final Logger logger = Logger.getLogger(Language.class.getName());

    private I18n i18n;

    public Language() {

        i18n = new I18n(Language.class);

        menu.setName("language");
        menu.setText(i18n.s("Language.languageMenuItem"));

        menu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String defaultLocale = i18n.s("Language.default");
                String selected = (String) JOptionPane.showInputDialog(
                        null,
                        i18n.s("Language.dialog.message"),
                        i18n.s("Language.dialog.selectionTitle"),
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        listLocales(defaultLocale),
                        defaultLocale);

                if (selected != null) {
                    if (selected.equals(defaultLocale)) {

                    } else {
                        Locale l = Locale.getDefault();
                        if (!selected.contains("_")) {
                            l = new Locale(selected);
                        } else {
                            String[] parts = selected.split("_");
                            if (parts.length == 2) {
                                l = new Locale(parts[0], parts[1]);
                            } else if (parts.length == 3) {
                                l = new Locale(parts[0], parts[1], parts[2]);
                            }
                        }
                        Locale.setDefault(l);

                        AppPreferencesWrapper prefs
                                = (AppPreferencesWrapper) PreferencesManager.loadOrCreate(
                                        AppPreferencesWrapper.class,
                                        new File(MultimodalObserver.APP_PREFERENCES_FILE));
                        prefs.setLocaleLanguage(l.getLanguage());
                        prefs.setLocaleCountry(l.getCountry());

                        PreferencesManager.save(prefs, new File(MultimodalObserver.APP_PREFERENCES_FILE));
                    }
                }
            }
        });
    }

    private Object[] listLocales(String defaultStr) {
        LinkedHashSet<String> locales = new LinkedHashSet<>();
        locales.add(defaultStr);
        File source = new File(Language.class.getProtectionDomain()
                .getCodeSource().getLocation().getFile());

        if (source.getName().endsWith(".jar")) {

            try (JarFile jarFile = new JarFile(source)) {
                Enumeration entries = jarFile.entries();

                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = (JarEntry) entries.nextElement();
                    String entryName = jarEntry.getName();

                    if (entryName.contains("Translations_")) {
                        String localeStr = entryName.substring(
                                entryName.indexOf("Translations_") + 5,
                                entryName.lastIndexOf('.'));
                        locales.add(localeStr);
                    }

                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }

            File i18nFolder = new File(source.getParentFile(), "i18n");

            if (i18nFolder.exists()) {
                String[] extensions = {"properties"};
                Collection<File> files = FileUtils
                        .listFiles(i18nFolder, extensions, true);

                for (File file : files) {
                    if (file.getName().contains("Translations_")) {
                        String fileName = file.getName();
                        String localeStr = fileName.substring(
                                fileName.indexOf("Translations_")
                                + "Translations_".length(),
                                fileName.lastIndexOf('.'));
                        locales.add(localeStr);
                    }
                }
            }
        } else {
            source = source.getParentFile().getParentFile();
            String[] extensions = {"class", "properties"};

            Collection<File> files = FileUtils
                    .listFiles(source, extensions, true);

            for (File file : files) {
                if (file.getName().contains("Translations_")) {
                    String fileName = file.getName();
                    String localeStr = fileName.substring(
                            fileName.indexOf("Translations_")
                            + "Translations_".length(),
                            fileName.lastIndexOf('.'));
                    locales.add(localeStr);
                }
            }
        }

        return locales.toArray();
    }

    @Override
    public JMenuItem getItem() {
        return menu;
    }

    @Override
    public int getRelativePosition() {
        return MenuItemLocations.UNDER;
    }

    @Override
    public String getRelativeTo() {
        return "options";
    }

    public static void loadLocale() {
        AppPreferencesWrapper prefs
                = (AppPreferencesWrapper) PreferencesManager.loadOrCreate(
                        AppPreferencesWrapper.class,
                        new File(MultimodalObserver.APP_PREFERENCES_FILE));

        String lan = prefs.getLocaleLanguage();
        String cou = prefs.getLocaleCountry();
        String var = prefs.getLocaleVariant();

        boolean languageSet = lan != null && !lan.isEmpty();

        boolean countrySet = cou != null && !cou.isEmpty();

        boolean variantSet = var != null && !var.isEmpty();

        Locale locale = null;
        if (languageSet && countrySet && variantSet) {
            locale = new Locale(lan, cou, var);
        } else if (languageSet && countrySet) {
            locale = new Locale(lan, cou);
        } else if (languageSet) {
            locale = new Locale(lan);
        }

        if (locale != null) {
            Locale.setDefault(locale);
        }
    }
}
