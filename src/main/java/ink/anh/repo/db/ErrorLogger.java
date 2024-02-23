package ink.anh.repo.db;

import java.util.logging.Level;

import ink.anh.repo.AnhyRepo;

public class ErrorLogger {

    public static void log(AnhyRepo plugin, Exception ex, String message) {
        plugin.getLogger().log(Level.SEVERE, message, ex);
    }
}
