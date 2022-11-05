package club.aurorapvp.config;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.config;
import static club.aurorapvp.AuroraKits.lang;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

// Finds or generates the custom config file
public class ConfigHandler {
  private static final HashMap<String, String> Defaults = new HashMap<>();

  public static void generateConfigDefaults() throws IOException {
    Defaults.put("doFirstFallDamage", "false");
    Defaults.put("giveKitOnJoin.enabled", "false");
    Defaults.put("giveKitOnJoin.kit", "Default");

    for (String path : Defaults.keySet()) {
      if (!config.contains(path) || config.getString(path) == null) {
        config.set(path, Defaults.get(path));
        config.save(new File(DataFolder, "config.yml"));
      }
    }
  }

  public static void saveConfigFile() throws IOException {
    lang.save(new File(DataFolder, "config.yml"));
  }
}