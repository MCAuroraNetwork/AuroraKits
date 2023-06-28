package club.aurorapvp.aurorakits.config;

import club.aurorapvp.aurorakits.AuroraKits;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
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
      if (Objects.requireNonNull(get().getString((String) path)).startsWith("~") &&
          Objects.requireNonNull(get().getString((String) path)).endsWith("~")) {
        PLACEHOLDERS.put((String) path, Objects.requireNonNull(get().getString((String) path))
            .replace("~", ""));
      }
    }

    DEFAULTS.put("prefix", "~<gradient:#FFAA00:#FF55FF><bold>AuroraKits ><reset>~");
    DEFAULTS.put("frame-created", "prefix <gradient:#FFAA00:#FF55FF>Frame successfully created");
    DEFAULTS.put("frame-deleted", "prefix <gradient:#FFAA00:#FF55FF>Frame successfully deleted");
    DEFAULTS.put("frame-invalid", "prefix <gradient:#FFAA00:#FF55FF>Invalid frame name!");
    DEFAULTS.put("GUIName", "<gradient:#FFAA00:#FF55FF>KitGUI");
    DEFAULTS.put("kit-created",
        "prefix <gradient:#FFAA00:#FF55FF>Kit successfully created! Use /kits to access it!");
    DEFAULTS.put("kit-updated",
        "prefix <gradient:#FFAA00:#FF55FF>Kit successfully updated! Use /kits to access it!");
    DEFAULTS.put("kit-used", "prefix <gradient:#FFAA00:#FF55FF>Kit successfully used!");
    DEFAULTS.put("kit-deleted", "prefix <gradient:#FFAA00:#FF55FF>Kit successfully deleted");
    DEFAULTS.put("kit-too-many", "prefix <gradient:#FFAA00:#FF55FF>You have too many kits!");
    DEFAULTS.put("kit-invalid-name", "prefix <gradient:#FFAA00:#FF55FF>Invalid kit name!");
    DEFAULTS.put("kit-invalid-item", "prefix <gradient:#FFAA00:#FF55FF>Invalid kit display item!");
    DEFAULTS.put("kit-not-found", "prefix <gradient:#FFAA00:#FF55FF>Kit not found!");
    DEFAULTS.put("kit-already-exists", "prefix <gradient:#FFAA00:#FF55FF>Kit already exists!");
    DEFAULTS.put("next-page", "<gradient:#FFAA00:#FF55FF><bold>Next Page");
    DEFAULTS.put("previous-page", "<gradient:#FFAA00:#FF55FF><bold>Previous Page");
    DEFAULTS.put("unknown-frame-command",
        "prefix <gradient:#FFAA00:#FF55FF>Unknown Frame Command! Use /help for help!");
    DEFAULTS.put("unknown-kit-command",
        "prefix <gradient:#FFAA00:#FF55FF>Unknown Kit Command! Use /help for help!");
    DEFAULTS.put("help-command", """
        prefix <gradient:#FFAA00:#FF55FF>All Commands:\s
        <blue><bold>/kits: <reset><yellow>Opens the Kit Menu\s
        <blue><bold>/kit [name]: <reset><yellow>Opens a Kit\s
        <blue><bold>/kit create [name]: <reset><yellow>Creates a Kit\s
        <blue><bold>/kit delete [name]: <reset><yellow>Deletes a kit""");

    for (String path : DEFAULTS.keySet()) {
      if (!get().contains(path) || get().getString(path) == null) {
        get().set(path, DEFAULTS.get(path));
      }
    }

    try {
      get().save(FILE);
    } catch (IOException e) {
      AuroraKits.INSTANCE.getLogger().severe("Failed to save lang file");
    }

    for (Object path : get().getKeys(false).toArray()) {
      if (Objects.requireNonNull(get().getString((String) path)).startsWith("~") &&
          Objects.requireNonNull(get().getString((String) path)).endsWith("~")) {
        PLACEHOLDERS.put((String) path, Objects.requireNonNull(get().getString((String) path))
            .replace("~", ""));
      }
    }
  }

  @SuppressWarnings("unused")
  public static String getString(String message) {
    String pathString = get().getString(message);
    for (String placeholder : PLACEHOLDERS.keySet()) {
      assert pathString != null;
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder,
            PLACEHOLDERS.get(placeholder));
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
        pathString = pathString.replace(placeholder,
            PLACEHOLDERS.get(placeholder));
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
        pathString = pathString.replace(placeholder,
            PLACEHOLDERS.get(placeholder));
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
        AuroraKits.INSTANCE.getLogger().severe("Failed to generate lang file");
      }
    }
    lang = YamlConfiguration.loadConfiguration(FILE);
    AuroraKits.INSTANCE.getLogger().info("Lang reloaded!");
  }
}