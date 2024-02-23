package ink.anh.repo.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class RepoGroupHolder implements InventoryHolder {
    private Inventory inventory;

    public RepoGroupHolder(String repoName) {
        inventory = Bukkit.createInventory(this, 9, repoName);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void addItems(ItemStack[] content) {
    	inventory.setContents(content);
    }
}
