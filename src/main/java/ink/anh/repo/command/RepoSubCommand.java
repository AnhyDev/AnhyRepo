package ink.anh.repo.command;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ink.anh.api.messages.Logger;
import ink.anh.api.messages.MessageForFormatting;
import ink.anh.api.messages.MessageType;
import ink.anh.repo.AnhyRepo;
import ink.anh.repo.gui.InventoryManager;
import ink.anh.repo.storage.Repository;
import ink.anh.repo.storage.Slots;
import ink.anh.repo.utils.RepoUtils;

public class RepoSubCommand extends Sender {

	public RepoSubCommand(AnhyRepo repoPlugin) {
		super(repoPlugin);
	}

	public void openGui(CommandSender sender, String[] args) {
    	Player player = null;
	    try {
			if (sender instanceof Player) {
				player = (Player) sender;

		        if (args.length > 1) {
		            int index = Integer.parseInt(args[1]) - 1;

		            Repository[] repositories = RepoUtils.getRepositories(player);
		            
		            boolean hasRepositories = false;

		            if (repositories != null && repositories.length > 0 && repositories.length <= Slots.values().length) {
		                
		                for (int i = 0; i < repositories.length; i++) {
		                    if (index == i && repositories[i] != null) {
		                    	hasRepositories = true;
		                        break;
		                    }
		                }
		            }
		            if (hasRepositories) {
		            	new InventoryManager(repoPlugin).openRepositoryInventory(player, index);
			            return;
		            }
		        }
				new InventoryManager(repoPlugin).openRepoGroupInventory(player);
			}
	    } catch (NumberFormatException e) {
	    	if (player != null) {
	    		new InventoryManager(repoPlugin).openRepoGroupInventory(player);
	    	} else {
	    		sendMessage(new MessageForFormatting("repo_err_command_only_player", null), MessageType.WARNING, sender);
	    	}
        } catch (Exception e) {
	        Logger.error(repoPlugin, translate(null, "repo_error_occurred_repository ") + e.getMessage());
	        e.printStackTrace();
	    }
	}

	public void newGroup(CommandSender sender, String[] args) {
	    try {
	        Player player = null;
	        if (sender instanceof Player) {
				player = (Player) sender;
				Repository[] getRepos = RepoUtils.createNewRepositories(player, args);
	        	if (getRepos != null) {
					new InventoryManager(repoPlugin).openRepoGroupInventory(player);
	        	} else {
	                sendMessage(new MessageForFormatting("repo_error_update_failed", null), MessageType.WARNING, sender);
	        	}
	        }
	    } catch (Exception e) {
            sendMessage(new MessageForFormatting("repo_error_occurred_repository " + e.getMessage(), null), MessageType.WARNING, sender);
	        Logger.error(repoPlugin, translate(null, "repo_error_occurred_repository ") + e.getMessage());
	        e.printStackTrace();
	    }
	}

    public void renameRepository(CommandSender sender, String[] args) {
        // Переконуємося, що команду виконує гравець
        if (!(sender instanceof Player)) {
            sendMessage(new MessageForFormatting("repo_err_command_only_player", null), MessageType.WARNING, sender);
            return;
        }

        // Переконуємося, що достатньо аргументів
        if (args.length < 3) {
            sendMessage(new MessageForFormatting("repo_err_command_format /repo <regroup> <repo_index> <new group name>", null), MessageType.WARNING, sender);
            return;
        }

        Player player = (Player) sender;
        try {
            int index = Integer.parseInt(args[1]) - 1;
            Repository[] repositories = RepoUtils.getRepositories(player);

            // Перевірка існування репозиторію за індексом
            if (index < 0 || index >= repositories.length || repositories[index] == null) {
                sendMessage(new MessageForFormatting("repo_err_index_not_found", null), MessageType.WARNING, sender);
                return;
            }

            // Об'єднання аргументів для створення нової назви
            String newName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

            // Перейменування репозиторію
            repositories[index].setGroupName(newName);
            RepoUtils.addRepositories(player, repositories, true);

            sendMessage(new MessageForFormatting("repo_group_successfully_renamed", null), MessageType.IMPORTANT, sender);

        } catch (NumberFormatException e) {
            sendMessage(new MessageForFormatting("repo_err_invalid_index_format", null), MessageType.WARNING, sender);
        } catch (Exception e) {
            sendMessage(new MessageForFormatting("repo_err_group_rename", null), MessageType.WARNING, sender);
            e.printStackTrace();
        }
    }


    public void addItemToRepository(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(new MessageForFormatting("repo_err_command_only_player", null), MessageType.WARNING, sender);
            return;
        }

        if (args.length < 4) {
            sendMessage(new MessageForFormatting("repo_err_command_only_player /repo add <repo_index> <name>/<text> (/ar a <i> <n>/<text>)", null), MessageType.WARNING, sender);
            return;
        }

        Player player = (Player) sender;
        try {
            int index = Integer.parseInt(args[1]) - 1; // Вирахування індексу
            // Об'єднання всіх аргументів в один рядок, починаючи з третього аргументу
            String combinedArgs = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            // Розділення рядка на назву та лор за допомогою слеша
            String[] parts = combinedArgs.split("/", 2);
            if (parts.length < 2) {
                sendMessage(new MessageForFormatting("repo_err_invalid_name_lore_format", null), MessageType.WARNING, sender);
                return;
            }
            String itemName = parts[0].trim();
            String itemLore = parts[1].trim();

            Repository[] repositories = RepoUtils.getRepositories(player);
            Repository repo = getRepository(repositories, index);
            
            if (!repo.addItem(itemName, itemLore, null)) {
                sendMessage(new MessageForFormatting("repo_error_update_failed", null), MessageType.WARNING, sender);
                return;
            }

            // Оновлення даних репозиторіїв
            RepoUtils.addRepositories(player, repositories, true);

            sendMessage(new MessageForFormatting("repo_success_item_added", null), MessageType.IMPORTANT, sender);
        } catch (NumberFormatException e) {
            sendMessage(new MessageForFormatting("repo_err_invalid_index_format", null), MessageType.WARNING, sender);
        } catch (Exception e) {
            sendMessage(new MessageForFormatting("repo_error_update_failed", null), MessageType.WARNING, sender);
            e.printStackTrace();
        }
    }

    public void replaceItemInRepository(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            
            sendMessage(new MessageForFormatting("repo_err_command_only_player", null), MessageType.WARNING, sender);
            return;
        }

        if (args.length < 3) {
            sendMessage(new MessageForFormatting("repo_err_command_format /repo reitem <repo_index> <item_index>", null), MessageType.WARNING, sender);
            return;
        }

        Player player = (Player) sender;
        try {
            int repoIndex = Integer.parseInt(args[1]) - 1;
            int itemIndex = Integer.parseInt(args[2]);

            Repository[] repositories = RepoUtils.getRepositories(player);
            Repository repo = getRepository(repositories, repoIndex);
            
            ItemStack rawItem = player.getInventory().getItemInMainHand();
            if (rawItem == null || rawItem.getType() == Material.AIR) {
                sendMessage(new MessageForFormatting("repo_err_item_not_in_hand", null), MessageType.WARNING, sender);
                return;
            }
            
            Material itemType = rawItem.getType();
            ItemStack cloneItemInHand = new ItemStack(itemType);

            if (repo.updateItemStack(itemIndex, cloneItemInHand)) {
                sendMessage(new MessageForFormatting("repo_success_item_replaced", null), MessageType.IMPORTANT, sender);
            } else {
                sendMessage(new MessageForFormatting("repo_error_item_replace_failed", null), MessageType.WARNING, sender);
            }
        } catch (NumberFormatException e) {
            sendMessage(new MessageForFormatting("repo_err_invalid_index_format", null), MessageType.WARNING, sender);
        } catch (IllegalArgumentException e) {
        	
        } catch (Exception e) {
            sendMessage(new MessageForFormatting("repo_error_update_failed " + e.getMessage(), null), MessageType.WARNING, sender);
            e.printStackTrace();
        }
    }

    public void renameItemInRepository(CommandSender sender, String[] args) {
    	Repository[] repositories = updateText(sender, args, (repo, index, text) -> repo.updateKeyName(index, text));

        // Оновлення даних репозиторіїв
        if (repositories != null) {
        	RepoUtils.addRepositories((Player) sender, repositories, true);
        } else {
        	sendMessage(new MessageForFormatting("repo_error_update_failed", null), MessageType.WARNING, sender);
        }
    }

    public void updateItemLoreInRepository(CommandSender sender, String[] args) {
    	Repository[] repositories = updateText(sender, args, (repo, index, text) -> repo.updateItemLore(index, text));

        // Оновлення даних репозиторіїв
        if (repositories != null) {
        	RepoUtils.addRepositories((Player) sender, repositories, true);
        } else {
        	sendMessage(new MessageForFormatting("repo_error_update_failed", null), MessageType.WARNING, sender);
        }
    }

    public Repository[] updateText(CommandSender sender, String[] args, RepositoryUpdateAction action) {
    	Repository[] repositories = null;
        if (!(sender instanceof Player)) {
            
            sendMessage(new MessageForFormatting("repo_err_command_only_player", null), MessageType.WARNING, sender);
            return repositories;
        }

        if (args.length < 4) {
            sendMessage(new MessageForFormatting("repo_err_command_format /repo rename(retext) <repo_index> <item_index> <new_text>", null), MessageType.WARNING, sender);
            return repositories;
        }

        Player player = (Player) sender;
        try {
            int repoIndex = Integer.parseInt(args[1]) - 1;
            int itemIndex = Integer.parseInt(args[2]);

            repositories = RepoUtils.getRepositories(player);
            Repository repo = getRepository(repositories, repoIndex);

            // Об'єднання всіх аргументів в один рядок, починаючи з четвертого аргументу
            String combinedArgs = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

            if (action.update(repo, itemIndex, combinedArgs)) {
                sendMessage(new MessageForFormatting("repo_success_update", null), MessageType.IMPORTANT, sender);
            } else {
                sendMessage(new MessageForFormatting("repo_error_update_failed", null), MessageType.WARNING, sender);
            }
        } catch (NumberFormatException e) {
            sendMessage(new MessageForFormatting("repo_err_invalid_index_format", null), MessageType.WARNING, sender);
        } catch (IllegalArgumentException e) {

        } catch (Exception e) {
            sendMessage(new MessageForFormatting("repo_error_update_failed " + e.getMessage(), null), MessageType.WARNING, sender);
            e.printStackTrace();
        }
		return repositories;
    }

    public void removeItemAndReindex(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            
            sendMessage(new MessageForFormatting("repo_err_command_only_player", null), MessageType.WARNING, sender);
            return;
        }

        if (args.length < 3) {
            sendMessage(new MessageForFormatting("repo_err_command_format /repo remove <repo_index> <item_index>", null), MessageType.WARNING, sender);
            return;
        }

        Player player = (Player) sender;
        try {
            int repoIndex = Integer.parseInt(args[1]) - 1;
            int itemIndex = Integer.parseInt(args[2]);
            Repository[] repositories = RepoUtils.getRepositories(player);
            
            if (repoIndex < 0 || repoIndex >= repositories.length || repositories[repoIndex] == null) {
                sendMessage(new MessageForFormatting("repo_err_index_not_found", null), MessageType.WARNING, sender);
                return;
            }

            Repository repo = repositories[repoIndex];
            repo.removeItem(itemIndex);

            // Оновлюємо дані репозиторіїв
            RepoUtils.addRepositories(player, repositories, true);

            sendMessage(new MessageForFormatting("repo_success_item_deleted_reindexed", null), MessageType.IMPORTANT, sender);
        } catch (NumberFormatException e) {
            sendMessage(new MessageForFormatting("repo_err_invalid_index_format", null), MessageType.WARNING, sender);
        } catch (IllegalArgumentException e) {

        } catch (Exception e) {
            sendMessage(new MessageForFormatting("repo_error_update_failed " + e.getMessage(), null), MessageType.WARNING, sender);
            e.printStackTrace();
        }
    }

    private Repository getRepository(Repository[] repositories, int index) throws IllegalArgumentException {
        if (index < 0 || index >= repositories.length || repositories[index] == null) {
            throw new IllegalArgumentException("The repository with this index does not exist.");
        }
        return repositories[index];
    }

    @FunctionalInterface
    public interface RepositoryUpdateAction {
        boolean update(Repository repository, int index, String text);
    }
}
