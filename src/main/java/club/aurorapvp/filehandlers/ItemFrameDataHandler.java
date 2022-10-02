package club.aurorapvp.filehandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.plugin;
import static club.aurorapvp.listeners.CommandListener.commandArg0;
import static club.aurorapvp.listeners.CommandListener.mainHandData;
import static club.aurorapvp.listeners.CommandListener.p;
import static club.aurorapvp.listeners.EventListener.clickLoc;

import java.io.File;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;

// TODO make KitDataHandler, CustomConfigHandler, and ItemFrameDataHandler all use a shared util class
public class ItemFrameDataHandler {

  private static File file;
  private static File dir;

  private static FileConfiguration customFile;

  public static void setup() {
    new File(DataFolder, "/frames/").mkdir();
    dir = new File(DataFolder, "/frames/");

    file = new File(dir,"data.yml");
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        plugin.getLogger().warning("Could not create data.yml");
      }
    }
    customFile = YamlConfiguration.loadConfiguration(file);
    get().createSection("frames");
    save();
  }

  public static void create() {
    Location loc = p.getTargetBlock(null, 4).getLocation().add(-1, 0, 0);
    ItemFrame frame = p.getWorld().spawn(loc, ItemFrame.class);
    frame.setItem(mainHandData);

    checkFile();
    get().set("frames." + commandArg0 + ".item", mainHandData);
    get().set("frames." + commandArg0 + ".location", frame.getLocation());
    save();
  }

  public static void delete() {
    checkFile();
    file.delete();
  }

  public static FileConfiguration get() {
    return customFile;
  }

  public static void checkFile() {
    // Defines the dir and file variables
    if (!dir.exists() || !file.exists()) {
      setup();
    }
  }
  public static void save() {
    try {
      customFile.save(file);
    } catch (IOException e) {
      plugin.getLogger().warning("Couldn't save frame");
    }
  }
  public static String checkLocation() {
    for (Object path : get().getConfigurationSection("frames").getKeys(false).toArray()) {
      if (get().getLocation("frames." + path + ".location").equals(clickLoc)) {
        return (String) path;
      }
    }
    return null;
  }
}