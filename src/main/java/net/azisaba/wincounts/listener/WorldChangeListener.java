package net.azisaba.wincounts.listener;

import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.azisaba.wincounts.WinCounts;
import net.azisaba.wincounts.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class WorldChangeListener implements Listener {

  private final WinCounts plugin;

  @EventHandler
  public void onMoveWorld(PlayerChangedWorldEvent e) {
    Player p = e.getPlayer();
    HashMap<String, List<ItemStack>> allItemMap = plugin.getWinnerItemLoader().getAllItems();
    for (String key : allItemMap.keySet()) {
      List<ItemStack> items = allItemMap.get(key);
      int count = 0;
      for (int i = 0, size = p.getInventory().getSize(); i < size; i++) {
        ItemStack item = p.getInventory().getItem(i);
        if (item == null) {
          continue;
        }
        for (ItemStack compareItem : items) {
          if (item.isSimilar(compareItem)) {
            p.getInventory().setItem(i, null);
            count += item.getAmount();
          }
        }
      }

      if (count <= 0) {
        return;
      }

      final int sum = count;
      Bukkit.getScheduler()
          .runTaskAsynchronously(
              plugin,
              () -> {
                plugin.getSqlTransactionExecutor().incrementFor(key, p.getUniqueId(), sum);
                plugin.getLogger().info(p.getName() + "に" + sum + "ポイントを付与しました (" + key + ")");
                p.sendMessage(Chat.f("&a勝利アイテム{0}個を勝利ポイント{0}に変換しました！", sum));
              });
    }
  }
}
