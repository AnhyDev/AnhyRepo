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
import ink.anh.repo.utils.RepoUtils;

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

	public void newGroup(CommandSender sender, String[] args) {
	    try {
	        Player player = null;
	        if (sender instanceof Player) {
				player = (Player) sender;
				Repository[] getRepos = RepoUtils.createNewRepositories(player, args);
	        	if (getRepos != null) {
					new InventoryManager(repoPlugin).openRepoGroupInventory(player);
	        	}
	        }
	    } catch (Exception e) {
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
            sendMessage(new MessageForFormatting("repo_err_command_only_player", null), MessageType.WARNING, sender);
            sender.sendMessage("repo_err_command_format /repo <regroup> <index> <new group name> (<index>: 1,2,3,4,5,6,7,8,9)");
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

            sendMessage(new MessageForFormatting("repo_group_successfully_renamed", null), MessageType.WARNING, sender);

        } catch (NumberFormatException e) {
            sendMessage(new MessageForFormatting("repo_err_index_no_number", null), MessageType.WARNING, sender);
        } catch (Exception e) {
            sendMessage(new MessageForFormatting("repo_err_group_rename", null), MessageType.WARNING, sender);
            e.printStackTrace();
        }
    }


    public void addItemToRepository(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Цю команду може виконати лише гравець.");
            return;
        }

        if (args.length < 4) { // Переконуємося, що є достатньо аргументів для індексу, назви та лору
            sender.sendMessage("Недостатньо аргументів. Використання: /команда <індекс_репозиторію> <назва>/<лор>");
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
                sender.sendMessage("Неправильний формат для назви та лору. Потрібно використати слеш (/) для розділення.");
                return;
            }
            String itemName = parts[0].trim();
            String itemLore = parts[1].trim();

            Repository[] repositories = RepoUtils.getRepositories(player);
            Repository repo = getRepository(repositories, index);
            repo.addItem(itemName, itemLore, null);

            // Оновлення даних репозиторіїв
            RepoUtils.addRepositories(player, repositories, true);

            sender.sendMessage("Елемент успішно додано до репозиторію.");
        } catch (NumberFormatException e) {
            sender.sendMessage("Неправильний формат індексу. Індекс повинен бути числом.");
        } catch (Exception e) {
            sender.sendMessage("Сталася помилка при додаванні елемента в репозиторій: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void replaceItemInRepository(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Цю команду може виконати лише гравець.");
            return;
        }

        if (args.length < 3) {
            sender.sendMessage("Недостатньо аргументів. Використання: /repo reitem <індекс_репозиторію> <індекс_предмету>");
            return;
        }

        Player player = (Player) sender;
        try {
            int repoIndex = Integer.parseInt(args[1]) - 1;
            int itemIndex = Integer.parseInt(args[2]);

            Repository[] repositories = RepoUtils.getRepositories(player);
            Repository repo = getRepository(repositories, repoIndex);
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            if (itemInHand == null || itemInHand.getType() == Material.AIR) {
                sender.sendMessage("У вас немає предмету в руці.");
                return;
            }

            if (repo.updateItemStack(itemIndex, itemInHand)) {
                sender.sendMessage("Предмет успішно замінено.");
            } else {
                sender.sendMessage("Помилка при заміні предмету.");
            }
        } catch (IllegalArgumentException e) {
            sender.sendMessage(e.getMessage());
        } catch (Exception e) {
            sender.sendMessage("Сталася помилка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void renameItemInRepository(CommandSender sender, String[] args) {
    	Repository[] repositories = updateText(sender, args, (repo, index, text) -> repo.updateKeyName(index, text));

        // Оновлення даних репозиторіїв
        if (repositories != null) RepoUtils.addRepositories((Player) sender, repositories, true);
    }

    public void updateItemLoreInRepository(CommandSender sender, String[] args) {
    	Repository[] repositories = updateText(sender, args, (repo, index, text) -> repo.updateItemLore(index, text));

        // Оновлення даних репозиторіїв
    	if (repositories != null) RepoUtils.addRepositories((Player) sender, repositories, true);
    }

    public Repository[] updateText(CommandSender sender, String[] args, RepositoryUpdateAction action) {
    	Repository[] repositories = null;
        if (!(sender instanceof Player)) {
            sender.sendMessage("Цю команду може виконати лише гравець.");
            return repositories;
        }

        if (args.length < 4) {
            sender.sendMessage("Недостатньо аргументів. Використання: /repo <команда> <індекс_репозиторію> <індекс_предмету> <текст>");
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
                sender.sendMessage("Оновлення успішне.");
            } else {
                sender.sendMessage("Помилка при оновленні.");
            }
        } catch (IllegalArgumentException e) {
            sender.sendMessage(e.getMessage());
        } catch (Exception e) {
            sender.sendMessage("Сталася помилка: " + e.getMessage());
            e.printStackTrace();
        }
		return repositories;
    }

    public void removeItemAndReindex(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Цю команду може виконати лише гравець.");
            return;
        }

        if (args.length < 3) {
            sender.sendMessage("Недостатньо аргументів. Використання: /repo reitem <індекс_репозиторію> <індекс_предмету>");
            return;
        }

        Player player = (Player) sender;
        try {
            int repoIndex = Integer.parseInt(args[1]) - 1;
            int itemIndex = Integer.parseInt(args[2]);
            Repository[] repositories = RepoUtils.getRepositories(player);
            
            if (repoIndex < 0 || repoIndex >= repositories.length || repositories[repoIndex] == null) {
                sender.sendMessage("Репозиторій з таким індексом не існує.");
                return;
            }

            Repository repo = repositories[repoIndex];
            repo.removeItem(itemIndex);

            // Оновлюємо дані репозиторіїв
            RepoUtils.addRepositories(player, repositories, true);

            sender.sendMessage("Сховище репозиторію оновлено та переіндексовано.");
        } catch (NumberFormatException e) {
            sender.sendMessage("Неправильний формат індексу. Індекс повинен бути числом.");
        } catch (Exception e) {
            sender.sendMessage("Сталася помилка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Repository getRepository(Repository[] repositories, int index) throws IllegalArgumentException {
        if (index < 0 || index >= repositories.length || repositories[index] == null) {
            throw new IllegalArgumentException("Репозиторій з таким індексом не існує.");
        }
        return repositories[index];
    }

    @FunctionalInterface
    public interface RepositoryUpdateAction {
        boolean update(Repository repository, int index, String text);
    }
}
