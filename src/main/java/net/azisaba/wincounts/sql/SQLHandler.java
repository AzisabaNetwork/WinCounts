package net.azisaba.wincounts.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SQLHandler {

  private final String hostname;
  private final int port;
  private final String database;
  private final String username;
  private final String password;

  private Connection connection;

  public boolean isConnected() {
    return (connection != null);
  }

  public void connect() throws SQLException {
    if (!isConnected())
      connection =
          DriverManager.getConnection(
              "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSLL=false",
              username,
              password);
  }

  public void close() {
    if (isConnected()) {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public Connection getConnection() {
    return connection;
  }
}
