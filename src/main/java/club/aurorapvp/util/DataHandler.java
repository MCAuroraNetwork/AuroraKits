package club.aurorapvp.util;

import static club.aurorapvp.AuroraKits.plugin;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DataHandler {

  public static File file;
  public static FileConfiguration customFile;
  public static File dir;

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
  public static void reload() {
    customFile = YamlConfiguration.loadConfiguration(file);
  }
}
