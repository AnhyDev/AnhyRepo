package ink.anh.repo.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ink.anh.api.lingo.Translator;
import ink.anh.api.utils.LangUtils;
import ink.anh.repo.AnhyRepo;
import ink.anh.repo.storage.Repository;
import ink.anh.repo.utils.RepoUtils;

public class InventoryManager {

	private final AnhyRepo repoPlugin;
	
    public InventoryManager(AnhyRepo repoPlugin) {
		this.repoPlugin = repoPlugin;
	}

	// Метод для відкриття RepoGroup інвентаря
    public void openRepoGroupInventory(Player player) {
    	String guiName = Translator.translateKyeWorld(repoPlugin.getGlobalManager(), "repo_group_holder", LangUtils.getPlayerLanguage(player));
        RepoGroupHolder holder = new RepoGroupHolder(guiName);
        Repository[] repositories = RepoUtils.getRepositories(player);
        ItemStack[] itemssRepo = RepoUtils.contentRepoGroup(repositories);
        
    	Bukkit.getScheduler().runTask(repoPlugin, () -> {
            holder.addItems(itemssRepo);
            player.openInventory(holder.getInventory());
    	});
    }

    // Метод для відкриття Repository інвентаря
    public void openRepositoryInventory(Player player, int slotIndex) {
        Repository repo = RepoUtils.getRepositories(player)[slotIndex];
        ItemStack[] content = RepoUtils.contentRepository(repo);
    	String guiName = Translator.translateKyeWorld(repoPlugin.getGlobalManager(), repo.getGroupName(), LangUtils.getPlayerLanguage(player));
        RepositoryHolder holder = new RepositoryHolder(guiName);
        
    	Bukkit.getScheduler().runTask(repoPlugin, () -> {
    		holder.addItems(content);
            player.openInventory(holder.getInventory());
    	});
    }
}
