package club.aurorapvp.datahandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.plugin;
import static club.aurorapvp.AuroraKits.serializeComponent;
import static club.aurorapvp.listeners.CommandListener.commandArg0;
import static club.aurorapvp.listeners.CommandListener.p;

import java.io.File;
import java.io.IOException;
import java.util.List;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
    inv = Bukkit.createInventory(null, 54, MiniMessage.miniMessage()
        .deserialize("<gradient:#FFAA00:#FF55FF>KitGUI"));

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
        plugin.getLogger().warning("Couldn't save GUI File");
      }
    }
    customFile = YamlConfiguration.loadConfiguration(file);

    if (get().getConfigurationSection("kits") != null) {
      for (Object path : get().getConfigurationSection("kits").getKeys(false).toArray()) {
        inv.addItem(createGuiItem(get().getItemStack("kits." + path + ".displayItem").getType(),
            (String) path, get().getString("kits." + path + ".creator"), 0));
      }
    }

    file = new File(dir, p.getUniqueId() + ".yml");
    customFile = YamlConfiguration.loadConfiguration(file);

    if (get().getConfigurationSection("kits") != null) {
      for (Object path : get().getConfigurationSection("kits").getKeys(false).toArray()) {
        inv.addItem(createGuiItem(get().getItemStack("kits." + path + ".displayItem").getType(),
            (String) path, get().getString("kits." + path + ".creator"), 1));
      }
    }
  }


  protected static ItemStack createGuiItem(final Material material, final String name,
                                           String creator, int type) {
    final ItemStack item = new ItemStack(material, 1);
    final ItemMeta meta = item.getItemMeta();

    meta.displayName(
        MiniMessage.miniMessage().deserialize("<gradient:#FFAA00:#FF55FF>" + name)
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.BOLD, true));

    switch (type) {
      case 0 -> meta.lore(List.of(MiniMessage.miniMessage()
          .deserialize("<gradient:#FFAA00:#FF55FF>Created by " + creator), MiniMessage.miniMessage()
          .deserialize("This is a Public Kit")));
      case 1 -> meta.lore(List.of(MiniMessage.miniMessage()
          .deserialize("<gradient:#FFAA00:#FF55FF>Created by " + creator), MiniMessage.miniMessage()
          .deserialize("This is a Private Kit")));
      default -> meta.lore();
    }

    item.setItemMeta(meta);

    return item;
  }

  public static void openGUI(final HumanEntity ent) {
    GUIHandler();
    ent.openInventory(inv);
  }

  public static void createGUIEntry() throws IOException {
    dir = new File(DataFolder, "/GUIs/");
    if (!dir.exists()) {
      new File(DataFolder, "/GUIs/").mkdir();
    }
    file = new File(dir, p.getUniqueId() + ".yml");
    if (!file.exists()) {
      file.createNewFile();
    }
    customFile = YamlConfiguration.loadConfiguration(file);

    get().set("kits." + commandArg0 + ".displayItem", p.getInventory().getItemInMainHand());
    get().set("kits." + commandArg0 + ".creator", serializeComponent.serialize(p.displayName()));
    save();
  }

  public static void deleteGUIEntry() {
    dir = new File(DataFolder, "/GUIs/");
    if (!dir.exists()) {
      new File(DataFolder, "/GUIs/").mkdir();
    }
    file = new File(dir, p.getUniqueId() + ".yml");
    if (file.exists()) {
      customFile = YamlConfiguration.loadConfiguration(file);

      get().createSection("kits");
      get().set("kits." + commandArg0, null);
      save();
    }
  }

  public static void createPublicGUIEntry() throws IOException {
    dir = new File(DataFolder, "/GUIs/");
    if (!dir.exists()) {
      new File(DataFolder, "/GUIs/").mkdir();
    }
    file = new File(dir, "public.yml");
    if (!file.exists()) {
      file.createNewFile();
    }
    customFile = YamlConfiguration.loadConfiguration(file);

    get().createSection("kits");
    get().set("kits." + commandArg0 + ".displayItem", p.getInventory().getItemInMainHand());
    get().set("kits." + commandArg0 + ".creator", serializeComponent.serialize(p.displayName()));
    save();
  }

  public static void deletePublicGUIEntry() {
    dir = new File(DataFolder, "/GUIs/");
    if (!dir.exists()) {
      new File(DataFolder, "/GUIs/").mkdir();
    }
    file = new File(dir, "public.yml");
    if (file.exists()) {
      customFile = YamlConfiguration.loadConfiguration(file);

      get().createSection("kits");
      get().set("kits." + commandArg0, "");
      save();
    }
  }

  public static void save() {
    try {
      customFile.save(file);
    } catch (IOException e) {
      plugin.getLogger().warning("Couldn't save");
    }
  }

  public static FileConfiguration get() {
    return customFile;
  }
}