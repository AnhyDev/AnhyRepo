package ink.anh.repo.utils;

import ink.anh.api.messages.Logger;
import ink.anh.repo.AnhyRepo;
import ink.anh.repo.db.AbstractRepositoryTable;
import ink.anh.repo.storage.KeyStore;
import ink.anh.repo.storage.RepoDataHandler;
import ink.anh.repo.storage.Repository;
import ink.anh.repo.storage.Slots;
import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RepoUtils {

	private static final AnhyRepo repoPlugin = AnhyRepo.getInstance();
    private static final RepoDataHandler dataHandler = new RepoDataHandler();
    private static final AbstractRepositoryTable repoTable = repoPlugin.getDatabaseManager().getRepositoryTable();

    
	// Метод для додавання масиву репозиторіїв до глобальної мапи
    public static void addRepositories(Player player, Repository[] repositories, boolean dbSave) {
        if (player != null && player.isOnline() && repositories != null) {
        	if (repoPlugin.getGlobalManager().isDebug()) Logger.error(repoPlugin, "repositories == null: " + (repositories == null));
        	dataHandler.addRepositoryData(player.getUniqueId(), repositories);
        	if (dbSave) {
        		repoTable.insertOrUpdateRepository(player.getUniqueId(), player.getName(), repositories);
        	}
        }
    }

    public static Repository[] getRepositories(Player player) {
    	
    	if (player == null) {
    		return null;
    	}
    	
    	UUID playerUUID = player.getUniqueId();
        Repository[] repositories = dataHandler.getRepositoryData(playerUUID);
        if (repositories == null ) {
        	repositories = repositoriesSorted(repoTable.getAllRepositories(playerUUID));
        	if (repositories != null) {
        		addRepositories(player, repositories, false);
        	}
        }
    	
    	if (repositories == null) {
            repositories = createDefaultGroupRepo(player);
    	}
    	
        return repositories;
    }

    public static Repository[] createNewRepositories(Player player, String[] args) {

        Repository[] repositories = RepoUtils.getRepositories(player);
        
        boolean hasNullRepositories = false;

        int index = -1;
        if (repositories != null && repositories.length > 0 && repositories.length <= Slots.values().length) {
            
            for (int i = 0; i < repositories.length; i++) {
                if (repositories[i] == null) {
                	index = i;
                    break;
                }
            }
            hasNullRepositories = index > -1;
        }
        
        if (hasNullRepositories) {
            Slots slot = Slots.values()[index];

            String groupName = null;
            if (args.length > 1) {
                groupName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            } else {
                Slots slotName = Slots.values()[index+1];
                groupName = slotName.name().toLowerCase();
            }

            // Створення нового репозиторію
            Map<KeyStore, ItemStack> storage = new TreeMap<>();
            Repository newRepo = new Repository(slot, groupName, storage);
            repositories[index] = newRepo;
            addRepositories(player, repositories, true);
            
        } else {
        	return null;
        }
        return repositories;
    }

    public static Repository[] createDefaultGroupRepo(Player player) {
        Repository[] repositories = new Repository[Slots.values().length];
        Repository defaultRepo = new Repository(repoPlugin, repoPlugin.getGlobalManager().getDefaultRepository(), null);
        if (repoPlugin.getGlobalManager().isDebug());
        repositories[0] = defaultRepo;
        
        if (repoPlugin.getGlobalManager().isDebug()) {

            for (Repository repo : repositories) {
                if (repo != null) {
                    Logger.warn(repoPlugin, "repo group name: " + repo.getGroupName());
                }
            }
        }

        addRepositories(player, repositories, true);
        return repositories;
    }

    public static Repository[] repositoriesSorted(Repository[] repositories) {
        if (repositories == null) {
            return null;
        }
        Repository[] sortedRepositories = new Repository[Slots.values().length];
        boolean hasRepositories = false;

        for (Repository repo : repositories) {
            if (repo != null) {
                int slotIndex = repo.getSlot().ordinal();
                if (slotIndex >= 0 && slotIndex < sortedRepositories.length) {
                    sortedRepositories[slotIndex] = repo;
                    hasRepositories = true;
                }
            }
        }

        return hasRepositories ? sortedRepositories : null;
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
                Map<KeyStore, ItemStack> storageClone = repo.getStorageClone();
                ItemStack item;
                List<String> lore = null;

                // Якщо мапа порожня або нульова, створюємо лист паперу
                if (storageClone == null || storageClone.isEmpty()) {
                    item = new ItemStack(Material.PAPER);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(ChatColor.AQUA + repo.getGroupName());
                        item.setItemMeta(meta);
                    }
                } else {
                    // В іншому випадку створюємо зачаровану книгу з лором
                    item = new ItemStack(Material.BOOK);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(ChatColor.AQUA + repo.getGroupName());
                        lore = storageClone.keySet().stream()
                                .map(KeyStore::displayName)
                                .collect(Collectors.toList());
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                    }

                    // Встановлюємо кількість предметів у книзі відповідно до кількості елементів у storage
                    int itemCount = storageClone.size();
                    item.setAmount(Math.min(itemCount, item.getMaxStackSize()));
                }

                content[i] = item;
                
                if (size <= i) break;
            }
        }
        
        return content;
    }
}
