package club.aurorapvp.aurorakits;

import club.aurorapvp.aurorakits.commands.CommandManager;
import club.aurorapvp.aurorakits.config.Config;
import club.aurorapvp.aurorakits.config.Lang;
import club.aurorapvp.aurorakits.events.EventManager;
import club.aurorapvp.aurorakits.modules.Frame;
import club.aurorapvp.aurorakits.modules.kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class AuroraKits extends JavaPlugin {

  private static AuroraKits INSTANCE;
  private static boolean auroraCombatInstalled;
  private Config config;
  private Lang lang;

  public static AuroraKits getInstance() {
    return INSTANCE;
  }
  public static boolean isAuroraCombatInstalled() {
    return auroraCombatInstalled;
  }

  public @NotNull YamlConfiguration getConfig() {
    return config.getYaml();
  }

  public Lang getLang() {
    return lang;
  }

  @Override
  public void onEnable() {
    long startTime = System.currentTimeMillis();

    INSTANCE = this;

    // Initialize configs
    config = new Config();
    lang = new Lang();

    // Setup classes
    CommandManager.init();
    EventManager.init();
    Frame.init();
    Kit.init();

    // Check soft depends
    if (Bukkit.getPluginManager().getPlugin("AuroraCombat") != null) {
      if (getConfig().getBoolean("optional-plugins.auroracombat-compatibility")) {
        auroraCombatInstalled = true;
      }
    }

    getLogger().info("AuroraKits enabled in " + (System.currentTimeMillis() - startTime) + "ms");
  }

  @Override
  public void onDisable() {
    getLogger().info("AuroraKits disabled");
  }

  public void reloadConfig() {
    config.reload();
  }
}
