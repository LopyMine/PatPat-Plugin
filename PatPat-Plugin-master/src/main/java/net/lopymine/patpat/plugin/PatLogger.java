package net.lopymine.patpat.plugin;

import net.lopymine.patpat.plugin.config.PatPatConfig;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PatLogger {

    private PatLogger() {
        throw new IllegalStateException("Logger class");
    }

    public static final Logger LOGGER = Logger.getLogger(PatPatPlugin.PLUGIN_ID);

    public static void info(String message, Object... args) {
        LOGGER.info(format(message, args));
    }

    public static void warn(String message, Exception e) {
        LOGGER.log(Level.WARNING, message, e);
    }

    public static void warn(String message, Object... args) {
        LOGGER.warning(format(message, args));
    }

    public static void error(String message, Exception e) {
        LOGGER.log(Level.SEVERE, message, e);
    }

    public static void error(String message, Object... args) {
        LOGGER.severe(format(message, args));
    }

    public static void debug(String message, Object... args) {
        PatPatConfig cfg = PatPatConfig.getInstance();
        if (cfg == null || !cfg.isDebug()) {
            return;
        }
        LOGGER.info("[DEBUG]: " + format(message, args));
    }

    private static String format(String message, Object... args) {
        return (args.length != 0) ? message.formatted(args) : message;
    }
}
