package club.aurorapvp.datahandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.plugin;
import static club.aurorapvp.listeners.CommandListener.commandArg0;
import static club.aurorapvp.listeners.CommandListener.mainHandData;
import static club.aurorapvp.listeners.CommandListener.p;
import static club.aurorapvp.listeners.EventListener.clickLoc;

import java.io.File;
import java.io.IOException;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;

public class ItemFrameDataHandler {
  private static File dir;
  private static File file;
  private static FileConfiguration customFile;

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
    Block b = p.getTargetBlock(4);
    ItemFrame frame = (ItemFrame) p.getWorld().spawn(b.getLocation(), ItemFrame.class);
    if (getFacingDirection() != BlockFace.EAST) {
      frame.teleport(b.getRelative(getFacingDirection()).getLocation());
      frame.setFacingDirection(getFacingDirection(), true);
    }

    frame.setItem(mainHandData);

    checkFile();
    get().set("frames." + commandArg0 + ".item", mainHandData);
    get().set("frames." + commandArg0 + ".location", frame.getLocation());
    save();
  }

  public static BlockFace getFacingDirection() {
    double rotation = (p.getLocation().getYaw()) % 360;
    if (rotation < 0) {
      rotation += 360.0;
    }
    if (0 <= rotation && rotation < 56.25) {
      return BlockFace.NORTH;
    } else if (56.25 <= rotation && rotation < 135) {
      return BlockFace.EAST;
    } else if (135 <= rotation && rotation < 230.625) {
      return BlockFace.SOUTH;
    } else if (230.625 <= rotation && rotation < 303.75) {
      return BlockFace.WEST;
    } else if (303.75 <= rotation && rotation < 360.0) {
      return BlockFace.NORTH;
    } else {
      return null;
    }
  }

  public static void checkFile() {
    new File(DataFolder, "/frames/").mkdir();
    dir = new File(DataFolder, "/frames/");

    file = new File(dir, "data.yml");
    // Defines the dir and file variables
    if (!dir.exists() || !file.exists()) {
      setup();
    } else {
      customFile = YamlConfiguration.loadConfiguration(file);
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
}