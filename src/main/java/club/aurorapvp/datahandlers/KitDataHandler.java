package club.aurorapvp.datahandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.plugin;
import static club.aurorapvp.config.LangHandler.getLangComponent;
import static club.aurorapvp.datahandlers.GUIDataHandler.deleteGUIEntry;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class KitDataHandler {

  private static YamlConfiguration kitFile;

  public static void createKitData(Player p, String kitName, String kitLocation)
      throws IOException {
    setupKitFile(kitLocation, kitName);

    for (int i = 0; i < p.getInventory().getContents().length; i++) {
      try {
        kitFile.set("items." + i, p.getInventory().getContents()[i]);
      } catch (Exception e) {
        plugin.getLogger().severe("Unable to save kit");
        e.printStackTrace();
      }
    }
    saveKitFile(kitLocation, kitName);
  }

  public static void deleteKitData(CommandSender sender, String kitName, String kitLocation) {
    if (getKitFile(kitName, kitName) != null) {
      new File(DataFolder, "/kits/" + kitLocation + "/" + kitName + ".yml").delete();
      try {
        deleteGUIEntry(kitName, kitName);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      sender.sendMessage(getLangComponent("kit-deleted"));
    } else {
      sender.sendMessage(getLangComponent("kit-invalid-name"));
    }
  }

  public static void setupKitFile(String kitLocation, String kitName) throws IOException {
    File file = new File(DataFolder, "/kits/" + kitLocation + "/" + kitName + ".yml");
    if (!file.exists()) {
      file.getParentFile().mkdirs();

      file.createNewFile();
    }
    kitFile = YamlConfiguration.loadConfiguration(file);
  }

  public static YamlConfiguration getKitFile(String kitLocation, String kitName) {
    File file = new File(DataFolder, "/kits/" + kitLocation + "/" + kitName + ".yml");
    if (file.exists()) {
      kitFile = YamlConfiguration.loadConfiguration(file);
      return kitFile;
    } else {
      file = new File(DataFolder, "/kits/public/" + kitName + ".yml");
      if (file.exists()) {
        kitFile = YamlConfiguration.loadConfiguration(file);
        return kitFile;
      }
    }
    return null;
  }

  public static void saveKitFile(String kitLocation, String kitName) throws IOException {
    File file = new File(DataFolder, "/kits/" + kitLocation + "/" + kitName + ".yml");
    if (!file.exists()) {
      file.getParentFile().mkdirs();

      file.createNewFile();
    }
    kitFile.save(file);
  }

  public static int getKitAmount(UUID playerUUID) {
    File file = new File(DataFolder, "/kits/" + playerUUID + "/");

    if (file.listFiles() == null) {
      return new File(DataFolder, "/kits/public/").listFiles().length;
    } else {
      return file.listFiles().length + new File(DataFolder, "/kits/public/").listFiles().length;
    }
  }
}