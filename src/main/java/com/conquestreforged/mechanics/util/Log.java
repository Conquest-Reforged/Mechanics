package com.conquestreforged.mechanics.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;

public class Log {

    private static final Logger logger = LogManager.getLogger("Mechanics");

    private Log() {}

    public static void info(Marker marker, String format, Object... args) {
        logger.info(marker, format, args);
    }

    public static void debug(Marker marker, String format, Object... args) {
        logger.debug(marker, format, args);
    }

    public static void trace(Marker marker, String format, Object... args) {
        logger.trace(marker, format, args);
    }

    public static void err(Marker marker, String format, Object... args) {
        logger.error(marker, format, args);
    }

    public static void warn(Marker marker, String format, Object... args) {
        logger.warn(marker, format, args);
    }
}
