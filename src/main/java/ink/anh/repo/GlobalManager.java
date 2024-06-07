package ink.anh.repo;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import ink.anh.api.LibraryManager;
import ink.anh.api.database.DatabaseManager;
import ink.anh.api.database.MySQLConfig;
import ink.anh.api.lingo.Translator;
import ink.anh.api.lingo.lang.LanguageManager;
import ink.anh.api.messages.Logger;
import ink.anh.repo.storage.RepoDataHandler;
import net.md_5.bungee.api.ChatColor;

public class GlobalManager extends LibraryManager {

    private static GlobalManager instance;
	private AnhyRepo plugin;
	
	private LanguageManager langManager;
    private String pluginName;
    private String defaultLang;
    private boolean debug;

    private boolean useMySQL;
    private MySQLConfig mySQLConfig;
	
	private Map<String, String> defaultRepository;
	
	private GlobalManager(AnhyRepo plugin) {
		super(plugin);
		this.plugin = plugin;
		this.saveDefaultConfig();
		this.loadFields(plugin);
	}

    public static synchronized GlobalManager getManager(AnhyRepo plugin) {
        if (instance == null) {
            instance = new GlobalManager(plugin);
        }
        return instance;
    }
    
	@Override
	public Plugin getPlugin() {
		return plugin;
	}

	@Override
	public String getPluginName() {
		return pluginName;
	}

	@Override
	public LanguageManager getLanguageManager() {
		return langManager;
	}

	@Override
	public String getDefaultLang() {
		return defaultLang;
	}

	@Override
	public boolean isDebug() {
		return debug;
	}

	public boolean isUseMySQL() {
		return useMySQL;
	}

	public MySQLConfig getMySQLConfig() {
		return mySQLConfig;
	}

	public Map<String, String> getDefaultRepository() {
		return defaultRepository;
	}
    
    private void loadFields(AnhyRepo plugin) {
        defaultLang = plugin.getConfig().getString("language", "en");
        pluginName = ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("plugin_name", "AnhyRepo"));
        debug = plugin.getConfig().getBoolean("debug", false);
        useMySQL = "MySQL".equalsIgnoreCase(plugin.getConfig().getString("database.type"));
        setLanguageManager();
        setMySQLConfig();
        defaultRepository = loadRepoDefaultCommands();
    }

    public Map<String, String> loadRepoDefaultCommands() {
    	if (debug) Logger.warn(plugin, "Start loaded default repository");
        ConfigurationSection repoDefaultSection = plugin.getConfig().getConfigurationSection("repo_default");
        Map<String, String> commandsConfig = new LinkedHashMap<>();

        if (debug) Logger.warn(plugin, "repoDefaultSection != null: " + (repoDefaultSection != null));
        if (repoDefaultSection != null) {
            for (String key : repoDefaultSection.getKeys(false)) {
                String value = repoDefaultSection.getString(key);
                commandsConfig.put(key, value);
            	if (debug) Logger.warn(plugin, "DefaultRepo add: " + key + ": " + value);
            }
        }
        
        return commandsConfig;
    }

    private void saveDefaultConfig() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            boolean created = dataFolder.mkdirs();
            if (!created) {
                Logger.error(plugin, "Could not create plugin directory: " + dataFolder.getPath());
                return;
            }
        }
        
    	File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
        	plugin.getConfig().options().copyDefaults(true);
        	plugin.saveDefaultConfig();
        }
    }

    private void setLanguageManager() {
        if (this.langManager == null) {
            this.langManager = LangMessage.getInstance(this);;
        } else {
        	this.langManager.reloadLanguages();
        }
    }

	public boolean reload() {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
	        try {
	        	saveDefaultConfig();
	            plugin.reloadConfig();
	            loadFields(plugin);
	            langManager = null;
	            setLanguageManager();
	            new RepoDataHandler().removeAllRepositoryData();
	            Logger.info(plugin, Translator.translateKyeWorld(instance, "repo_configuration_reloaded" , new String[] {defaultLang}));
	        } catch (Exception e) {
	            e.printStackTrace();
	            Logger.error(plugin, Translator.translateKyeWorld(instance, "repo_err_reloading_configuration ", new String[] {defaultLang}));
	        }
		});
        return true;
    }

	private void setMySQLConfig() {
		this.mySQLConfig = new MySQLConfig(
				plugin.getConfig().getString("database.mysql.host"),
				plugin.getConfig().getInt("database.mysql.port"),
				plugin.getConfig().getString("database.mysql.database"),
				plugin.getConfig().getString("database.mysql.username"),
				plugin.getConfig().getString("database.mysql.password"),
				plugin.getConfig().getString("database.mysql.prefix"),
				plugin.getConfig().getBoolean("database.mysql.useSSL"),
				plugin.getConfig().getBoolean("database.mysql.autoReconnect"),
				false
	        );
	}

	@Override
	public DatabaseManager getDatabaseManager() {
		// TODO Auto-generated method stub
		return null;
	}
}
