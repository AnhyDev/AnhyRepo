package ink.anh.repo.utils;

import ink.anh.api.lingo.Translator;
import ink.anh.api.messages.Logger;
import ink.anh.api.utils.LangUtils;
import ink.anh.repo.AnhyRepo;
import ink.anh.repo.db.AbstractRepositoryTable;
import ink.anh.repo.storage.KeyStore;
import ink.anh.repo.storage.RepoDataHandler;
import ink.anh.repo.storage.Repository;
import ink.anh.repo.storage.Slots;
import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RepoUtils {

	private static final AnhyRepo repoPlugin = AnhyRepo.getInstance();
    private static final RepoDataHandler dataHandler = new RepoDataHandler();

    
	// Метод для додавання масиву репозиторіїв до глобальної мапи
    public static void addRepositories(Player player, Repository[] repositories) {
        if (player != null && player.isOnline() && repositories != null) {
        	if (repoPlugin.getGlobalManager().isDebug()) Logger.error(repoPlugin, "repositories == null: " + (repositories == null));
        	dataHandler.addRepositoryData(player.getUniqueId(), repositories);
        }
    }

    public static Repository[] getRepositories(Player player) {
    	
    	if (player == null) {
    		return null;
    	}
    	
    	UUID playerUUID = player.getUniqueId();
        Repository[] repositories = dataHandler.getRepositoryData(playerUUID);
    	AbstractRepositoryTable repoTable = repoPlugin.getDatabaseManager().getRepositoryTable();
        if (repositories == null ) {
        	repositories = repositoriesToArray(repoTable.getAllRepositories(playerUUID));
        	if (repositories != null) {
        		addRepositories(player, repositories);
        	}
        }
    	
    	if (repositories == null) {
            repositories = createNewRepositories(player);
        	repoTable.insertOrUpdateRepository(playerUUID, player.getName(), repositories);
    	}
    	
        return repositories;
    }

    public static Repository[] createNewRepositories(Player player) {
        Repository[] repositories = new Repository[Slots.values().length];
        Repository defaultRepo = repoPlugin.getGlobalManager().getDefaultRepository();
        if (repoPlugin.getGlobalManager().isDebug());
        String groupName = Translator.translateKyeWorld(repoPlugin.getGlobalManager(), defaultRepo.getGroupName(), LangUtils.getPlayerLanguage(player));
        defaultRepo.setGroupName(groupName);
        repositories[0] = defaultRepo;
        
        if (repoPlugin.getGlobalManager().isDebug()) {

            for (Repository repo : repositories) {
                if (repo != null) {
                    Logger.warn(repoPlugin, "repo group name: " + repo.getGroupName());
                }
            }
        }

    	addRepositories(player, repositories);
        return repositories;
    }

    public static Repository[] repositoriesToArray(Repository[] repositories) {
    	if (repositories == null) {
    		return null;
    	}
        Repository[] sortedRepositories = null;

        for (Repository repo : repositories) {
            if (repo != null) {
                sortedRepositories = new Repository[Slots.values().length];
                int slotIndex = repo.getSlot().ordinal();
                sortedRepositories[slotIndex] = repo;
            }
        }
        return sortedRepositories;
    }
    
    public static ItemStack[] contentRepository(Repository repo) {
        final int size = 27;
        ItemStack[] content = new ItemStack[size];
        
    	if (repo == null) {
            return content;
    	}
    	
        Map<KeyStore, ItemStack> storage = repo.getStorageClone();
        if (storage == null || storage.isEmpty()) return content;

        int i = 0;
        for (Map.Entry<KeyStore, ItemStack> entry : storage.entrySet()) {
            if (i >= size) break;
            content[i] = entry.getValue();
            i++;
        }
        return content;
    }

    public static ItemStack[] contentRepoGroup(Repository[] repositories) {
    	final int size = Slots.values().length;
        ItemStack[] content = new ItemStack[size];
        
        for (int i = 0; i < repositories.length; i++) {
            Repository repo = repositories[i];
            if (repo != null) {
                // Створюємо зачаровану книгу
                ItemStack book = new ItemStack(Material.BOOK);
                ItemMeta meta = book.getItemMeta();
                if (meta != null) {
                    // Встановлюємо назву книги згідно з groupName репозиторію
                    meta.setDisplayName(ChatColor.AQUA + repo.getGroupName());

                    // Отримуємо клон storage і створюємо список назв елементів
                    Map<KeyStore, ItemStack> storageClone = repo.getStorageClone();
                    List<String> lore = storageClone.keySet().stream()
                        .map(KeyStore::displayName)
                        .collect(Collectors.toList());

                    // Встановлюємо лор для книги зі списку назв елементів
                    meta.setLore(lore);
                    book.setItemMeta(meta);

                    // Встановлюємо кількість предметів у книзі відповідно до кількості елементів у storage
                    int itemCount = storageClone.size();
                    book.setAmount(Math.min(itemCount, book.getMaxStackSize()));
                }
                content[i] = book;
                
                if (size <= i) break;
            }
        }
        
        return content;
    }
}
