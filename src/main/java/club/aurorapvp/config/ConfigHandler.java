package club.aurorapvp.config;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.config;
import static club.aurorapvp.AuroraKits.lang;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigHandler {
  private static final HashMap<String, String> Defaults = new HashMap<>();

  public static void generateConfigDefaults() throws IOException {
    Defaults.put("doFirstFallDamage", "false");
    Defaults.put("giveKitOnJoin.enabled", "false");
    Defaults.put("giveKitOnJoin.kit", "Default");

    for (String path : Defaults.keySet()) {
      if (!getConfigFile().contains(path) || config.getString(path) == null) {
        getConfigFile().set(path, Defaults.get(path));
        getConfigFile().save(new File(DataFolder, "config.yml"));
      }
    }
  }

  public static void saveConfigFile() throws IOException {
    lang.save(new File(DataFolder, "config.yml"));
  }
  public static YamlConfiguration getConfigFile() {
    return config;
  }
}