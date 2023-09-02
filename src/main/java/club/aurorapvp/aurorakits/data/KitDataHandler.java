package club.aurorapvp.aurorakits.data;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.modules.kits.Kit;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class KitDataHandler {

  private final YamlConfiguration DATA_FILE;
  private final File FILE;
  private final Kit kit;

  public KitDataHandler(Kit kit) {
    this.kit = kit;
    this.FILE =
        new File(
            AuroraKits.INSTANCE.getDataFolder(),
            "/kits/" + kit.getPath() + "/" + kit.getName() + ".yml");
    DATA_FILE = YamlConfiguration.loadConfiguration(FILE);
  }

  public ItemStack getDisplayItem() {
    return DATA_FILE.getItemStack("displayItem");
  }

  public File getFile() {
    return FILE;
  }

  public YamlConfiguration get() {
    return DATA_FILE;
  }

  public boolean isPublic() {
    return DATA_FILE.getBoolean("isPublic");
  }

  public ItemStack[] getContents() {
    ConfigurationSection section = this.get().getConfigurationSection("items.contents");
    assert section != null;
    ItemStack[] itemStacks = new ItemStack[section.getKeys(false).size()];
    int i = 0;

    for (String path : section.getKeys(false)) {
      itemStacks[i] = this.get().getItemStack("items.contents" + path);
      i++;
    }
    return itemStacks;
  }

  public ItemStack[] getArmor() {
    ConfigurationSection section = this.get().getConfigurationSection("items.armor");
    assert section != null;
    ItemStack[] itemStacks = new ItemStack[section.getKeys(false).size()];
    int i = 0;

    for (String path : section.getKeys(false)) {
      itemStacks[i] = this.get().getItemStack("items.armor" + path);
      i++;
    }
    return itemStacks;
  }

  public ItemStack getOffhand() {
    return this.get().getItemStack("items.offhand");
  }

  public void create() {
    DATA_FILE.set("isPublic", kit.isPublic());
    DATA_FILE.set("owner", String.valueOf(kit.getOwner().getUniqueId()));
    DATA_FILE.set("displayItem", kit.getDisplayItem());

    for (int i = 0; i < kit.getContents().length; i++) {
      DATA_FILE.set("items.contents" + i, kit.getContents()[i]);
    }

    for (int i = 0; i < kit.getArmor().length; i++) {
      DATA_FILE.set("items.contents" + i, kit.getContents()[i]);
    }

    DATA_FILE.set("items.offhand", kit.getOffhand());

    save();
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void save() {
    try {
      if (!FILE.exists()) {
        FILE.getParentFile().mkdirs();
        FILE.createNewFile();
      }
      DATA_FILE.save(FILE);
    } catch (IOException e) {
      AuroraKits.INSTANCE.getLogger().log(Level.SEVERE, "Failed to save kit data", e);
    }
  }
}
