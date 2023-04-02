package club.aurorapvp.configs;

import club.aurorapvp.AuroraKits;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
  private static final HashMap<String, Object> DEFAULTS = new HashMap<>();
  private static final File FILE = new File(AuroraKits.DATA_FOLDER, "config.yml");
  private static YamlConfiguration config;

  public static void init() {
    reload();
    generateDefaults();
  }

  public static void generateDefaults() {
    DEFAULTS.put("kits.give-last-kit.enabled", false);
    DEFAULTS.put("kits.give-last-kit.defaultKit", "Default");

    for (String path : DEFAULTS.keySet()) {
      if (!get().contains(path) || get().getString(path) == null) {
        get().set(path, DEFAULTS.get(path));

        try {
          get().save(FILE);
        } catch (IOException e) {
          AuroraKits.LOGGER.severe("Unable to save config file");
        }
      }
    }
  }

  public static YamlConfiguration get() {
    return config;
  }

  public static void reload() {
    if (!FILE.exists()) {
      try {
        FILE.getParentFile().mkdirs();
        FILE.createNewFile();
      } catch (IOException e) {
        AuroraKits.LOGGER.severe("Failed to generate config files");
      }
    }
    config = YamlConfiguration.loadConfiguration(FILE);
    AuroraKits.LOGGER.info("Config reloaded!");
  }
}