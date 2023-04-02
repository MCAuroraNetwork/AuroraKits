package club.aurorapvp;

import club.aurorapvp.commands.Commands;
import club.aurorapvp.configs.Config;
import club.aurorapvp.configs.Lang;
import club.aurorapvp.events.Events;
import club.aurorapvp.modules.Frame;
import club.aurorapvp.modules.Kit;
import java.io.File;
import java.util.logging.Logger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuroraKits extends JavaPlugin {
  public static JavaPlugin INSTANCE;
  public static Logger LOGGER;
  public static File DATA_FOLDER;
  public static PlainTextComponentSerializer SERIALIZE_COMPONENT;
  public static MiniMessage DESERIALIZE_COMPONENT;

  @Override
  public void onEnable() {
    long startTime = System.currentTimeMillis();

    // Register important variables
    INSTANCE = this;
    LOGGER = this.getLogger();
    DATA_FOLDER = this.getDataFolder();
    SERIALIZE_COMPONENT = PlainTextComponentSerializer.plainText();
    DESERIALIZE_COMPONENT = MiniMessage.miniMessage();

    // Setup classes
    Config.init();
    Lang.init();
    Events.init();
    Frame.init();
    Kit.init();
    Commands.init();

    getLogger().info(
        "AuroraKits loaded in " + (System.currentTimeMillis() - startTime) +
            "ms");
  }

  @Override
  public void onDisable() {
    getLogger().info("AuroraKits unloaded");
  }
}

