package ink.anh.repo.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import ink.anh.repo.AnhyRepo;

public class MySQLDatabaseManager extends DatabaseManager {

    private String host;
    private String database;
    private String username;
    private String password;
    private int port;
    private String tablePrefix;
    private final boolean useSSL;
    private final boolean autoReconnect;

    private Connection connection;

    protected MySQLDatabaseManager(AnhyRepo plugin, MySQLConfig config) {
    	super(plugin);
        this.host = config.getHost();
        this.database = config.getDatabase();
        this.username = config.getUsername();
        this.password = config.getPassword();
        this.port = config.getPort();
        this.tablePrefix = config.getPrefix();
        this.useSSL = config.isUseSSL();
        this.autoReconnect = config.isAutoReconnect();

        initialize();
    }

    @Override
    public void initialize() {
        try {
        	connection = DriverManager.getConnection(
        			"jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database +
        			"?autoReconnect=" + this.autoReconnect + "&useSSL=" + this.useSSL,
        			this.username, 
        			this.password

        		);

        	initializeTables();
        } catch (SQLException e) {
            ErrorLogger.log(plugin, e, "Could not initialize MySQL connection");
        }
    }
    
    @Override
    public AbstractRepositoryTable getRepositoryTable() {
        return new MySQLRepositoryTable(this, database);
    }

    @Override
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(
                		"jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database +
            			"?autoReconnect=" + this.autoReconnect + "&useSSL=" + this.useSSL,
            			this.username, 
            			this.password);
            }
        } catch (SQLException e) {
            ErrorLogger.log(plugin, e, "Could not retrieve MySQL connection");
        }
        return connection;
    }

    public String getDatabase() {
        return database;
    }

	public String getTablePrefix() {
		return tablePrefix;
	}
}
