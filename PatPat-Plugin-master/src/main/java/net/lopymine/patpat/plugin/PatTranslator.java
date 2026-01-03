package net.lopymine.patpat.plugin;

import lombok.SneakyThrows;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;

import net.lopymine.patpat.plugin.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PatTranslator implements Translator {

    private static final String DEFAULT_LANG = "en_US";
    private static final String LANG_FOLDER = "lang/";
    private static final String LANG_FILETYPE = ".json";

    // Immutable snapshot after construction
    private final Map<String, Map<String, String>> localizations;

    private static volatile PatTranslator instance;

    public static PatTranslator getInstance() {
        PatTranslator t = instance;
        if (t == null) {
            synchronized (PatTranslator.class) {
                t = instance;
                if (t == null) {
                    t = new PatTranslator();
                    instance = t;
                }
            }
        }
        return t;
    }

    public static void register() {
        GlobalTranslator.translator().addSource(getInstance());
    }

    public static void unregister() {
        PatTranslator t = instance;
        if (t != null) {
            GlobalTranslator.translator().removeSource(t);
            instance = null;
        }
    }

    public PatTranslator() {
        Map<String, Map<String, String>> tmp = new HashMap<>();
        registerInternalLangs(tmp);
        registerExternalLangs(tmp);

        // freeze deep
        Map<String, Map<String, String>> frozen = new HashMap<>();
        tmp.forEach((lang, map) -> frozen.put(lang, Map.copyOf(map)));
        this.localizations = Map.copyOf(frozen);
    }

    @SneakyThrows
    private List<String> getInternalLangFiles() {
        // Ищем папку lang/ в classpath (внутри jar)
        Enumeration<URL> resources = PatTranslator.class.getClassLoader().getResources(LANG_FOLDER);
        Set<String> result = new HashSet<>();

        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();

            // В jar это обычно jar:file:...!/lang/
            if ("jar".equals(url.getProtocol())) {
                JarURLConnection conn = (JarURLConnection) url.openConnection();
                try (JarFile jar = conn.getJarFile()) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry e = entries.nextElement();
                        String name = e.getName();

                        if (name.startsWith(LANG_FOLDER) && name.endsWith(LANG_FILETYPE) && !e.isDirectory()) {
                            String file = name.substring(LANG_FOLDER.length());          // en_US.json
                            String lang = file.substring(0, file.length() - LANG_FILETYPE.length()); // en_US
                            result.add(lang);
                        }
                    }
                }
            } else if ("file".equals(url.getProtocol())) {
                // На дев-сборке ресурсы могут быть просто папкой
                File dir = new File(url.toURI());
                File[] files = dir.listFiles((d, n) -> n.endsWith(LANG_FILETYPE));
                if (files != null) {
                    for (File f : files) {
                        String n = f.getName();
                        result.add(n.substring(0, n.length() - LANG_FILETYPE.length()));
                    }
                }
            }
        }

        return result.stream().sorted().toList();
    }


    private void registerInternalLangs(Map<String, Map<String, String>> tmp) {
        try {
            for (String lang : getInternalLangFiles()) {
                readLangResourceFromJar(tmp, lang);
            }
        } catch (Exception e) {
            PatLogger.error("Failed to load internal langs", e);
        }
    }

    private void readLangResourceFromJar(Map<String, Map<String, String>> tmp, String lang) {
        Map<String, String> langResource = ResourceUtils.loadLangFromJar("%s%s%s".formatted(LANG_FOLDER, lang, LANG_FILETYPE));
        if (langResource == null) return;

        langResource.replaceAll((k, v) -> v.replace("'", "''"));
        tmp.computeIfAbsent(lang, k -> new HashMap<>()).putAll(langResource);
    }

    private void registerExternalLangs(Map<String, Map<String, String>> tmp) {
        File langFolder = new File(PatPatPlugin.getInstance().getDataFolder(), "lang");
        if (!langFolder.exists() || !langFolder.isDirectory()) return;

        File[] jsonFiles = langFolder.listFiles((dir, name) ->
                name.toLowerCase().endsWith(LANG_FILETYPE) && new File(dir, name).isFile());
        if (jsonFiles == null) return;

        for (File jsonFile : jsonFiles) {
            String name = jsonFile.getName();
            String lang = name.substring(0, name.length() - LANG_FILETYPE.length());

            Map<String, String> langResource = ResourceUtils.loadLang(jsonFile);
            if (langResource == null) continue;

            langResource.replaceAll((k, v) -> v.replace("'", "''"));
            tmp.computeIfAbsent(lang, k -> new HashMap<>()).putAll(langResource);
        }
    }

    // getInternalLangFiles() можно оставить как есть, но лучше ловить исключения внутри, см. выше

    @Override
    public @NotNull Key name() {
        return Key.key("patpat:translator");
    }

    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        if (!key.startsWith("patpat")) {
            return null;
        }

        String lang = locale.toString();
        Map<String, String> localization = this.localizations.get(lang);

        if (localization == null) {
            String msg = this.localizations.getOrDefault(DEFAULT_LANG, Map.of()).get(key);
            return msg == null ? null : new MessageFormat(msg);
        }

        String msg = localization.getOrDefault(
                key,
                this.localizations.getOrDefault(DEFAULT_LANG, Map.of()).get(key)
        );
        return msg == null ? null : new MessageFormat(msg);
    }
}
