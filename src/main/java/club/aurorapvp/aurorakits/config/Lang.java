package club.aurorapvp.aurorakits.config;

import club.aurorapvp.aurorakits.AuroraKits;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;

public class Lang {

  private static final HashMap<String, String> PLACEHOLDERS = new HashMap<>();
  private static final File FILE = new File(AuroraKits.INSTANCE.getDataFolder(), "lang.yml");
  private static YamlConfiguration lang;

  public static void init() {
    reload();
    generateDefaults();
  }

  public static void generateDefaults() {
    final HashMap<String, String> DEFAULTS = new HashMap<>();

    for (Object path : get().getKeys(false).toArray()) {
      if (Objects.requireNonNull(get().getString((String) path)).startsWith("~")
          && Objects.requireNonNull(get().getString((String) path)).endsWith("~")) {
        PLACEHOLDERS.put(
            (String) path, Objects.requireNonNull(get().getString((String) path)).replace("~", ""));
      }
    }

    DEFAULTS.put("prefix", "~<gradient:#FFAA00:#FF55FF><bold>AuroraKits ><reset>~");
    DEFAULTS.put("frame-created", "prefix <green>Frame created!");
    DEFAULTS.put("frame-deleted", "prefix <red>Frame deleted");
    DEFAULTS.put("frame-invalid", "prefix <red>Invalid frame name");
    DEFAULTS.put("GUIName", "<aqua>KitGUI");
    DEFAULTS.put("kit-created", "prefix <green>Kit created! Use /kits");
    DEFAULTS.put("kit-updated", "prefix <green>Kit updated! Use /kits");
    DEFAULTS.put("kit-used", "prefix <green>Kit used!");
    DEFAULTS.put("kit-deleted", "prefix <red>Kit deleted");
    DEFAULTS.put("kit-too-many", "prefix <red>Too many kits");
    DEFAULTS.put("kit-invalid-name", "prefix <red>Invalid kit name");
    DEFAULTS.put("kit-invalid-item", "prefix <red>Invalid kit item");
    DEFAULTS.put("kit-not-found", "prefix <red>Kit not found");
    DEFAULTS.put("kit-already-exists", "prefix <red>Kit already exists");
    DEFAULTS.put("next-page", "<aqua><bold>Next");
    DEFAULTS.put("previous-page", "<aqua><bold>Previous");
    DEFAULTS.put("unknown-kit-command", "prefix <red>Unknown Kit Command! Use /help");
    DEFAULTS.put(
        "help-command",
        """
            prefix <aqua><bold>All Commands:
                    
            <blue><bold>/kits: <reset><yellow>Opens the Kit Menu
            <blue><bold>/kit [name]: <reset><yellow>Opens a Kit
            <blue><bold>/kit create [name]: <reset><yellow>Creates a Kit
            <blue><bold>/kit delete [name]: <reset><yellow>Deletes a kit
            """);

    for (String path : DEFAULTS.keySet()) {
      if (!get().contains(path) || get().getString(path) == null) {
        get().set(path, DEFAULTS.get(path));
      }
    }

    try {
      get().save(FILE);
    } catch (IOException e) {
      AuroraKits.INSTANCE.getLogger().log(Level.SEVERE, "Failed to save lang file", e);
    }

    for (Object path : get().getKeys(false).toArray()) {
      if (Objects.requireNonNull(get().getString((String) path)).startsWith("~")
          && Objects.requireNonNull(get().getString((String) path)).endsWith("~")) {
        PLACEHOLDERS.put(
            (String) path, Objects.requireNonNull(get().getString((String) path)).replace("~", ""));
      }
    }
  }

  @SuppressWarnings("unused")
  public static String getString(String message) {
    String pathString = get().getString(message);
    for (String placeholder : PLACEHOLDERS.keySet()) {
      assert pathString != null;
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
      }
    }
    return pathString;
  }

  @SuppressWarnings("unused")
  public static Component formatComponent(String message, Object... args) {
    String pathString = get().getString(message);
    assert pathString != null;
    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
      }
    }

    pathString = String.format(pathString, args);

    return MiniMessage.miniMessage().deserialize(pathString);
  }

  public static Component getComponent(String message) {
    String pathString = get().getString(message);
    assert pathString != null;

    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
      }
    }
    return MiniMessage.miniMessage().deserialize(pathString);
  }

  public static YamlConfiguration get() {
    return lang;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public static void reload() {
    if (!FILE.exists()) {
      try {
        FILE.getParentFile().mkdirs();
        FILE.createNewFile();
      } catch (IOException e) {
        AuroraKits.INSTANCE.getLogger().log(Level.SEVERE, "Failed to generate lang file", e);
      }
    }
    lang = YamlConfiguration.loadConfiguration(FILE);
    AuroraKits.INSTANCE.getLogger().info("Lang reloaded!");
  }
}
