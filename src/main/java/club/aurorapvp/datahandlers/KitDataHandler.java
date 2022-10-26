package club.aurorapvp.datahandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.plugin;
import static club.aurorapvp.datahandlers.GUIHandler.createGUIEntry;
import static club.aurorapvp.datahandlers.GUIHandler.createPublicGUIEntry;
import static club.aurorapvp.datahandlers.GUIHandler.deleteGUIEntry;
import static club.aurorapvp.datahandlers.GUIHandler.deletePublicGUIEntry;
import static club.aurorapvp.listeners.CommandListener.commandArg0;
import static club.aurorapvp.listeners.CommandListener.inventoryData;
import static club.aurorapvp.listeners.CommandListener.p;

import club.aurorapvp.listeners.CommandListener;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class KitDataHandler {
  private static File[] files;
  private static File file;
  private static File dir;
  private static FileConfiguration customFile;

  public static void setup() {
    new File(DataFolder, "/kits/").mkdir();
    new File(DataFolder, "/GUIs/").mkdir();
  }

  public static void create() {
    setFile();

    for (int i = 0; i < inventoryData.length; i++) {
      try {
        get().set("items." + i, inventoryData[i]);
      } catch (Exception e) {
        plugin.getLogger().severe("Unable to save kit");
        e.printStackTrace();
      }
    }
    save();
    try {
      createGUIEntry();
    } catch (IOException e) {
      plugin.getLogger().warning("Couldn't save GUI Data");
    }
  }

  public static void createPublic() {
    dir = new File(DataFolder, "/kits/public/");
    if (!dir.exists()) {
      new File(DataFolder, "/kits/public/").mkdir();
    }

    file = new File(dir, CommandListener.commandArg0 + ".yml");
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        plugin.getLogger().warning("Could not create kit");
      }
    }
    customFile = YamlConfiguration.loadConfiguration(file);

    for (int i = 0; i < inventoryData.length; i++) {
      try {
        get().set("items." + i, inventoryData[i]);
      } catch (Exception e) {
        plugin.getLogger().severe(
            String.format("Unable to save data for %s's %s in slot %s", inventoryData[i].getType(),
                i));
        e.printStackTrace();
      }
    }
    save();
    try {
      createPublicGUIEntry();
    } catch (IOException e) {
      plugin.getLogger().warning("Couldn't save GUI Data");
    }
  }

  public static void delete() {
    setFile();
    file.delete();
    deleteGUIEntry();
  }

  public static void deletePublic() {
    dir = new File(DataFolder, "/kits/public/");
    if (!dir.exists()) {
      new File(DataFolder, "/kits/public/").mkdir();
    }

    file = new File(dir, CommandListener.commandArg0 + ".yml");
    if (file.exists()) {
      file.delete();
    }
    deletePublicGUIEntry();
  }

  public static void checkKits() {
    dir = new File(DataFolder, "/kits/" + p.getUniqueId() + "/");
    if (!dir.exists()) {
      new File(DataFolder, "/kits/" + p.getUniqueId() + "/").mkdir();
    }

    file = new File(dir, commandArg0 + ".yml");
    if (file.exists()) {
      customFile = YamlConfiguration.loadConfiguration(file);
    } else {
      file = new File(DataFolder, "/kits/public/" + commandArg0 + ".yml");
      if (file.exists()) {
        customFile = YamlConfiguration.loadConfiguration(file);
      } else {
        customFile = null;
      }
    }
  }

  public static void setFile() {
    // Defines the dir and file variables
    dir = new File(DataFolder, "/kits/" + p.getUniqueId() + "/");
    if (!dir.exists()) {
      new File(DataFolder, "/kits/" + p.getUniqueId() + "/").mkdir();
    }

    file = new File(dir, commandArg0 + ".yml");
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        plugin.getLogger().warning("Could not find kit");
      }
    }
    customFile = YamlConfiguration.loadConfiguration(file);
  }

  public static int checkKitAmount() {
    if (new File(DataFolder, "/kits/" + p.getUniqueId() + "/").listFiles() != null) {
      return new File(DataFolder, "/kits/" + p.getUniqueId() + "/").listFiles().length;
    }
    return 0;
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