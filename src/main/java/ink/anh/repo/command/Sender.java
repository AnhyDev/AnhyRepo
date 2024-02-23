package ink.anh.repo.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ink.anh.api.LibraryManager;
import ink.anh.api.lingo.Translator;
import ink.anh.api.messages.MessageChat;
import ink.anh.api.messages.MessageForFormatting;
import ink.anh.api.messages.MessageType;
import ink.anh.api.utils.LangUtils;
import ink.anh.repo.AnhyRepo;

public abstract class Sender {

	public AnhyRepo repoPlugin;
	public LibraryManager libraryManager;

	public Sender(AnhyRepo repoPlugin) {
		this.repoPlugin = repoPlugin;
		this.libraryManager = repoPlugin.getGlobalManager();
	}

	public void sendMessage(MessageForFormatting textForFormatting, MessageType type, CommandSender... senders) {
		sendMessage(textForFormatting, type, true, senders);
	}

	public void sendMessage(MessageForFormatting textForFormatting, MessageType type, boolean addPluginName, CommandSender... senders) {
	    for (CommandSender sender : senders) {
	    	if (sender != null) {
	    		MessageChat.sendMessage(repoPlugin.getGlobalManager(), sender, textForFormatting, type, addPluginName);
	    	}
	    }
	}
	
	public String[] getLangs(CommandSender sender) {
		return sender instanceof Player ? LangUtils.getPlayerLanguage((Player) sender) : new String[] {libraryManager.getDefaultLang()};
	}
	
	public String translate(CommandSender sender, String message) {
		return Translator.translateKyeWorld(libraryManager, message, (sender != null ? getLangs(sender) : null));
	}
}
