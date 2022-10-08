package club.aurorapvp.config;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.plugin;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomConfigHandler {
  private static FileConfiguration customFile;
  private static File file;

  // Finds or generates the custom config file
  public static void setup() {
    file = new File(DataFolder, "config.yml");

    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        plugin.getLogger().warning("Couldn't create config");
      }
    }
    customFile = YamlConfiguration.loadConfiguration(file);
    save();
  }

  public static FileConfiguration get() {
    return customFile;
  }

  public static void save() {
    try {
      customFile.save(file);
    } catch (IOException e) {
      plugin.getLogger().warning("Couldn't save");
    }
  }

  public static void generateDefaults() {
    get().addDefault("doFirstFallDamage", "false");
  }

  public static void reload() {
    customFile = YamlConfiguration.loadConfiguration(file);
  }
}