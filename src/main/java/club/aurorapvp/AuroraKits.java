package club.aurorapvp;

import club.aurorapvp.configs.Config;
import club.aurorapvp.configs.Lang;
import club.aurorapvp.events.Events;
import club.aurorapvp.modules.Frame;
import club.aurorapvp.modules.Kit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuroraKits extends JavaPlugin {

  public static JavaPlugin INSTANCE;

  @Override
  public void onEnable() {
    long startTime = System.currentTimeMillis();

    INSTANCE = this;

    // Setup classes
    Commands.init();
    Commands.registerCompletions();
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

