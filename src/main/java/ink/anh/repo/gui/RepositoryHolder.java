package ink.anh.repo.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class RepositoryHolder implements InventoryHolder {
    private Inventory inventory;

    public RepositoryHolder(String repoName) {
        // Створення інвентаря на 27 слотів з назвою "Repository"
        inventory = Bukkit.createInventory(this, 27, repoName);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void addItems(ItemStack[] content) {
    	inventory.setContents(content);
    }
}
