package club.aurorapvp.datahandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.serializeComponent;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class GUIDataHandler {

  public static YamlConfiguration GUIData =
      YamlConfiguration.loadConfiguration(new File(DataFolder, "/GUIs/public.yml"));
  private static YamlConfiguration GUIFile;

  public static void setupGUIData() throws IOException {
    if (!new File(DataFolder, "/GUIs/public.yml").exists()) {
      new File(DataFolder, "/GUIs/public.yml").createNewFile();
    }
  }

  public static void setupGUIFile(UUID fileName) throws IOException {
    File file = new File(DataFolder, "/GUI/" + fileName + ".yml");

    if (!file.exists()) {
      file.createNewFile();
    }
    GUIFile = YamlConfiguration.loadConfiguration(file);
  }

  public static void saveGUIData() throws IOException {
    GUIData.save(new File(DataFolder, "/GUI/public.yml"));
  }

  public static void createGUIData(Player p, String arg) throws IOException {
    setupGUIFile(p.getUniqueId());

    GUIFile.set("kits." + arg + ".displayItem", p.getInventory().getItemInMainHand());
    GUIFile.set("kits." + arg + ".creator", serializeComponent.serialize(p.displayName()));
    saveGUIData();
  }

  public static void deleteGUIData(Player p, String arg) throws IOException {
    setupGUIFile(p.getUniqueId());

    GUIFile.set("kits." + arg, null);
    saveGUIData();
  }

  public static void createPublicGUIData(String arg, Player p) throws IOException {
    GUIData.set("kits." + arg + ".displayItem", p.getInventory().getItemInMainHand());
    GUIData.set("kits." + arg + ".creator", serializeComponent.serialize(p.displayName()));
    saveGUIData();
  }

  public static void deletePublicGUIData(String type, String arg) throws IOException {
    GUIData.set("kits." + arg, null);
    saveGUIData();
  }
}