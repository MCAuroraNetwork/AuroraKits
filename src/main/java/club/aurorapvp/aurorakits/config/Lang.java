package club.aurorapvp.aurorakits.config;

import club.aurorapvp.aurorakits.AuroraKits;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;

public class Lang {

  private final HashMap<String, String> PLACEHOLDERS = new HashMap<>();
  private final File FILE = new File(AuroraKits.getInstance().getDataFolder(), "lang.yml");
  private YamlConfiguration lang;

  public Lang() {
    this.reload();
    this.generateDefaults();
  }

  public void generateDefaults() {
    final HashMap<String, String> DEFAULTS = new HashMap<>();

    for (var path : getYaml().getKeys(false).toArray()) {
      if (Objects.requireNonNull(getYaml().getString((String) path)).startsWith("~")
          && Objects.requireNonNull(getYaml().getString((String) path)).endsWith("~")) {
        PLACEHOLDERS.put(
            (String) path, Objects.requireNonNull(getYaml().getString((String) path)).replace("~", ""));
      }
    }

    DEFAULTS.put("prefix", "~<gradient:#FFAA00:#FF55FF><bold>AuroraKits ><reset>~");
    DEFAULTS.put("reloaded", "prefix <green>Reloaded");
    DEFAULTS.put("frame-created", "prefix <green>Frame created!");
    DEFAULTS.put("frame-deleted", "prefix <red>Frame deleted");
    DEFAULTS.put("frame-invalid", "prefix <red>Invalid frame name");
    DEFAULTS.put("kit-gui", "<yellow><bold>Kits");
    DEFAULTS.put("kit-created",
        "prefix <green>Kit created! Use <bold>/kits<reset><green> to get it!");
    DEFAULTS.put("kit-updated",
        "prefix <green>Kit updated! Use <bold>/kits<reset><green> to get it!");
    DEFAULTS.put("kit-used", "prefix <green>Kit used!");
    DEFAULTS.put("kit-refilled", "prefix <green>Kit refilled!");
    DEFAULTS.put("kit-deleted", "prefix <red>Kit deleted");
    DEFAULTS.put("kit-too-many", "prefix <red>Too many kits");
    DEFAULTS.put("kit-invalid-name", "prefix <red>Invalid kit name");
    DEFAULTS.put("kit-invalid-item", "prefix <red>Invalid kit item");
    DEFAULTS.put("kit-not-found", "prefix <red>Kit not found");
    DEFAULTS.put("kit-already-exists", "prefix <red>Kit already exists");
    DEFAULTS.put("too-many-kits", "prefix <red>You have reached your maximum amount of kits!");
    DEFAULTS.put("not-your-kit", "prefix <red>That's not your kit!");
    DEFAULTS.put("next-page", "<yellow><bold>Next");
    DEFAULTS.put("previous-page", "<yellow><bold>Previous");
    DEFAULTS.put("no-kit", "<yellow><bold>No Kit");
    DEFAULTS.put(
        "help-command",
        """
            prefix <yellow><bold>All Commands:
                    
                    
            <blue><bold>/kits: <reset><yellow>Opens the Kit Menu
            
            <blue><bold>/kit [name]: <reset><yellow>Opens a Kit
            
            <blue><bold>/kit create [name]: <reset><yellow>Creates a Kit
            
            <blue><bold>/kit delete [name]: <reset><yellow>Deletes a kit
            """);

    for (String path : DEFAULTS.keySet()) {
      if (!getYaml().contains(path) || getYaml().getString(path) == null) {
        getYaml().set(path, DEFAULTS.get(path));
      }
    }

    try {
      getYaml().save(FILE);
    } catch (IOException e) {
      AuroraKits.getInstance().getLogger().log(Level.SEVERE, "Failed to save lang file", e);
    }

    for (var path : getYaml().getKeys(false).toArray()) {
      if (Objects.requireNonNull(getYaml().getString((String) path)).startsWith("~")
          && Objects.requireNonNull(getYaml().getString((String) path)).endsWith("~")) {
        PLACEHOLDERS.put(
            (String) path, Objects.requireNonNull(getYaml().getString((String) path)).replace("~", ""));
      }
    }
  }

  public Component getComponent(String message) {
    String pathString = getYaml().getString(message);
    assert pathString != null;

    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
      }
    }
    return MiniMessage.miniMessage().deserialize(pathString);
  }

  public Component getformattedComponentCommand(String message, String hover, String command, Object... args) {
    String pathString = getYaml().getString(message);
    assert pathString != null;
    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
      }
    }

    pathString = String.format(pathString, args);

    Component component = MiniMessage.miniMessage().deserialize(pathString);

    return component
            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command))
            .hoverEvent(HoverEvent.showText(Component.text(hover)));
  }

  public Component getComponentCommand(String message, String hover, String command) {
    String pathString = getYaml().getString(message);
    assert pathString != null;

    for (String placeholder : PLACEHOLDERS.keySet()) {
      if (pathString.contains(placeholder)) {
        pathString = pathString.replace(placeholder, PLACEHOLDERS.get(placeholder));
      }
    }

    Component component = MiniMessage.miniMessage().deserialize(pathString);

    return component
            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command))
            .hoverEvent(HoverEvent.showText(Component.text(hover)));
  }

  public YamlConfiguration getYaml() {
    return lang;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void reload() {
    if (!FILE.exists()) {
      try {
        FILE.getParentFile().mkdirs();
        FILE.createNewFile();

        lang = YamlConfiguration.loadConfiguration(FILE);

        this.generateDefaults();
      } catch (IOException e) {
        AuroraKits.getInstance().getLogger().log(Level.SEVERE, "Failed to generate lang file", e);
      }
    }
    lang = YamlConfiguration.loadConfiguration(FILE);
    AuroraKits.getInstance().getLogger().info("Lang reloaded!");
  }
}
