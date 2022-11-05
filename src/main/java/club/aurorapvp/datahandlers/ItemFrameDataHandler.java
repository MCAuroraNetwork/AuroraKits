package club.aurorapvp.datahandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.config.LangHandler.getLangComponent;

import java.io.File;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

public class ItemFrameDataHandler {
  private static YamlConfiguration framesData = null;

  public static void setupFrameData() throws IOException {
    File file = new File(DataFolder, "/frames/data.yml");
    if (!file.exists()) {
      file.getParentFile().mkdirs();

      file.createNewFile();
    }
  }

  public static void setFrameData() {
    framesData =
        YamlConfiguration.loadConfiguration(new File(DataFolder, "/frames/data.yml"));
  }

  public static void saveFrameData() throws IOException {
    getFrameData().save(new File(DataFolder, "/frames/data.yml"));
  }

  public static void deleteFrameData(CommandSender sender, String arg) {
    if (arg != null) {

      getFrameData().set("frames." + arg, null);
      try {
        saveFrameData();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      sender.sendMessage(getLangComponent("frame-deleted"));
    } else {
      sender.sendMessage(getLangComponent("frame-invalid"));
    }
  }

  public static void createFrameData(Player p, String arg, ItemFrame frame) {
    getFrameData().set("frames." + arg + ".item", p.getInventory().getItemInMainHand());
    getFrameData().set("frames." + arg + ".location", frame.getLocation());
    try {
      saveFrameData();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String getFrame(Location clickLoc) {
    for (Object path : getFrameData().getConfigurationSection("frames").getKeys(false).toArray()) {
      if (getFrameData().getLocation("frames." + path + ".location").equals(clickLoc)) {
        return (String) path;
      }
    }
    return null;
  }

  public static YamlConfiguration getFrameData() {
    return framesData;
  }
}