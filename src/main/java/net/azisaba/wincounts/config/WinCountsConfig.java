package net.azisaba.wincounts.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.azisaba.wincounts.WinCounts;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
@RequiredArgsConstructor
public class WinCountsConfig {

  private final WinCounts plugin;

  private String sqlHostname;
  private int sqlPort;
  private String sqlDatabase;
  private String sqlUsername;
  private String sqlPassword;

  public void load() {
    plugin.saveDefaultConfig();
    FileConfiguration conf = plugin.getConfig();

    sqlHostname = conf.getString("mysql.hostname");
    sqlPort = conf.getInt("mysql.port");
    sqlDatabase = conf.getString("mysql.database");
    sqlUsername = conf.getString("mysql.username");
    sqlPassword = conf.getString("mysql.password");
  }
}
