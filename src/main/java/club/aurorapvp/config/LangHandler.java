package club.aurorapvp.config;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.deserializeComponent;
import static club.aurorapvp.AuroraKits.lang;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;

public class LangHandler {
  private static final HashMap<String, String> placeholders = new HashMap<>();
  private static final HashMap<String, String> Defaults = new HashMap<>();

  public static void setupLangFile() throws IOException {
    if (!new File(DataFolder, "lang.yml").exists()) {
      new File(DataFolder, "lang.yml").createNewFile();
    }
  }

  public static void generateLangDefaults() throws IOException {
    for (Object path : getLangFile().getKeys(false).toArray()) {
      if (getLangFile().getString((String) path).startsWith("~") &&
          getLangFile().getString((String) path).endsWith("~")) {
        placeholders.put((String) path, getLangFile().getString((String) path).replace("~", ""));
      }
    }

    Defaults.put("prefix", "~<gradient:#FFAA00:#FF55FF><bold>AuroraKits ><reset>~");
    Defaults.put("frame-created",
        "prefix <gradient:#FFAA00:#FF55FF>Frame successfully created");
    Defaults.put("frame-deleted", "prefix <gradient:#FFAA00:#FF55FF>Frame successfully deleted");
    Defaults.put("frame-invalid", "prefix <gradient:#FFAA00:#FF55FF>Invalid frame name!");
    Defaults.put("GUIName", "<gradient:#FFAA00:#FF55FF>KitGUI");
    Defaults.put("kit-created",
        "prefix <gradient:#FFAA00:#FF55FF>Kit sucessfully created! Use /kits to access it!");
    Defaults.put("kit-used", "prefix <gradient:#FFAA00:#FF55FF>Kit sucessfully used!");
    Defaults.put("kit-deleted", "prefix <gradient:#FFAA00:#FF55FF>Kit sucessfully deleted");
    Defaults.put("kit-too-many", "prefix <gradient:#FFAA00:#FF55FF>You have too many kits!");
    Defaults.put("kit-invalid-name", "prefix <gradient:#FFAA00:#FF55FF>Invalid kit name!");
    Defaults.put("kit-invalid-item", "prefix <gradient:#FFAA00:#FF55FF>Invalid kit display item!");
    Defaults.put("kit-not-found", "prefix <gradient:#FFAA00:#FF55FF>Kit not found!");

    for (String path : Defaults.keySet()) {
      if (!getLangFile().contains(path) || getLangFile().getString(path) == null) {
        getLangFile().set(path, Defaults.get(path));
        getLangFile().save(new File(DataFolder, "lang.yml"));
      }
    }
  }

  public static Component getLangComponent(String path) {
    if (getLangFile().contains(path)) {
      String pathString = getLangFile().getString(path);
      for (String placeholder : placeholders.keySet()) {
        if (pathString.contains(placeholder)) {
          pathString = pathString.replace(placeholder,
              placeholders.get(placeholder));
        }
      }
      return deserializeComponent.deserialize(pathString);
    }
    return null;
  }

  public static void saveLangFile() throws IOException {
    getLangFile().save(new File(DataFolder, "lang.yml"));
  }
  public static YamlConfiguration getLangFile() {
    return lang;
  }
}
