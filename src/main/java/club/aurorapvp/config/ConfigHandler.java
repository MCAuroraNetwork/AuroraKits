package club.aurorapvp.config;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigHandler {
  private static final HashMap<String, String> Defaults = new HashMap<>();
  private static final File file = new File(DataFolder, "config.yml");
  private static YamlConfiguration config;

  public static void generateConfigDefaults() throws IOException {
    Defaults.put("doFirstFallDamage", "false");
    Defaults.put("kits.lastUsedKit.enabled", "false");
    Defaults.put("kits.lastUsedKit.defaultKit", "Default");

    for (String path : Defaults.keySet()) {
      if (!getConfigFile().contains(path) || getConfigFile().getString(path) == null) {
        getConfigFile().set(path, Defaults.get(path));
        getConfigFile().save(file);
      }
    }
  }

  public static void saveConfigFile() throws IOException {
    getConfigFile().save(file);
  }

  public static YamlConfiguration getConfigFile() {
    return config;
  }

  public static void reloadConfig() throws IOException {
    if (!file.exists()) {
      file.getParentFile().mkdirs();

      file.createNewFile();
    }
    config = YamlConfiguration.loadConfiguration(file);
    plugin.getLogger().info("Config reloaded!");
  }
}