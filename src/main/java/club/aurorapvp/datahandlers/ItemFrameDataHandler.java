package club.aurorapvp.datahandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.plugin;
import static club.aurorapvp.config.LangHandler.getLangComponent;

import java.io.File;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

public class ItemFrameDataHandler {
  private static YamlConfiguration framesData;
  private static final File file = new File(DataFolder, "/frames/data.yml");

  public static void saveFrameData() throws IOException {
    getFrameData().save(file);
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

  public static void createFrameData(Player p, String frameName, ItemFrame frame) {
    getFrameData().set("frames." + frameName + ".item", p.getInventory().getItemInMainHand());
    getFrameData().set("frames." + frameName + ".location", frame.getLocation());
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

  public static void reloadFrameData() throws IOException {
    if (!file.exists()) {
      file.getParentFile().mkdirs();

      file.createNewFile();
    }
    framesData = YamlConfiguration.loadConfiguration(file);
    plugin.getLogger().info("Frame data reloaded!");
  }
}