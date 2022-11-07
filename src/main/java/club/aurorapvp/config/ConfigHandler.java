package club.aurorapvp.config;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigHandler {
  private static final File file = new File(DataFolder, "config.yml");
  private static final HashMap<String, String> Defaults = new HashMap<>();
  private static YamlConfiguration config;

  public static void setupConfigFile() throws IOException {
    if (!new File(DataFolder, "config.yml").exists()) {
      new File(DataFolder, "config.yml").createNewFile();
    }
    reloadConfig();
  }

  public static void generateConfigDefaults() throws IOException {
    Defaults.put("doFirstFallDamage", "false");
    Defaults.put("kits.lastUsedKit.enabled", "false");
    Defaults.put("kits.lastUsedKit.defaultKit", "Default");

    for (String path : Defaults.keySet()) {
      if (!getConfigFile().contains(path) || getConfigFile().getString(path) == null) {
        getConfigFile().set(path, Defaults.get(path));
        getConfigFile().save(new File(DataFolder, "config.yml"));
      }
    }
  }

  public static void saveConfigFile() throws IOException {
    getConfigFile().save(new File(DataFolder, "config.yml"));
  }

  public static YamlConfiguration getConfigFile() {
    return config;
  }

  public static void reloadConfig() {
    config = YamlConfiguration.loadConfiguration(new File(DataFolder, "config.yml"));
    plugin.getLogger().info("Config reloaded!");
  }
}