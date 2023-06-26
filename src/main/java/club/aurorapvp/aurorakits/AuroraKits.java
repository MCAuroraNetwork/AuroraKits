package club.aurorapvp.aurorakits;

import club.aurorapvp.aurorakits.commands.Commands;
import club.aurorapvp.aurorakits.config.Config;
import club.aurorapvp.aurorakits.config.Lang;
import club.aurorapvp.aurorakits.events.Events;
import club.aurorapvp.aurorakits.modules.Frame;
import club.aurorapvp.aurorakits.modules.kits.Kit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuroraKits extends JavaPlugin {

  public static JavaPlugin INSTANCE;

  @Override
  public void onEnable() {
    long startTime = System.currentTimeMillis();

    INSTANCE = this;

    // Setup classes
    Commands.init();
    Config.init();
    Lang.init();
    Events.init();
    Frame.init();
    Kit.init();

    getLogger().info(
        "AuroraKits loaded in " + (System.currentTimeMillis() - startTime) +
            "ms");
  }

  @Override
  public void onDisable() {
    getLogger().info("AuroraKits unloaded");
  }
}

