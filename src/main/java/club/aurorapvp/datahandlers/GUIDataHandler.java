package club.aurorapvp.datahandlers;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.serializeComponent;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class GUIDataHandler {
  private static YamlConfiguration GUIFile;
  private static final YamlConfiguration GUIPublicData =
      YamlConfiguration.loadConfiguration(new File(DataFolder, "/GUIs/public.yml"));

  public static void setupGUIPublicData() throws IOException {
    File file = new File(DataFolder, "/GUIs/public.yml");
    if (!file.exists()) {
      file.getParentFile().mkdirs();

      file.createNewFile();
    }
  }

  public static void setupGUIFile(UUID fileName) throws IOException {
    File file = new File(DataFolder, "/GUIs/" + fileName + ".yml");

    if (!file.exists()) {
      file.getParentFile().mkdirs();

      file.createNewFile();
    }
    GUIFile = YamlConfiguration.loadConfiguration(file);
  }

  public static void saveGUIPublicData() throws IOException {
    getGUIPublicData().save(new File(DataFolder, "/GUIs/public.yml"));
  }

  public static void saveGUIFile(UUID fileName) throws IOException {
      GUIFile.save(new File(DataFolder, "/GUIs/" + fileName + ".yml"));
  }

  public static void createGUIEntry(Player p, String arg) throws IOException {
    setupGUIFile(p.getUniqueId());

    GUIFile.set("kits." + arg + ".displayItem", p.getInventory().getItemInMainHand());
    GUIFile.set("kits." + arg + ".creator", serializeComponent.serialize(p.displayName()));
    saveGUIFile(p.getUniqueId());
  }

  public static void deleteGUIEntry(Player p, String arg) throws IOException {
    setupGUIFile(p.getUniqueId());

    GUIFile.set("kits." + arg, null);
    saveGUIPublicData();
  }

  public static void createGUIPublicData(String arg, Player p) throws IOException {
    getGUIPublicData().set("kits." + arg + ".displayItem", p.getInventory().getItemInMainHand());
    getGUIPublicData().set("kits." + arg + ".creator", serializeComponent.serialize(p.displayName()));
    saveGUIPublicData();
  }

  public static void deleteGUIPublicData(String type, String arg) throws IOException {
    getGUIPublicData().set("kits." + arg, null);
    saveGUIPublicData();
  }

  public static YamlConfiguration getGUIPublicData() {
    return GUIPublicData;
  }
}