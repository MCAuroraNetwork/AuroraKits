package club.aurorapvp.aurorakits.datahandlers;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.modules.Kit;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class KitData {
  private final YamlConfiguration DATA_FILE;
  private final File FILE;
  private final Kit kit;

  public KitData(Kit kit) {
    this.kit = kit;
    this.FILE =
        new File(AuroraKits.INSTANCE.getDataFolder(),
            "/kits/" + kit.getPath() + "/" + kit.getName() + ".yml");
    DATA_FILE = YamlConfiguration.loadConfiguration(FILE);
  }

  public OfflinePlayer getOwner() {
    return Bukkit.getOfflinePlayer(UUID.fromString(
        Objects.requireNonNull(DATA_FILE.getString("owner"))));
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

  public boolean isPublic() {
    return DATA_FILE.getBoolean("isPublic");
  }

  public ItemStack[] getItemStacks() {
    ConfigurationSection section = this.getYaml().getConfigurationSection("items");
    assert section != null;
    ItemStack[] itemStacks = new ItemStack[section.getKeys(false).size()];
    int i = 0;

    for (String path : section.getKeys(false)) {
      itemStacks[i] = this.getYaml().getItemStack("items." + path);
      i++;
    }
    return itemStacks;
  }

  public void create() {
    DATA_FILE.set("isPublic", kit.isPublic());
    DATA_FILE.set("owner", String.valueOf(kit.getOwner().getUniqueId()));
    DATA_FILE.set("displayItem", kit.getDisplayItem());

    for (int i = 0; i < kit.getItemStacks().length; i++) {
      DATA_FILE.set("items." + i, kit.getItemStacks()[i]);
    }

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
      AuroraKits.INSTANCE.getLogger().severe("Failed to save kit data");
    }
  }
}