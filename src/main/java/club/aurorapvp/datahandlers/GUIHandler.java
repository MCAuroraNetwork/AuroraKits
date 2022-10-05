package club.aurorapvp.datahandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.plugin;
import static club.aurorapvp.listeners.CommandListener.p;

import java.io.File;
import java.io.IOException;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIHandler implements Listener {
  private static FileConfiguration customFile;
  private static File dir;
  public static Inventory inv;
  private static File file;

  public static void GUIHandler() {
    inv = Bukkit.createInventory(null, 54, Component.text("KitGUI"));

    initializeItems();
  }

  public static void initializeItems() {
    dir = new File(DataFolder + "/GUIS/");
    dir = new File(DataFolder, "/GUIs/");
    if (!dir.exists()) {
      new File(DataFolder, "/GUIs/").mkdir();
    }
    file = new File(dir, "public.yml");
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        plugin.getLogger().warning("Couldn't save GUI Data");
      }
    }
    customFile = YamlConfiguration.loadConfiguration(file);

    for (int i = 0; i < 2; i++) {
      if (get().getConfigurationSection("kits") != null) {
        for (Object path : get().getConfigurationSection("kits").getKeys(false).toArray()) {
          inv.addItem(createGuiItem(get().getItemStack("kits." + path + ".displayItem").getType(),
              (String) path));
        }
      }
      file = new File(dir, p.getUniqueId() + ".yml");
      customFile = YamlConfiguration.loadConfiguration(file);
    }
  }

  protected static ItemStack createGuiItem(final Material material, final String name,
                                           final Component... lore) {
    final ItemStack item = new ItemStack(material, 1);
    final ItemMeta meta = item.getItemMeta();

    meta.displayName(Component.text(name));

    item.setItemMeta(meta);

    meta.lore(List.of(lore));

    return item;
  }

  public static void openGUI(final HumanEntity ent) {
    GUIHandler();
    ent.openInventory(inv);
  }

  public static FileConfiguration get() {
    return customFile;
  }
}