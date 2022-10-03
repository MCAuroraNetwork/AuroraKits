package club.aurorapvp.filehandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.plugin;
import static club.aurorapvp.listeners.CommandListener.commandArg0;
import static club.aurorapvp.listeners.CommandListener.inventoryData;
import static club.aurorapvp.listeners.CommandListener.p;
import static club.aurorapvp.util.DataHandler.dir;
import static club.aurorapvp.util.DataHandler.file;
import static club.aurorapvp.util.DataHandler.get;
import static club.aurorapvp.util.DataHandler.save;

import club.aurorapvp.listeners.CommandListener;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class KitDataHandler {
  private static File guiFile;
  private static File[] files;

  private static FileConfiguration customFile;

  public static void setup() {
    new File(DataFolder,"/kits/").mkdir();
    new File(DataFolder,"/GUIs/").mkdir();
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
        plugin.getLogger().severe(String.format("Unable to save data for %s's %s in slot %s", inventoryData[i].getType(), i));
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
        plugin.getLogger().warning("Could not create kit");
      }
    }
    customFile = YamlConfiguration.loadConfiguration(file);
  }
  public static void createGUIEntry() throws IOException {
    dir = new File(DataFolder, "/GUIs/");
    if (!dir.exists()) {
      new File(DataFolder, "/GUIs/").mkdir();
    }
    guiFile = new File(dir,p.getUniqueId() + ".yml");
    if (!guiFile.exists()) {
      guiFile.createNewFile();
    }
    customFile = YamlConfiguration.loadConfiguration(guiFile);

    get().createSection("kits");
    get().set("kits." + commandArg0 + ".displayItem", p.getInventory().getItemInMainHand());
    get().save(guiFile);
  }
  public static void createPublicGUIEntry() throws IOException {
    dir = new File(DataFolder, "/GUIs/");
    if (!dir.exists()) {
      new File(DataFolder, "/GUIs/").mkdir();
    }
    guiFile = new File(dir, "public.yml");
    if (!guiFile.exists()) {
      guiFile.createNewFile();
    }
    customFile = YamlConfiguration.loadConfiguration(guiFile);

    get().createSection("kits");
    get().set("kits." + commandArg0 + ".displayItem", p.getInventory().getItemInMainHand());
    get().save(guiFile);
  }
  public static boolean checkKitAmount() {
    int length;
    files =  new File(DataFolder, "/kits/" + p.getUniqueId() + "/").listFiles();
    if (files != null) {
      length = files.length;
      files = new File(DataFolder, "/kits/" + p.getUniqueId() + "/").listFiles();
      if (files != null) {
        length = length + files.length;
        if (length >= 55) {
          return false;
        }
      }
    }
    return true;
  }
}