package ink.anh.repo.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import ink.anh.api.items.ItemStackSerializer;
import ink.anh.api.lingo.Translator;
import ink.anh.api.utils.StringUtils;
import ink.anh.repo.AnhyRepo;
import net.md_5.bungee.api.ChatColor;

import java.lang.reflect.Type;

import org.bukkit.Material;

public class Repository {

    private static final int MAX_SIZE = 27;
    private Slots slot;
    private String groupName;
    private Map<KeyStore, ItemStack> storage = new TreeMap<>();

    public Repository(Slots slot, String groupName, Map<KeyStore, ItemStack> storage) {
        this.slot = slot;
        setGroupName(groupName);
        this.storage = new TreeMap<>(storage);
    }
    
    public Repository(AnhyRepo repoPlugin, Map<String, String> initialStorage, String[] langs) {
        this.slot = Slots.SLOT0;
        setGroupName(Translator.translateKyeWorld(repoPlugin.getGlobalManager(), "repo_basic_repository", langs));
        this.storage = new TreeMap<>();
        
        for (Map.Entry<String, String> entry : initialStorage.entrySet()) {
            this.addItem(Translator.translateKyeWorld(repoPlugin.getGlobalManager(), entry.getKey(), langs), entry.getValue(), null);
        }
    }
    
    public Repository(Slots slot, String groupName) {
        this.slot = slot;
        this.groupName = groupName;
        this.storage = new TreeMap<>();
    }

    public boolean addItem(String keyName, String value, ItemStack stack) {
        if (storage.size() >= MAX_SIZE) return false;
        
        keyName = StringUtils.colorize(keyName);
        value = StringUtils.colorize(value);

        int newIndex = 1;
        for (; newIndex <= storage.size(); newIndex++) {
            KeyStore tempKey = new KeyStore(newIndex, null);
            if (!storage.containsKey(tempKey)) {
                break;
            }
        }

        KeyStore key = new KeyStore(newIndex, keyName);
        
        Material material = Material.PAPER; // Default material
        if (stack != null && stack.getType() != Material.AIR) {
            material = stack.getType();
        } else if (value.startsWith("/")) {
            material = Material.COMMAND_BLOCK;
        } else if (value.length() > 10) {
        	
            material = Material.WRITABLE_BOOK;
        }

        ItemStack newItem = new ItemStack(material);
        ItemMeta meta = newItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(key.displayName());
            meta.setLore(Collections.singletonList(ChatColor.GREEN + value));
            newItem.setItemMeta(meta);
        }
        storage.put(key, newItem);
        return true;
    }

    public boolean updateItemStack(int index, ItemStack newItem) {
        KeyStore keyToUpdate = null;
        for (KeyStore key : storage.keySet()) {
            if (key.getIndex() == index) {
                keyToUpdate = key;
                break;
            }
        }

        if (keyToUpdate != null) {
            ItemStack currentItem = storage.get(keyToUpdate);
            ItemMeta currentMeta = currentItem.getItemMeta();
            ItemMeta newMeta = newItem.getItemMeta();

            if (newMeta != null && currentMeta != null) {
                newMeta.setDisplayName(currentMeta.getDisplayName());
                newMeta.setLore(currentMeta.getLore());
                newItem.setItemMeta(newMeta);
            }
            storage.put(keyToUpdate, newItem);
            return true;
        }
		return false;
    }

    public boolean updateItemLore(int index, String newLore) {
        KeyStore keyToUpdate = new KeyStore(index, "");
        ItemStack itemToUpdate = storage.get(keyToUpdate);
        newLore = StringUtils.colorize(newLore);

        if (itemToUpdate != null) {
            ItemMeta meta = itemToUpdate.getItemMeta();
            if (meta != null) {

                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GREEN + newLore);
                meta.setLore(lore);
                itemToUpdate.setItemMeta(meta);
                storage.put(keyToUpdate, itemToUpdate);
                return true;
            }
        }
        return false;
    }

    public boolean updateKeyName(int index, String newName) {
        KeyStore keyToUpdate = null;
        for (KeyStore key : storage.keySet()) {
            if (key.getIndex() == index) {
                keyToUpdate = key;
                break;
            }
        }

        newName = StringUtils.colorize(newName);
        KeyStore newKey = new KeyStore(index, newName);
        
        if (keyToUpdate != null) {
            ItemStack currentItem = storage.get(keyToUpdate);
            ItemMeta meta = currentItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(newKey.displayName());
                currentItem.setItemMeta(meta);
            }
            storage.remove(keyToUpdate);
            storage.put(newKey, currentItem);
            return true;
        }
		return false;
    }

    public void reindexKeyStores() {
        TreeMap<Integer, KeyStore> indexToKeyMap = new TreeMap<>();

        for (KeyStore key : storage.keySet()) {
            indexToKeyMap.put(key.getIndex(), key);
        }

        int expectedIndex = 1;
        Map<KeyStore, ItemStack> updatedStorage = new TreeMap<>();

        for (Map.Entry<Integer, KeyStore> entry : indexToKeyMap.entrySet()) {
            KeyStore currentKey = entry.getValue();
            ItemStack currentItemStack = storage.get(currentKey);

            if (entry.getKey() != expectedIndex) {
                KeyStore updatedKey = new KeyStore(expectedIndex, currentKey.getKeyName());

                if (currentItemStack != null) {
                    ItemMeta meta = currentItemStack.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(updatedKey.displayName());
                        currentItemStack.setItemMeta(meta);
                    }
                }
                updatedStorage.put(updatedKey, currentItemStack);
            } else {
                updatedStorage.put(currentKey, currentItemStack);
            }
            expectedIndex++;
        }

        storage.clear();
        storage.putAll(updatedStorage);
    }

    public boolean removeItem(int index) {
        KeyStore keyToRemove = null;
        for (KeyStore key : storage.keySet()) {
            if (key.getIndex() == index) {
                keyToRemove = key;
                break;
            }
        }

        if (keyToRemove != null) {
            storage.remove(keyToRemove);
            reindexKeyStores();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().create();
        Map<String, String> serializedStorage = new HashMap<>();
        for (Map.Entry<KeyStore, ItemStack> entry : storage.entrySet()) {
        	String key = entry.getKey().toString();
            String serializedItemStack = ItemStackSerializer.serializeItemStack(entry.getValue());
            serializedStorage.put(key, serializedItemStack);
        }

        Map<String, Object> fullData = new HashMap<>();
        fullData.put("slot", this.slot.name());
        fullData.put("groupName", this.groupName);
        fullData.put("storage", serializedStorage);

        return gson.toJson(fullData);
    }

    public static Repository fromString(String data) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> fullData = gson.fromJson(data, type);

        Slots slot = Slots.valueOf((String) fullData.get("slot"));
        String groupName = (String) fullData.get("groupName");

        Type storageType = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> serializedStorage = gson.fromJson(gson.toJson(fullData.get("storage")), storageType);

        Map<KeyStore, ItemStack> storage = new TreeMap<>();
        for (Map.Entry<String, String> entry : serializedStorage.entrySet()) {
        	KeyStore key = KeyStore.fromString(entry.getKey());
            ItemStack itemStack = ItemStackSerializer.deserializeItemStack(entry.getValue());
            storage.put(key, itemStack);
        }

        return new Repository(slot, groupName, storage);
    }

    public Slots getSlot() {
        return slot;
    }

    public void setSlot(Slots slot) {
        this.slot = slot;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = StringUtils.colorize(groupName);
    }

    public Map<KeyStore, ItemStack> getStorageClone() {
        return new TreeMap<>(storage);
    }

    public void setStorage(Map<KeyStore, ItemStack> newStorage) {
        if (newStorage.size() <= MAX_SIZE) {
            this.storage = new TreeMap<>(newStorage);
        } else {
            this.storage = new TreeMap<>();
            int count = 0;
            for (Map.Entry<KeyStore, ItemStack> entry : newStorage.entrySet()) {
                if (count++ < MAX_SIZE) {
                    this.storage.put(entry.getKey(), entry.getValue());
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Repository other = (Repository) obj;
        return slot == other.slot;
    }
}
