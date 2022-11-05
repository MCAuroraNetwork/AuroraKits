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
  public static YamlConfiguration framesData =
      YamlConfiguration.loadConfiguration(new File(DataFolder, "/frames/data.yml"));

  public static void setupFrameData() throws IOException {
    if (!new File(DataFolder, "/frames/data.yml").exists()) {
      new File(DataFolder, "/frames/data.yml").createNewFile();
    }
  }

  public static void saveFrameData() throws IOException {
    framesData.save(new File(DataFolder, "/frames/data.yml"));
  }

  public static void deleteFrameData(CommandSender sender, String arg) {
    if (arg != null) {

      framesData.set("frames." + arg, null);
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

  public static void createFrameData(String arg, Player p, ItemFrame frame) {
    framesData.set("frames." + arg + ".item", p.getActiveItem());
    framesData.set("frames." + arg + ".location", frame.getLocation());
    try {
      saveFrameData();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String getFrameData(Location clickLoc) {
    for (Object path : framesData.getConfigurationSection("frames").getKeys(false).toArray()) {
      if (framesData.getLocation("frames." + path + ".location").equals(clickLoc)) {
        return (String) path;
      }
    }
    return null;
  }
}