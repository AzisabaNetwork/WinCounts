package net.azisaba.wincounts;

import lombok.Getter;
import net.azisaba.wincounts.command.WinCountsCommand;
import net.azisaba.wincounts.config.WinCountsConfig;
import net.azisaba.wincounts.listener.WorldChangeListener;
import net.azisaba.wincounts.sql.SQLTransactionExecutor;
import net.azisaba.wincounts.util.WinnerItemLoader;
import net.azisaba.wincounts.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class WinCounts extends JavaPlugin {

  private WinnerItemLoader winnerItemLoader;

  private WinCountsConfig winCountsConfig;
  private SQLTransactionExecutor sqlTransactionExecutor;

  @Override
  public void onEnable() {
    winCountsConfig = new WinCountsConfig(this);
    winCountsConfig.load();

    winnerItemLoader = new WinnerItemLoader(this);
    winnerItemLoader.load();

    sqlTransactionExecutor =
        new SQLTransactionExecutor(
            winCountsConfig.getSqlHostname(),
            winCountsConfig.getSqlPort(),
            winCountsConfig.getSqlDatabase(),
            winCountsConfig.getSqlUsername(),
            winCountsConfig.getSqlPassword());

    if (!sqlTransactionExecutor.init()) {
      getLogger().severe("Failed to establish connection to SQL database.");
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }

    Bukkit.getPluginManager().registerEvents(new WorldChangeListener(this), this);

    Bukkit.getPluginCommand("wincounts").setExecutor(new WinCountsCommand(this));
    Bukkit.getPluginCommand("wincounts").setPermissionMessage(Chat.f("&cこのコマンドを実行する権限がありません。"));

    Bukkit.getLogger().info(getName() + " enabled.");
  }

  @Override
  public void onDisable() {
    if (sqlTransactionExecutor != null) {
      sqlTransactionExecutor.close();
    }
    if (winnerItemLoader != null) {
      winnerItemLoader.save();
    }
    Bukkit.getLogger().info(getName() + " disabled.");
  }
}
