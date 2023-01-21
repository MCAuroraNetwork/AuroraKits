package club.aurorapvp.datahandlers;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.config.Lang;
import club.aurorapvp.modules.Kit;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitData {
  private final YamlConfiguration DATA_FILE;
  private final File FILE;
  private final Inventory inv = Bukkit.createInventory(null, 54, Lang.getComponent("GUIName"));;
  private final Kit kit;

  public KitData(Kit kit) {
    this.kit = kit;
    this.FILE =
        new File(AuroraKits.DATA_FOLDER, "/kits/" + kit.getPath() + "/" + kit.getName() + ".yml");
    DATA_FILE = YamlConfiguration.loadConfiguration(FILE);

    if (!kit.exists()) {
      this.create();
    }
  }

  public Player getOwner() {
    return Bukkit.getPlayer(UUID.fromString(DATA_FILE.getString("owner")));
  }

  public ItemStack getDisplayItem() {
    return DATA_FILE.getItemStack("displayIte");
  }

  public File getFile() {
    return FILE;
  }

  public YamlConfiguration getYaml() {
    return DATA_FILE;
  }

  public Integer getType() {
    return DATA_FILE.getInt("type");
  }

  public ItemStack[] getInventoryData() {
    ConfigurationSection section = this.getYaml().getConfigurationSection("items");
    ItemStack[] itemStacks = new ItemStack[section.getKeys(false).size()];
    int i = 0;

    for (String path : section.getKeys(false)) {
      itemStacks[i] = this.getYaml().getItemStack("items." + path);
      i++;
    }
    return itemStacks;
  }

  public void create() {
    if (!kit.exists()) {
      DATA_FILE.set("type", kit.getType());
      DATA_FILE.set("owner.", kit.getOwner().getUniqueId());
      DATA_FILE.set("items.", kit.getInventoryData());
      DATA_FILE.set("displayItem", kit.getDisplayItem());
      save();
    }
  }

  // TODO this sucks ass
  public static int getAmount(Player p) {
    File file = new File(AuroraKits.DATA_FOLDER, "/kits/" + p.getUniqueId() + "/");

    if (file.listFiles() == null) {
      return new File(AuroraKits.DATA_FOLDER, "/kits/public/").listFiles().length;
    } else {
      return file.listFiles().length +
          new File(AuroraKits.DATA_FOLDER, "/kits/public/").listFiles().length;
    }
  }

  protected static ItemStack createItem(final Material material, final String name,
                                        String creator, int type) {
    final ItemStack item = new ItemStack(material, 1);
    final ItemMeta meta = item.getItemMeta();

    AuroraKits.DESERIALIZE_COMPONENT.deserialize("<gradient:#FFAA00:#FF55FF>" + name)
        .decoration(TextDecoration.ITALIC, false)
        .decoration(TextDecoration.BOLD, true);

    switch (type) {
      case 0 -> meta.lore(List.of(MiniMessage.miniMessage()
          .deserialize("<gradient:#FFAA00:#FF55FF>Created by " + creator), MiniMessage.miniMessage()
          .deserialize("This is a Private Kit")));
      case 1 -> meta.lore(List.of(MiniMessage.miniMessage()
          .deserialize("<gradient:#FFAA00:#FF55FF>Created by " + creator), MiniMessage.miniMessage()
          .deserialize("This is a Public Kit")));
      default -> meta.lore();
    }

    item.setItemMeta(meta);

    return item;
  }

  public void save() {
    try {
      if (!FILE.exists()) {
        FILE.getParentFile().mkdirs();
        FILE.createNewFile();
      }
      DATA_FILE.save(FILE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}