package ink.anh.repo.storage;

import ink.anh.api.AnhyLibAPI;
import ink.anh.api.DataHandler;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class RepoDataHandler extends DataHandler {

	private static final String REPO_DATA_KEY = "repo";

    public void addRepositoryData(UUID uuid, Repository[] repos) {
        // Зберігаємо масив об'єктів безпосередньо в глобальну мапу
        addData(uuid, REPO_DATA_KEY, repos);
    }

    public Repository[] getRepositoryData(UUID uuid) {
        Object data = getData(uuid, REPO_DATA_KEY);
        if (data instanceof Repository[]) {
            return (Repository[]) data;
        }
        return null;
    }

    public void removeRepositoryData(UUID uuid) {
        removeData(uuid, REPO_DATA_KEY);
    }

    public boolean hasRepositoryData(UUID uuid) {
        return getRepositoryData(uuid) != null && getData(uuid, REPO_DATA_KEY) instanceof Repository[] && getRepositoryData(uuid).length > 0;
    }

    public void removeAllRepositoryData() {
        Map<UUID, Map<String, Object>> globalDataMap = AnhyLibAPI.getInstance().getGlobalDataMap();

        for (Entry<UUID, Map<String, Object>> entry : globalDataMap.entrySet()) {
            Map<String, Object> subMap = entry.getValue();
            // Перевіряємо, чи існує ключ REPO_DATA_KEY у поточній суб-мапі, та видаляємо його
            if (subMap.containsKey(REPO_DATA_KEY)) {
                subMap.remove(REPO_DATA_KEY);
            }

            // Якщо після видалення суб-мапа стала порожньою, можемо видалити її з глобальної мапи
            if (subMap.isEmpty()) {
                globalDataMap.remove(entry.getKey());
            }
        }
    }
}
