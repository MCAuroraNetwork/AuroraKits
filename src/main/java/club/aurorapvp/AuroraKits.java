package club.aurorapvp;

import static club.aurorapvp.config.CustomConfigHandler.generateDefaults;

import club.aurorapvp.config.CustomConfigHandler;
import club.aurorapvp.datahandlers.ItemFrameDataHandler;
import club.aurorapvp.datahandlers.KitDataHandler;
import club.aurorapvp.listeners.CommandListener;
import club.aurorapvp.listeners.EventListener;
import java.io.File;
import java.util.List;
import net.kyori.adventure.text.Component;
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
  public static String prefix;

  @Override
  public void onEnable() {

    //Register important variables
    plugin = Bukkit.getPluginManager().getPlugin("AuroraKits");

    // Register Listeners
    getServer().getPluginManager().registerEvents(new EventListener(), this);
    List<Command> commandList = PluginCommandYamlParser.parse(plugin);
    for (Command command : commandList) {
      getCommand(command.getName()).setExecutor(new CommandListener());
    }

    // Config setup
    saveDefaultConfig();
    CustomConfigHandler.setup();
    generateDefaults();

    // Setup directories
    DataFolder = Bukkit.getServer().getPluginManager().getPlugin("AuroraKits").getDataFolder();
    KitDataHandler.setup();
    ItemFrameDataHandler.setup();

    serializeComponent = PlainTextComponentSerializer.plainText();
    prefix = "<gradient:#FFAA00:#FF55FF><bold>AuroraKits > <reset>";

    getLogger().info("Aurora Kits Loaded");
  }

  @Override
  public void onDisable() {
    getLogger().info("Aurora Kits Unloaded");
  }
}

