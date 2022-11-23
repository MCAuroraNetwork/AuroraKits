package club.aurorapvp;

import static club.aurorapvp.config.ConfigHandler.generateConfigDefaults;
import static club.aurorapvp.config.ConfigHandler.setupDataFolder;
import static club.aurorapvp.config.LangHandler.generateLangDefaults;
import static club.aurorapvp.config.LangHandler.reloadLang;
import static club.aurorapvp.datahandlers.ItemFrameDataHandler.reloadFrameData;

import club.aurorapvp.listeners.CommandListener;
import club.aurorapvp.listeners.EventListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuroraKits extends JavaPlugin {

  public static Plugin plugin;
  public static File DataFolder;
  public static PlainTextComponentSerializer serializeComponent;
  public static MiniMessage deserializeComponent;

  @Override
  public void onEnable() {

    // Register important variables
    plugin = Bukkit.getPluginManager().getPlugin("AuroraKits");
    DataFolder = Bukkit.getServer().getPluginManager().getPlugin("AuroraKits").getDataFolder();
    serializeComponent = PlainTextComponentSerializer.plainText();
    deserializeComponent = MiniMessage.miniMessage();

    // Setup configs
    try {
      setupDataFolder();
      reloadLang();
      reloadConfig();
      reloadFrameData();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Generate default values
    try {
      generateLangDefaults();
      generateConfigDefaults();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Register Listeners
    getServer().getPluginManager().registerEvents(new EventListener(), this);
    List<Command> commandList = PluginCommandYamlParser.parse(plugin);
    for (Command command : commandList) {
      getCommand(command.getName()).setExecutor(new CommandListener());
    }

    getLogger().info("Aurora Kits Loaded");
  }

  @Override
  public void onDisable() {
    getLogger().info("Aurora Kits Unloaded");
  }
}

