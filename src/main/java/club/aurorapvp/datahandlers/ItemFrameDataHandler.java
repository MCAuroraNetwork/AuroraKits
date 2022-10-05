package club.aurorapvp.datahandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.plugin;
import static club.aurorapvp.listeners.CommandListener.commandArg0;
import static club.aurorapvp.listeners.CommandListener.mainHandData;
import static club.aurorapvp.listeners.CommandListener.p;
import static club.aurorapvp.listeners.EventListener.clickLoc;
import static club.aurorapvp.util.DataHandler.customFile;
import static club.aurorapvp.util.DataHandler.dir;
import static club.aurorapvp.util.DataHandler.file;
import static club.aurorapvp.util.DataHandler.get;
import static club.aurorapvp.util.DataHandler.save;

import java.io.File;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;

public class ItemFrameDataHandler {
  public static void setup() {
    new File(DataFolder, "/frames/").mkdir();
    dir = new File(DataFolder, "/frames/");

    file = new File(dir, "data.yml");
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        plugin.getLogger().warning("Could not create data.yml");
      }
    }
    customFile = YamlConfiguration.loadConfiguration(file);
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

  public static void checkFile() {
    // Defines the dir and file variables
    if (!dir.exists() || !file.exists()) {
      setup();
    }
  }

  public static String checkLocation() {
    setup();
    for (Object path : get().getConfigurationSection("frames").getKeys(false).toArray()) {
      if (get().getLocation("frames." + path + ".location").equals(clickLoc)) {
        return (String) path;
      }
    }
    return null;
  }
}