package club.aurorapvp;

import club.aurorapvp.config.Config;
import club.aurorapvp.config.Lang;
import club.aurorapvp.listeners.Commands;
import club.aurorapvp.listeners.Events;
import club.aurorapvp.modules.Frame;
import club.aurorapvp.modules.Kit;
import java.io.File;
import java.io.IOException;
import java.util.List;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuroraKits extends JavaPlugin {
  public static Plugin PLUGIN;
  public static File DATA_FOLDER;
  public static PlainTextComponentSerializer SERIALIZE_COMPONENT;
  public static MiniMessage DESERIALIZE_COMPONENT;

  @Override
  public void onEnable() {
    // Register important variables
    PLUGIN = this;
    DATA_FOLDER = this.getDataFolder();
    SERIALIZE_COMPONENT = PlainTextComponentSerializer.plainText();
    DESERIALIZE_COMPONENT = MiniMessage.miniMessage();

    // Setup classes
    try {
      Lang.reload();
      Config.reload();
      Frame.setup();
      Kit.setup();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Generate default values
    try {
      Lang.generateDefaults();
      Config.generateDefaults();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Register Listeners
    getServer().getPluginManager().registerEvents(new Events(), this);
    List<Command> commandList = PluginCommandYamlParser.parse(PLUGIN);
    for (Command command : commandList) {
      getCommand(command.getName()).setExecutor(new Commands());
    }

    getLogger().info("Aurora Kits Loaded");
  }

  @Override
  public void onDisable() {
    getLogger().info("Aurora Kits Unloaded");
  }
}

