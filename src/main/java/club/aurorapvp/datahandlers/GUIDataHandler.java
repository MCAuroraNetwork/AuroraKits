package club.aurorapvp.datahandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.serializeComponent;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class GUIDataHandler {
  public static YamlConfiguration GUIFile;

  public static YamlConfiguration getGUIFile() {
    return GUIFile;
  }

  public static void setGUIFile(String GUIMenu) throws IOException {
    File file = new File(DataFolder, "/GUIs/" + GUIMenu + ".yml");

    if (!file.exists()) {
      file.getParentFile().mkdirs();

      file.createNewFile();
    }
    GUIFile = YamlConfiguration.loadConfiguration(file);
  }

  public static void saveGUIFile(String GUIMenu) throws IOException {
    GUIFile.save(new File(DataFolder, "/GUIs/" + GUIMenu + ".yml"));
  }

  public static void createGUIEntry(Player p, String GUIMenu, String kitName) throws IOException {
    setGUIFile(GUIMenu);

    getGUIFile().set("kits." + kitName + ".displayItem", p.getInventory().getItemInMainHand());
    getGUIFile().set("kits." + kitName + ".creator",
        serializeComponent.serialize(p.displayName()));
    if (Objects.equals(GUIMenu, "public")) {
      getGUIFile().set("kits." + kitName + ".type", 0);
    } else {
      getGUIFile().set("kits." + kitName + ".type", 1);
    }
    saveGUIFile(GUIMenu);
  }

  public static void deleteGUIEntry(String GUIMenu, String KitName) throws IOException {
    setGUIFile(GUIMenu);

    getGUIFile().set("kits." + KitName, null);
    saveGUIFile(GUIMenu);
  }
}