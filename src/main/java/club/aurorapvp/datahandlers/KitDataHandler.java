package club.aurorapvp.datahandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.plugin;
import static club.aurorapvp.config.LangHandler.getLangComponent;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class KitDataHandler {

  private static YamlConfiguration kitFile;

  public static void createKitData(Player p, String arg, String dir) throws IOException {
    setupKitFile(dir, arg);

    for (int i = 0; i < p.getInventory().getContents().length; i++) {
      try {
        kitFile.set("items." + i, p.getInventory().getContents()[i]);
      } catch (Exception e) {
        plugin.getLogger().severe("Unable to save kit");
        e.printStackTrace();
      }
    }
    saveKitFile();
  }

  public static void deleteKitData(CommandSender sender, String arg, String dir) {
    if (getKitFile(dir, arg) != null) {
      new File(DataFolder, "/GUI/" + dir + "/" + arg + ".yml").delete();
    } else {
      sender.sendMessage(getLangComponent("kit-invalid"));
    }
  }

  public static void setupKitFile(String dir, String fileName) throws IOException {
    File file = new File(DataFolder, "/GUI/" + dir + "/" + fileName + ".yml");

    if (!file.exists()) {
      file.createNewFile();
    }
    kitFile = YamlConfiguration.loadConfiguration(file);
  }

  public static YamlConfiguration getKitFile(String dir, String fileName) {
    File file = new File(DataFolder, "/GUI/" + dir + "/" + fileName + ".yml");
    if (file.exists()) {
      kitFile = YamlConfiguration.loadConfiguration(file);
    } else {
      kitFile = null;
    }
    return kitFile;
  }

  public static void saveKitFile() throws IOException {
    kitFile.save(new File(DataFolder, "/GUI/data.yml"));
  }

  public static int getKitAmount(Player p) {
    return new File(DataFolder, "/kits/" + p.getUniqueId() + "/").listFiles().length;
  }
}