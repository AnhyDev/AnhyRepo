package ink.anh.repo.db;

import ink.anh.repo.storage.Repository;
import ink.anh.repo.storage.Slots;
import ink.anh.repo.utils.OtherUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLiteRepositoryTable extends AbstractRepositoryTable {

    public SQLiteRepositoryTable(SQLiteDatabaseManager dbManager) {
    	super(dbManager);
        initialize();
    }

    @Override
    public void initialize() {
        try (Connection conn = dbManager.getConnection()) {
            // Створення таблиці
            try (PreparedStatement ps = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS repositories (" +
                "player_uuid TEXT PRIMARY KEY, " +
                "loverName TEXT UNIQUE, " +
                "slot0 TEXT, slot1 TEXT, slot2 TEXT, " +
                "slot3 TEXT, slot4 TEXT, slot5 TEXT, " +
                "slot6 TEXT, slot7 TEXT, slot8 TEXT);")) {
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                "CREATE INDEX IF NOT EXISTS idx_loverName ON repositories (loverName);")) {
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
        String sql = "INSERT OR REPLACE INTO repositories (player_uuid, loverName, " + fieldName + ") VALUES (?, ?, ?);";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUuid.toString());
            ps.setString(2, loverName);
            ps.setString(3, OtherUtils.encryptAndEncodeBase64(repository.toString(), playerUuid.toString()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertOrUpdateRepository(UUID playerUuid, String loverName, Repository[] repositories) {
    	loverName = loverName.toLowerCase();
        String sql = "INSERT OR REPLACE INTO repositories (player_uuid, loverName, " +
                     "slot0, slot1, slot2, slot3, slot4, slot5, slot6, slot7, slot8) VALUES " +
                     "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

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
        String sql =  "SELECT * FROM repositories WHERE loverName = ?;";
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
        String sql = "SELECT * FROM repositories WHERE player_uuid = ?;";
        Repository[] repositories = new Repository[Slots.values().length];

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, playerUuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                for (Slots slot : Slots.values()) {
                    String slotData = rs.getString(slot.name().toLowerCase());
                    if (slotData != null && !slotData.isEmpty()) {
                        repositories[slot.ordinal()] = Repository.fromString(OtherUtils.decodeAndDecryptBase64(slotData, rs.getString("player_uuid")));
                    } else {
                        repositories[slot.ordinal()] = null;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return repositories;
    }

    @Override
    public boolean clearRepository(UUID playerUuid) {
        String sql = "UPDATE repositories SET " +
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
        String sql = "DELETE FROM repositories WHERE player_uuid = ?;";

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
}
