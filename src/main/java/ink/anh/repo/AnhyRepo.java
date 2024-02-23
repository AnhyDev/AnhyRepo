package ink.anh.repo;

import org.bukkit.plugin.java.JavaPlugin;

import ink.anh.repo.command.RepoCommand;
import ink.anh.repo.db.DatabaseManager;
import ink.anh.repo.gui.InventoryListener;

public class AnhyRepo extends JavaPlugin {
	
    private static AnhyRepo instance;
    private GlobalManager manager;
    private DatabaseManager dbManager;


    public static AnhyRepo getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        manager = GlobalManager.getManager(instance);

        dbManager = DatabaseManager.getInstance(this);
        dbManager.initialize();

        this.getCommand("repo").setExecutor(new RepoCommand(this));
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
    }

	public GlobalManager getGlobalManager() {
		return manager;
	}

    public DatabaseManager getDatabaseManager() {
        return dbManager;
    }
}
