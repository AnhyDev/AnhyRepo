package ink.anh.repo.storage;

import java.util.Objects;

import com.google.gson.Gson;

import net.md_5.bungee.api.ChatColor;

public class KeyStore implements Comparable<KeyStore> {

	private int index;
	private String keyName;
	
	public KeyStore(int index, String keyName) {
		setIndex(index);
		setKeyName(keyName);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
	    if (index >= 1 && index <= 27) {
	        this.index = index;
	    } else {
	        throw new IllegalArgumentException("Index must be between 1 and 27, inclusive.");
	    }
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	
	public String displayName() {
		return ChatColor.GOLD + String.valueOf(index) + ". " + ChatColor.BLUE + keyName;
	}

    @Override
    public int compareTo(KeyStore other) {
        return Integer.compare(this.index, other.index);
    }

	@Override
	public int hashCode() {
		return Objects.hash(index);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyStore other = (KeyStore) obj;
		return index == other.index;
	}
	
    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static KeyStore fromString(String data) {
        Gson gson = new Gson();
        return gson.fromJson(data, KeyStore.class);
    }
}
