package club.aurorapvp.aurorakits;

import club.aurorapvp.aurorakits.commands.CommandManager;
import club.aurorapvp.aurorakits.config.Config;
import club.aurorapvp.aurorakits.config.Lang;
import club.aurorapvp.aurorakits.events.EventManager;
import club.aurorapvp.aurorakits.modules.Frame;
import club.aurorapvp.aurorakits.modules.kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuroraKits extends JavaPlugin {
  public static JavaPlugin INSTANCE;
  private static boolean auroraCombatInstalled;

  public boolean isAuroraCombatInstalled() {
    return auroraCombatInstalled;
  }

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

    // Check soft depends
    if (Bukkit.getPluginManager().getPlugin("AuroraCombat") != null) {
      if (Config.get().getBoolean("optional-plugins.auroracombat-compatibility")) {
        auroraCombatInstalled = true;
      }
    }

    getLogger().info("AuroraKits enabled in " + (System.currentTimeMillis() - startTime) + "ms");
  }

  @Override
  public void onDisable() {
    getLogger().info("AuroraKits disabled");
  }
}
