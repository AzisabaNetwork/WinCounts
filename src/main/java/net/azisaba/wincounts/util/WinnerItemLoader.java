package net.azisaba.wincounts.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.azisaba.wincounts.WinCounts;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class WinnerItemLoader {

  private static final String YML_FILE_NAME = "winner-items.yml";

  private final WinCounts plugin;

  private final HashMap<String, List<ItemStack>> items = new HashMap<>();

  public void load() {
    YamlConfiguration conf =
        YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), YML_FILE_NAME));

    ConfigurationSection sec = conf.getConfigurationSection("");

    if (sec == null) {
      return;
    }

    for (String key : sec.getKeys(false)) {
      List<ItemStack> loadedItems = new ArrayList<>();
      for (int i = 0; i < 54; i++) {
        String path = key + "." + i;
        if (!conf.isSet(path)) {
          break;
        }
        loadedItems.add(conf.getItemStack(path));
      }

      items.put(key, loadedItems);
    }
  }

  public void save() {
    YamlConfiguration conf = new YamlConfiguration();

    for (String key : items.keySet()) {
      List<ItemStack> items = this.items.get(key);
      for (int i = 0; i < items.size(); i++) {
        conf.set(key + "." + i, items.get(i));
      }
    }

    try {
      conf.save(new File(plugin.getDataFolder(), YML_FILE_NAME));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public HashMap<String, List<ItemStack>> getAllItems() {
    return items;
  }

  public void addItem(String eventName, ItemStack amount1) {
    List<ItemStack> list = items.computeIfAbsent(eventName, k -> new ArrayList<>());
    list.add(amount1);
    items.put(eventName, list);
  }

  public void clearItems(String eventName) {
    items.remove(eventName);
  }
}
