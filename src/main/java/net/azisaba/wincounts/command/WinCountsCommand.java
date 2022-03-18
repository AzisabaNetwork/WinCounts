package net.azisaba.wincounts.command;

import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import net.azisaba.wincounts.WinCounts;
import net.azisaba.wincounts.util.PointData;
import net.azisaba.wincounts.utils.Args;
import net.azisaba.wincounts.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class WinCountsCommand implements CommandExecutor {

  private final WinCounts plugin;

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "このコマンドはゲーム内から実行してください。");
      return true;
    }
    Player p = (Player) sender;

    if (args.length <= 0) {
      sendUsage(sender);
      return true;
    }

    if (Args.check(args, 0, "list")) {
      String values =
          String.join(Chat.f("&7, &b"), plugin.getWinnerItemLoader().getAllItems().keySet());
      p.sendMessage(Chat.f("&a登録されているイベント名: &b{0}", values));
    } else if (Args.check(
        args, 0, "getitem", "seeitem", "showitem", "getitems", "seeitems", "showitems")) {
      if (args.length <= 1) {
        p.sendMessage(Chat.f("&cイベント名を指定してください。"));
        return true;
      }

      Inventory inv = createInventoryFor(args[1].toLowerCase(Locale.ROOT));

      if (inv == null) {
        p.sendMessage(Chat.f("&cイベント名 &e{0} &cは存在しません。", args[1].toLowerCase(Locale.ROOT)));
        return true;
      }

      p.openInventory(inv);
    } else if (Args.check(args, 0, "setitem")) {
      ItemStack item = p.getInventory().getItemInMainHand();
      if (item == null || item.getType() == Material.AIR) {
        p.sendMessage(Chat.f("&c手にアイテムを持った状態で実行してください！"));
        return true;
      }

      if (args.length <= 1) {
        p.sendMessage(Chat.f("&cイベント名を指定してください。"));
        return true;
      }
      String eventName = args[1].toLowerCase(Locale.ROOT);

      List<ItemStack> items = plugin.getWinnerItemLoader().getAllItems().get(eventName);
      if (items != null) {
        for (ItemStack i : items) {
          if (i.isSimilar(item)) {
            p.sendMessage(Chat.f("&cそのアイテムは既に登録されています。"));
            return true;
          }
        }
      }

      ItemStack amount1 = item.clone();
      amount1.setAmount(1);

      plugin.getWinnerItemLoader().addItem(eventName, amount1);
      p.sendMessage(Chat.f("&a正常にアイテムを登録しました！"));
    } else if (Args.check(args, 0, "clearitems")) {
      if (args.length <= 1) {
        p.sendMessage(Chat.f("&cイベント名を指定してください。"));
        return true;
      }
      String eventName = args[1].toLowerCase(Locale.ROOT);
      plugin.getWinnerItemLoader().clearItems(eventName);
      p.sendMessage(Chat.f("&aイベント名 &e{0} &aのアイテムを削除しました。", eventName));
    } else if (Args.check(
        args, 0, "result", "showresult", "results", "showresults", "check", "checkresults")) {
      if (args.length <= 1) {
        p.sendMessage(Chat.f("&cイベント名を指定してください。"));
        return true;
      }
      String eventName = args[1].toLowerCase(Locale.ROOT);

      Bukkit.getScheduler()
          .runTaskAsynchronously(
              plugin,
              () -> {
                List<PointData> pointDataList =
                    plugin.getSqlTransactionExecutor().getTopFor(eventName);

                if (pointDataList == null) {
                  p.sendMessage(Chat.f("&c取得に失敗しました。"));
                  return;
                }

                for (PointData pointData : pointDataList) {
                  if (pointData.getMcid() != null) {
                    p.sendMessage(
                        Chat.f(" &a- &e{0} &7- &e{1}", pointData.getMcid(), pointData.getPoint()));
                  } else {
                    p.sendMessage(
                        Chat.f(" &a- &e{0} &7- &e{1}", pointData.getUuid(), pointData.getPoint()));
                  }
                }
              });
    } else {
      sendUsage(sender);
    }

    return true;
  }

  private void sendUsage(CommandSender sender) {
    sender.sendMessage(Chat.f("&c使い方: &b/wincounts <list|getitem|setitem|clearitems|result>"));
  }

  private Inventory createInventoryFor(String eventName) {
    List<ItemStack> items = plugin.getWinnerItemLoader().getAllItems().get(eventName);
    if (items == null || items.isEmpty()) {
      return null;
    }

    int size = (int) (Math.ceil((double) items.size() / 9d) * 9);
    Inventory inv = Bukkit.createInventory(null, size, Chat.f("&c{0} のアイテム", eventName));
    inv.addItem(items.toArray(new ItemStack[0]));
    return inv;
  }
}
