package ink.anh.repo.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ink.anh.api.messages.Logger;
import ink.anh.api.messages.MessageForFormatting;
import ink.anh.api.messages.MessageType;
import ink.anh.repo.AnhyRepo;
import ink.anh.repo.gui.InventoryManager;

public class RepoSubCommand extends Sender {

	public RepoSubCommand(AnhyRepo repoPlugin) {
		super(repoPlugin);
	}

	public void openGui(CommandSender sender, String[] args) {
	    try {
	    	Player player = null;
			if (sender instanceof Player) {
				player = (Player) sender;
				new InventoryManager(repoPlugin).openRepoGroupInventory(player);
			}
	    } catch (Exception e) {
	        Logger.error(repoPlugin, translate(null, "repo_error_occurred_repository ") + e.getMessage());
	        e.printStackTrace();
	    }
	}

	
	
	
	public void zagotovka(CommandSender sender, String[] args) {

		String sendername = sender.getName();
		Player player = null;
		
		if (sender instanceof Player) {
			player = (Player) sender;
			if (!player.hasPermission("Permissions.FAMILY_ADMIN")) {
	            sendMessage(new MessageForFormatting("repo_err_not_have_permission", null), MessageType.WARNING, sender);
	            return;
			}
		} else if(!sendername.equalsIgnoreCase("CONSOLE") && player == null) {
            sendMessage(new MessageForFormatting("repo_err_not_have_permission", null), MessageType.WARNING, sender);
            return;
		}
	}
}
