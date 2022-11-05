package club.aurorapvp.datahandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.serializeComponent;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class GUIDataHandler {
  private static YamlConfiguration GUIFile;

  public static void setupGUIFile(String fileName) throws IOException {
    File file = new File(DataFolder, "/GUIs/" + fileName + ".yml");

    if (!file.exists()) {
      file.getParentFile().mkdirs();

      file.createNewFile();
    }
    GUIFile = YamlConfiguration.loadConfiguration(file);
  }
  public static void saveGUIFile(String fileName) throws IOException {
      GUIFile.save(new File(DataFolder, "/GUIs/" + fileName + ".yml"));
  }

  public static void createGUIEntry(Player p, String fileName, String arg) throws IOException {
    setupGUIFile(fileName);

    GUIFile.set("kits." + arg + ".displayItem", p.getInventory().getItemInMainHand());
    GUIFile.set("kits." + arg + ".creator", serializeComponent.serialize(p.displayName()));
    if (Objects.equals(fileName, "public")) {
      GUIFile.set("kits." + arg + ".type", 0);
    } else {
      GUIFile.set("kits" + arg + ".type", 1);
    }
    saveGUIFile(fileName);
  }

  public static void deleteGUIEntry(String fileName, String arg) throws IOException {
    setupGUIFile(fileName);

    GUIFile.set("kits." + arg, null);
    saveGUIFile("public");
  }

  public static void createGUIPublicData(String arg, Player p) throws IOException {
    GUIFile.set("kits." + arg + ".displayItem", p.getInventory().getItemInMainHand());
    GUIFile.set("kits." + arg + ".creator", serializeComponent.serialize(p.displayName()));
    saveGUIFile("public");
  }

  public static void deleteGUIPublicData(String type, String arg) throws IOException {
    GUIFile.set("kits." + arg, null);
    saveGUIFile("public");
  }
}