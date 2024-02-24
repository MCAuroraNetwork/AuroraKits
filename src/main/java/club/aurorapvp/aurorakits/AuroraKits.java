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
  private boolean auroraCombatInstalled;
  private Config config;
  private Lang lang;

  public static AuroraKits getInstance() {
    return INSTANCE;
  }
  public boolean isAuroraCombatInstalled() {
    return auroraCombatInstalled;
  }

  public Lang getLang() {
    return lang;
  }

  public @NotNull YamlConfiguration getConfig() {
    return config.getYaml();
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
      if (this.getConfig().getBoolean("optional-plugins.auroracombat-compatibility")) {
        auroraCombatInstalled = true;
      }
    }

    getLogger().info("AuroraKits enabled in " + (System.currentTimeMillis() - startTime) + "ms");
  }

  @Override
  public void onDisable() {
    long startTime = System.currentTimeMillis();

    getLogger().info("AuroraKits disabled in " + (System.currentTimeMillis() - startTime) + "ms");
  }

  public void reloadConfig() {
    config.reload();
  }
}
