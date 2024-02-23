package ink.anh.repo.db;

import java.sql.Connection;
import java.sql.SQLException;

import ink.anh.repo.AnhyRepo;
import ink.anh.repo.GlobalManager;

public abstract class DatabaseManager {
    private static DatabaseManager instance;

    protected AnhyRepo plugin;
    protected Connection connection;
    protected String dbName = "family";

    public static DatabaseManager getInstance(AnhyRepo plugin) {
        if (instance == null) {
        	GlobalManager manager = plugin.getGlobalManager();
            if (manager.isUseMySQL()) {
                instance = new MySQLDatabaseManager(plugin, manager.getMySQLConfig());
            } else {
                instance = new SQLiteDatabaseManager(plugin);
            }
        }
        return instance;
    }

    protected DatabaseManager(AnhyRepo plugin) {
        this.plugin = plugin;
    }

    public abstract void initialize();

    public abstract Connection getConnection();

    public abstract AbstractRepositoryTable getRepositoryTable();
    
    // Метод для перезавантаження конфігурації та бази даних
    public static void reload(AnhyRepo plugin) {
        if (instance != null) {
            instance.closeConnection(); // Закриваємо існуюче з'єднання перед переініціалізацією
        }
        instance = null; // Скидаємо інстанс для створення нового з оновленою конфігурацією
        getInstance(plugin); // Створюємо новий інстанс з оновленою конфігурацією
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            ErrorLogger.log(plugin, e, "Failed to close database connection");
        }
    }
    
    public void initializeTables() {
    	getRepositoryTable().initialize();
    }
}
