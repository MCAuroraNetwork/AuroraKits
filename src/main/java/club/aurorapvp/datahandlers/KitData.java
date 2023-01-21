package club.aurorapvp.datahandlers;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.modules.Kit;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitData {
  private final YamlConfiguration DATA_FILE;
  private final File FILE;
  private final Kit kit;

  public KitData(Kit kit) {
    this.kit = kit;
    this.FILE =
        new File(AuroraKits.DATA_FOLDER, "/kits/" + kit.getPath() + "/" + kit.getName() + ".yml");
    DATA_FILE = YamlConfiguration.loadConfiguration(FILE);
  }

  public Player getOwner() {
    return Bukkit.getPlayer(UUID.fromString(DATA_FILE.getString("owner")));
  }

  public ItemStack getDisplayItem() {
    return DATA_FILE.getItemStack("displayItem");
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
    DATA_FILE.set("type", kit.getType());
    DATA_FILE.set("owner", String.valueOf(kit.getOwner().getUniqueId()));
    DATA_FILE.set("displayItem", kit.getDisplayItem());

    for (int i = 0; i < kit.getInventory().getContents().length; i++) {
      DATA_FILE.set("items." + i, kit.getInventory().getContents()[i]);
    }

    save();
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