package club.aurorapvp.datahandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.plugin;
import static club.aurorapvp.config.LangHandler.getLangComponent;
import static club.aurorapvp.datahandlers.GUIDataHandler.deleteGUIEntry;

import club.aurorapvp.config.ConfigHandler;
import club.aurorapvp.config.LangHandler;
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
    saveKitFile(dir, arg);
  }

  public static void deleteKitData(CommandSender sender, String arg, String dir) {
    if (getKitFile(dir, arg) != null) {
      new File(DataFolder, "/kits/" + dir + "/" + arg + ".yml").delete();
      try {
        deleteGUIEntry(dir, arg);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      sender.sendMessage(getLangComponent("kit-deleted"));
    } else {
      sender.sendMessage(getLangComponent("kit-invalid"));
    }
  }

  public static void setupKitFile(String dir, String fileName) throws IOException {
    File file = new File(DataFolder, "/kits/" + dir + "/" + fileName + ".yml");
    if (!file.exists()) {
      file.getParentFile().mkdirs();

      file.createNewFile();
    }
    kitFile = YamlConfiguration.loadConfiguration(file);
  }

  public static YamlConfiguration getKitFile(String dir, String fileName) {
    File file = new File(DataFolder, "/kits/" + dir + "/" + fileName + ".yml");
    if (file.exists()) {
      kitFile = YamlConfiguration.loadConfiguration(file);
      return kitFile;
    } else {
      file = new File(DataFolder, "/kits/public/" + fileName + ".yml");
      if (file.exists()) {
        kitFile = YamlConfiguration.loadConfiguration(file);
        return kitFile;
      }
    }
    return null;
  }

  public static void saveKitFile(String dir, String fileName) throws IOException {
    File file = new File(DataFolder, "/kits/" + dir + "/" + fileName + ".yml");
    if (!file.exists()) {
      file.getParentFile().mkdirs();

      file.createNewFile();
    }
    kitFile.save(file);
  }

  public static int getKitAmount(UUID dir) {
    File file = new File(DataFolder, "/kits/" + dir + "/");

    if (file.listFiles() == null) {
      return new File(DataFolder, "/kits/public/").listFiles().length;
    } else {
      return file.listFiles().length + new File(DataFolder, "/kits/public/").listFiles().length;
    }
  }
}