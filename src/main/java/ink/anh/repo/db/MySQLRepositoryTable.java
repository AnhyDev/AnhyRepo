package ink.anh.repo.db;

import ink.anh.repo.storage.Repository;
import ink.anh.repo.storage.Slots;
import ink.anh.repo.utils.OtherUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLRepositoryTable extends AbstractRepositoryTable {

    private final String tablePrefix;

    public MySQLRepositoryTable(MySQLDatabaseManager dbManager, String tablePrefix) {
    	super(dbManager);
        this.tablePrefix = tablePrefix;
    }

    @Override
    public void initialize() {
        // Створення таблиці
        String createTableSql = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "repositories (" +
                     "player_uuid VARCHAR(36) PRIMARY KEY, " +
                     "loverName VARCHAR(255) UNIQUE, " +
                     "slot0 TEXT, slot1 TEXT, slot2 TEXT, " +
                     "slot3 TEXT, slot4 TEXT, slot5 TEXT, " +
                     "slot6 TEXT, slot7 TEXT, slot8 TEXT" + // Видалена зайва кома
                     ");";
        // Створення індексу
        String createIndexSql = "CREATE INDEX IF NOT EXISTS idx_loverName ON " + tablePrefix + dbName + " (loverName);";

        try (Connection conn = dbManager.getConnection()) {
            // Виконання команди створення таблиці
            try (PreparedStatement ps = conn.prepareStatement(createTableSql)) {
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            // Виконання команди створення індексу
            try (PreparedStatement ps = conn.prepareStatement(createIndexSql)) {
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertOrUpdateRepository(UUID playerUuid, String loverName, Repository repository) {
    	loverName = loverName.toLowerCase();
        String fieldName = repository.getSlot().name().toLowerCase();
        String sql = "INSERT INTO " + tablePrefix + "repositories (player_uuid, " + fieldName + ") VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE " + fieldName + " = VALUES(" + fieldName + ");";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUuid.toString());
            ps.setString(2, OtherUtils.encryptAndEncodeBase64(repository.toString(), playerUuid.toString()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertOrUpdateRepository(UUID playerUuid, String loverName, Repository[] repositories) {
    	loverName = loverName.toLowerCase();
        String sql = "INSERT INTO " + tablePrefix + "repositories (player_uuid, loverName, " +
                     "slot0, slot1, slot2, slot3, slot4, slot5, slot6, slot7, slot8) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "loverName = VALUES(loverName), slot0 = VALUES(slot0), slot1 = VALUES(slot1), " +
                     "slot2 = VALUES(slot2), slot3 = VALUES(slot3), slot4 = VALUES(slot4), " +
                     "slot5 = VALUES(slot5), slot6 = VALUES(slot6), slot7 = VALUES(slot7), slot8 = VALUES(slot8);";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUuid.toString());
            ps.setString(2, loverName);
            // Заповнення значень для кожного слота
            for (int i = 0; i < Slots.values().length; i++) {
                if (i < repositories.length && repositories[i] != null) {
                    ps.setString(i + 3, OtherUtils.encryptAndEncodeBase64(repositories[i].toString(), playerUuid.toString()));
                } else {
                    ps.setString(i + 3, null);
                }
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Repository[] getAllRepositoriesByLoverName(String loverName) {
    	loverName = loverName.toLowerCase();
        String sql = "SELECT * FROM " + tablePrefix + "repositories WHERE loverName = ?;";
        Repository[] repositories = new Repository[Slots.values().length];

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loverName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                for (Slots slot : Slots.values()) {
                    String json = rs.getString(slot.name().toLowerCase());
                    if (json != null && !json.isEmpty()) {
                        repositories[slot.ordinal()] = Repository.fromString(OtherUtils.decodeAndDecryptBase64(json, rs.getString("player_uuid")));
                    } else {
                        repositories[slot.ordinal()] = null; // Якщо дані відсутні, зберігаємо як null
                    }
                }
                return repositories;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Повертаємо null, якщо немає записів з вказаним loverName
    }

    @Override
    public Repository[] getAllRepositories(UUID playerUuid) {
        String sql = "SELECT * FROM " + tablePrefix + "repositories WHERE player_uuid = ?;";
        Repository[] repositories = new Repository[Slots.values().length];

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                for (Slots slot : Slots.values()) {
                    String json = rs.getString(slot.name().toLowerCase());
                    if (json != null && !json.isEmpty()) {
                        repositories[slot.ordinal()] = Repository.fromString(OtherUtils.decodeAndDecryptBase64(json, playerUuid.toString()));
                    }
                }
                return repositories;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean clearRepository(UUID playerUuid) {
        String sql = "UPDATE " + tablePrefix + "repositories SET " +
                     "slot0 = NULL, slot1 = NULL, slot2 = NULL, " +
                     "slot3 = NULL, slot4 = NULL, slot5 = NULL, " +
                     "slot6 = NULL, slot7 = NULL, slot8 = NULL " +
                     "WHERE player_uuid = ?;";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUuid.toString());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteRepository(UUID playerUuid) {
        String sql = "DELETE FROM " + tablePrefix + "repositories WHERE player_uuid = ?;";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUuid.toString());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Повертає true, якщо запис було успішно видалено
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Повертає false у випадку помилки
        }
    }
}
