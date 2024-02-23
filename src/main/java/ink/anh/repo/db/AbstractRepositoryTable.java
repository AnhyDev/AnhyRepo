package ink.anh.repo.db;

import java.util.UUID;

import ink.anh.repo.storage.Repository;

public abstract class AbstractRepositoryTable {
    
    protected DatabaseManager dbManager;
    protected String dbName;

    public AbstractRepositoryTable(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.dbName = dbManager.dbName;
        initialize();
    }

    protected abstract void initialize();

    public abstract void insertOrUpdateRepository(UUID playerUuid, String loverName, Repository[] repositories);
    public abstract void insertOrUpdateRepository(UUID playerUuid, String loverName, Repository repository);

    
    public abstract Repository[] getAllRepositoriesByLoverName(String loverName);
    public abstract Repository[] getAllRepositories(UUID playerUuid);
    public abstract boolean clearRepository(UUID playerUuid);
    public abstract boolean deleteRepository(UUID playerUuid);
    
}
