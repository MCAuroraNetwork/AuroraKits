package club.aurorapvp.config;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.plugin;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

// TODO make KitDataHandler, CustomConfigHandler, and ItemFrameDataHandler all use a shared util class
public class CustomConfigHandler {
  private static File file;
  private static FileConfiguration customFile;

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

    GenerateDefaults();
  }

  public static FileConfiguration get() {
    return customFile;
  }

  public static void save() {
    try {
      customFile.save(file);
    } catch (IOException e) {
      plugin.getLogger().warning("Couldn't save config");
    }
  }

  public static void reload() {
    customFile = YamlConfiguration.loadConfiguration(file);
    plugin.getLogger().info("Config reloaded");
  }

  public static void GenerateDefaults() {
    get().createSection("message");
    get().addDefault("message.joinMessage", "Welcome to Aurora PvP!");
    get().addDefault("message.firstJoinMessage", "You can use /kits to get a kit, or /createkit to make your own!");
  }
}