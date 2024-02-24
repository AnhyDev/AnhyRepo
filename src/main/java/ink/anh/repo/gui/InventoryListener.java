package ink.anh.repo.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ink.anh.api.lingo.Translator;
import ink.anh.api.messages.MessageComponents;
import ink.anh.api.messages.Messenger;
import ink.anh.api.utils.LangUtils;
import ink.anh.repo.AnhyRepo;
import net.md_5.bungee.api.ChatColor;

public class InventoryListener implements Listener {

	private final AnhyRepo repoPlugin;
	
    public InventoryListener(AnhyRepo repoPlugin) {
		this.repoPlugin = repoPlugin;
	}
	
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof RepoGroupHolder) {
            handleRepoGroupClick(event);
        } else if (holder instanceof RepositoryHolder) {
            handleRepositoryClick(event);
        }
    }

    private void handleRepoGroupClick(InventoryClickEvent event) {
        int clickedSlot = event.getRawSlot();

        if (clickedSlot < event.getView().getTopInventory().getSize()) {
            event.setCancelled(true);

            ItemStack item = event.getView().getItem(clickedSlot);
            if (item == null || item.getType() == Material.AIR) {
            	return;
            }
            
            ItemMeta meta = item.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            new InventoryManager(repoPlugin).openRepositoryInventory(player, clickedSlot);
        }
    }

    private void handleRepositoryClick(InventoryClickEvent event) {
        int clickedSlot = event.getRawSlot();

        if (clickedSlot < event.getView().getTopInventory().getSize()) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            

            if (event.getClick() == ClickType.LEFT) {
                ItemStack item = event.getView().getItem(clickedSlot);
                if (item == null || item.getType() == Material.AIR) {
                	return;
                }
                
                ItemMeta meta = item.getItemMeta();
                if (meta == null || !meta.hasDisplayName() || !meta.hasLore()) {
                    return;
                }
                
                String nameText = meta.getDisplayName();
                String textToInsert = meta.getLore().get(0);
                String preText = Translator.translateKyeWorld(repoPlugin.getGlobalManager(), "repo_click_text_here \n", LangUtils.getPlayerLanguage(player));
                String hoverText = textToInsert;

                // Створення компоненту для спливаючого повідомлення
                MessageComponents hoverComponents = MessageComponents.builder()
                        .content(preText)
                        .color("YELLOW") // Задаємо жовтий колір для першого рядка
                        .append(MessageComponents.builder().content("\n" + hoverText).build()) // Додаємо другий рядок без змін
                        .build();

                // Створення основного компоненту та додавання до нього спливаючого повідомлення
                MessageComponents messageComponents = MessageComponents.builder()
                        .content(nameText) // Текст, який буде відображатися зазвичай
                        .hoverComponent(hoverComponents) // Додаємо спливаюче повідомлення
                        .insertTextChat(ChatColor.stripColor(textToInsert))
                        .color("GOLD")
                        .build();



                
                Messenger.sendMessage(AnhyRepo.getInstance(), player, messageComponents, 
                		Translator.translateKyeWorld(repoPlugin.getGlobalManager(), "repo_failed_to_send_component",  LangUtils.getPlayerLanguage(player)));
                player.closeInventory();
            } else {
            	new InventoryManager(repoPlugin).openRepoGroupInventory(player);
            }
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        
        // Перевірка, чи інвентар належить до одного з кастомних інвентарів
        if (holder instanceof RepoGroupHolder || holder instanceof RepositoryHolder) {
            // Перевіряємо, чи залучені до перетягування слоти належать верхньому інвентарю
            boolean cancel = event.getRawSlots().stream().anyMatch(slot -> 
                slot < event.getView().getTopInventory().getSize()
            );
            
            if (cancel) {
                event.setCancelled(true);
            }
        }
    }

}