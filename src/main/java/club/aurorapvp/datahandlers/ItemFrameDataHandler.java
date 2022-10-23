package club.aurorapvp.datahandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.plugin;
import static club.aurorapvp.listeners.CommandListener.commandArg0;
import static club.aurorapvp.listeners.CommandListener.mainHandData;
import static club.aurorapvp.listeners.CommandListener.p;
import static club.aurorapvp.listeners.EventListener.clickLoc;

import java.io.File;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

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
    if (getNewCardinalDirection() != BlockFace.EAST) {
      frame.teleport(b.getRelative(getNewCardinalDirection()).getLocation());
      frame.setFacingDirection(getNewCardinalDirection(), true);
    }

    frame.setItem(mainHandData);

    checkFile();
    get().set("frames." + commandArg0 + ".item", mainHandData);
    get().set("frames." + commandArg0 + ".location", frame.getLocation());
    save();
  }

  public static BlockFace getNewCardinalDirection() {
    double rotation = (p.getLocation().getYaw()) % 360;
    if (rotation < 0) {
      rotation += 360.0;
    }
    if (0 <= rotation && rotation < 22.5) {
      return BlockFace.NORTH;
    } else if (22.5 <= rotation && rotation < 67.5) {
      return BlockFace.NORTH_EAST;
    } else if (67.5 <= rotation && rotation < 112.5) {
      return BlockFace.EAST;
    } else if (112.5 <= rotation && rotation < 157.5) {
      return BlockFace.SOUTH_EAST;
    } else if (157.5 <= rotation && rotation < 202.5) {
      return BlockFace.SOUTH;
    } else if (202.5 <= rotation && rotation < 247.5) {
      return BlockFace.SOUTH_WEST;
    } else if (247.5 <= rotation && rotation < 292.5) {
      return BlockFace.WEST;
    } else if (292.5 <= rotation && rotation < 337.5) {
      return BlockFace.NORTH_WEST;
    } else if (337.5 <= rotation && rotation < 360.0) {
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