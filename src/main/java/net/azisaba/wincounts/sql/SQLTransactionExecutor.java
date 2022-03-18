package net.azisaba.wincounts.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.azisaba.wincounts.util.PointData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

@RequiredArgsConstructor
public class SQLTransactionExecutor {

  private final String hostname;
  private final int port;
  private final String database;
  private final String username;
  private final String password;

  private SQLHandler sqlHandler;

  public boolean init() {
    sqlHandler = new SQLHandler(hostname, port, database, username, password);
    try {
      sqlHandler.connect();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean incrementFor(String id, UUID playerUUID, int count) {
    createTableIfNotExists(id);
    try {
      // if id is consisted of only alphabet and underscore, it is valid.
      if (!id.matches("^[a-zA-Z_]+$")) {
        // invalid id
        return false;
      }
      PreparedStatement statement =
          sqlHandler
              .getConnection()
              .prepareStatement(
                  "INSERT INTO `points_"
                      + id
                      + "` (`uuid`, `point`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `point` = `point` + ?");

      statement.setString(1, playerUUID.toString());
      statement.setInt(2, count);
      statement.setInt(3, count);

      return statement.executeUpdate() >= 1;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean decrementFor(String id, UUID playerUUID, int count) {
    createTableIfNotExists(id);
    try {
      // if id is consisted of only alphabet and underscore, it is valid.
      if (!id.matches("^[a-zA-Z_]+$")) {
        // invalid id
        return false;
      }
      PreparedStatement statement =
          sqlHandler
              .getConnection()
              .prepareStatement(
                  // This is safe because id is consisted of only alphabet and underscore.
                  "INSERT INTO `points_"
                      + id
                      + "` (`uuid`, `point`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `point` = `point` - ?");

      statement.setString(1, playerUUID.toString());
      statement.setInt(2, count);
      statement.setInt(3, count);

      return statement.executeUpdate() >= 1;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public List<PointData> getTopFor(String id) {
    try {
      // if id is consisted of only alphabet and underscore, it is valid.
      if (!id.matches("^[a-zA-Z_]+$")) {
        // invalid id
        return null;
      }

      PreparedStatement statement =
          sqlHandler
              .getConnection()
              .prepareStatement(
                  // This is safe because id is consisted of only alphabet and underscore.
                  "SELECT `point_rank`, `uuid`, `point` FROM (SELECT RANK() OVER(ORDER BY `point` DESC) AS `point_rank`, `uuid`, `point` FROM `points_"
                      + id
                      + "`) AS `points_"
                      + id
                      + "` WHERE `point_rank` = 1 ORDER BY `point_rank`");

      ResultSet resultSet = statement.executeQuery();

      List<PointData> dataList = new ArrayList<>();
      while (resultSet.next()) {
        UUID uuid = UUID.fromString(resultSet.getString("uuid"));
        int point = resultSet.getInt("point");

        PointData data = new PointData(uuid, null, point);
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (player != null) {
          data.setMcid(player.getName());
        }
        dataList.add(data);
      }

      return dataList;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void close() {
    if (sqlHandler != null) {
      sqlHandler.close();
    }
  }

  private void createTableIfNotExists(String id) {
    try {
      // if id is consisted of only alphabet and underscore, it is valid.
      if (!id.matches("^[a-zA-Z_]+$")) {
        // invalid id
        return;
      }

      PreparedStatement statement =
          sqlHandler
              .getConnection()
              .prepareStatement(
                  "CREATE TABLE IF NOT EXISTS `points_"
                      + id // This is safe because id is consisted of only alphabet and underscore.
                      + "` (`uuid` VARCHAR(36) NOT NULL, `point` INT NULL, PRIMARY KEY (`uuid`))");
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
