package club.aurorapvp.aurorakits;

import club.aurorapvp.aurorakits.commands.CommandManager;
import club.aurorapvp.aurorakits.config.Config;
import club.aurorapvp.aurorakits.config.Lang;
import club.aurorapvp.aurorakits.events.EventManager;
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
    CommandManager.init();
    Config.init();
    Lang.init();
    EventManager.init();
    Frame.init();
    Kit.init();

    getLogger().info(
        "AuroraKits enabled in " + (System.currentTimeMillis() - startTime) +
            "ms");
  }

  @Override
  public void onDisable() {
    getLogger().info("AuroraKits disabled");
  }
}

