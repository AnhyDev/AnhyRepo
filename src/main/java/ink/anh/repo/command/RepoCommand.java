package ink.anh.repo.command;

import java.util.concurrent.CompletableFuture;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import ink.anh.api.lingo.Translator;
import ink.anh.api.messages.MessageForFormatting;
import ink.anh.api.messages.MessageType;
import ink.anh.api.utils.LangUtils;
import ink.anh.repo.AnhyRepo;


public class RepoCommand extends Sender implements CommandExecutor {
	

	public RepoCommand(AnhyRepo repoPlugin) {
		super(repoPlugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {

        	CompletableFuture.runAsync(() -> {
                switch (args[0].toLowerCase()) {
                case "gui":
                    new RepoSubCommand(repoPlugin).openGui(sender, args);
                    break;
                case "rg":
                case "regroup":
                    new RepoSubCommand(repoPlugin).renameRepository(sender, args);
                    break;
                case "n":
                case "new":
                    new RepoSubCommand(repoPlugin).newGroup(sender, args);
                    break;
                case "a":
                case "add":
                    new RepoSubCommand(repoPlugin).addItemToRepository(sender, args);
                    break;
                case "rn":
                case "rename":
                    new RepoSubCommand(repoPlugin).renameItemInRepository(sender, args);
                    break;
                case "ri":
                case "reitem":
                    new RepoSubCommand(repoPlugin).replaceItemInRepository(sender, args);
                    break;
                case "rt":
                case "retext":
                    new RepoSubCommand(repoPlugin).updateItemLoreInRepository(sender, args);
                    break;
                case "remove":
                    new RepoSubCommand(repoPlugin).removeItemAndReindex(sender, args);
                    break;
                    
                    
                    
                case "reload":
                    reload(sender);
                    break;
                case "setlang":
                	new LangSubCommand(repoPlugin).setLang(sender, args);
                    break;
                case "getlang":
                	new LangSubCommand(repoPlugin).getLang(sender);
                    break;
                case "resetlang":
                	new LangSubCommand(repoPlugin).resetLang(sender);
                    break;
                default:
                    return;
                }
        	});
        }
		return true;
	}

    private void reload(CommandSender sender) {
    	String[] langs = checkPlayerPermissions(sender, "anhyrepo.reload");
	    if (langs != null && langs[0] == null) {
            return;
	    }
	    
        if (repoPlugin.getGlobalManager().reload()) {
            sendMessage(new MessageForFormatting("repo_language_reloaded ", null), MessageType.NORMAL, sender);
            return;
        }
        return;
    }

    private String[] checkPlayerPermissions(CommandSender sender, String permission) {
        // Checking if the command is executed by the console
        if (sender instanceof ConsoleCommandSender) {
            return null;
        }

        // Ініціалізація масиву з одним елементом null
        String[] langs = new String[] {null};

        if (sender instanceof Player) {
            Player player = (Player) sender;

            // We get languages for the player
            langs = LangUtils.getPlayerLanguage(player);

            // We check whether the player has permission
            if (!player.hasPermission(permission)) {
                sendMessage(new MessageForFormatting(Translator.translateKyeWorld(repoPlugin.getGlobalManager(), "repo_err_not_have_permission ", langs), null), MessageType.ERROR, sender);
                return langs;
            }
        }

        return langs;
    }
}
