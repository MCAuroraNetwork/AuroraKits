package club.aurorapvp.datahandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.serializeComponent;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class GUIDataHandler {
  public static YamlConfiguration getGUIFile(String fileName) throws IOException {
    File file = new File(DataFolder, "/GUIs/" + fileName + ".yml");

    if (!file.exists()) {
      file.getParentFile().mkdirs();

      file.createNewFile();
    }
    return YamlConfiguration.loadConfiguration(file);
  }

  public static void saveGUIFile(String fileName) throws IOException {
    getGUIFile(fileName).save(new File(DataFolder, "/GUIs/" + fileName + ".yml"));
  }

  public static void createGUIEntry(Player p, String fileName, String arg) throws IOException {
    getGUIFile(fileName).set("kits." + arg + ".displayItem", p.getInventory().getItemInMainHand());
    getGUIFile(fileName).set("kits." + arg + ".creator",
        serializeComponent.serialize(p.displayName()));
    if (Objects.equals(fileName, "public")) {
      getGUIFile(fileName).set("kits." + arg + ".type", 0);
    } else {
      getGUIFile(fileName).set("kits." + arg + ".type", 1);
    }
    saveGUIFile(fileName);
  }

  public static void deleteGUIEntry(String fileName, String arg) throws IOException {
    getGUIFile(fileName).set("kits." + arg, null);
    saveGUIFile(fileName);
  }
}