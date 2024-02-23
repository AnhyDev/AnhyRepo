package ink.anh.repo.db;

public class MySQLConfig {
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final String prefix;
    private final boolean useSSL;
    private final boolean autoReconnect;

    public MySQLConfig(String host, int port, String database, String username, String password, String prefix, boolean useSSL, boolean autoReconnect) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.prefix = prefix;
        this.useSSL = useSSL;
        this.autoReconnect = autoReconnect;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    
    public String getPrefix() {
        return prefix;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }
}
