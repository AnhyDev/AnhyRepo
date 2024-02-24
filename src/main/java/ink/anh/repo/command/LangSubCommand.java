package ink.anh.repo.command;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ink.anh.api.messages.MessageForFormatting;
import ink.anh.api.messages.MessageType;
import ink.anh.api.utils.LangUtils;
import ink.anh.repo.AnhyRepo;

public class LangSubCommand extends Sender {

	public LangSubCommand(AnhyRepo repoPlugin) {
		super(repoPlugin);
	}

    public void setLang(CommandSender sender, String[] args) {
        // Check if the command is executed by a player
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Check if enough arguments are provided (minimum 2)
            if (args.length < 2) {
                sendMessage(new MessageForFormatting("repo_err_command_format /repo setlang <lang1> [lang2] [langX]", null), MessageType.WARNING, sender);
                return;
            }

            // Create an array to store language codes
            String[] newlangs = Arrays.copyOfRange(args, 1, args.length);
            
            // Attempt to set the player's language preferences
            int result = LangUtils.setLangs(player, newlangs);
            
            // Handling the result of the language setting operation
            if (result == 1) {
                // Successful operation
                String[] langs = LangUtils.getPlayerLanguage(player);
                sendMessage(new MessageForFormatting("repo_language_is_selected " + String.join(", ", langs), null), MessageType.NORMAL, sender);
            } else if (result == 0) {
                // Invalid language code length
                sendMessage(new MessageForFormatting("repo_err_language_code_2letters", null), MessageType.WARNING, sender);
            } else {
                // Other errors
                sendMessage(new MessageForFormatting("repo_err_invalid_language_code ", null), MessageType.WARNING, sender);
            }
        } else {
            // Command can only be executed by a player
            sendMessage(new MessageForFormatting("repo_err_command_only_player", null), MessageType.WARNING, sender);
        }
        return;
    }

    public void getLang(CommandSender sender) {
        // Check if the command is executed by a player
        if (sender instanceof Player) {
            Player player = (Player) sender;
            
            // Retrieve the player's current language settings
            String[] langs = LangUtils.getLangs(player);
            if (langs != null) {
                // Display the current language settings to the player
                sendMessage(new MessageForFormatting("repo_you_language " + String.join(", ", langs), null), MessageType.NORMAL, sender);
            } else {
                // No language settings found
                sendMessage(new MessageForFormatting("repo_you_have_not_set_language", null), MessageType.WARNING, sender);
            }
        } else {
            // Command can only be executed by a player
            sendMessage(new MessageForFormatting("repo_err_command_only_player", null), MessageType.WARNING, sender);
        }
        return;
    }

    public void resetLang(CommandSender sender) {
        // Check if the command is executed by a player
        if (sender instanceof Player) {
            Player player = (Player) sender;
            
            // Attempt to reset the player's language settings
            int result = LangUtils.resetLangs(player);
            
            // Handling the result of the reset operation
            if (result == 1) {
                // Successful reset
                sendMessage(new MessageForFormatting("repo_cleared_the_language ", null), MessageType.NORMAL, sender);
            } else {
                // No language settings to reset
                sendMessage(new MessageForFormatting("repo_you_have_not_set_language", null), MessageType.WARNING, sender);
            }
        } else {
            // Command can only be executed by a player
            sendMessage(new MessageForFormatting("repo_err_command_only_player", null), MessageType.WARNING, sender);
        }
        return;
    }
}
