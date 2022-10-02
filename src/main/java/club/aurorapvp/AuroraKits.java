package club.aurorapvp;

import club.aurorapvp.config.CustomConfigHandler;
import club.aurorapvp.filehandlers.ItemFrameDataHandler;
import club.aurorapvp.filehandlers.KitDataHandler;
import club.aurorapvp.listeners.CommandListener;
import club.aurorapvp.listeners.EventListener;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuroraKits extends JavaPlugin {

  public static Plugin plugin;
  public static File DataFolder;
  @Override
  public void onEnable() {

    // Register Listeners
    getServer().getPluginManager().registerEvents(new EventListener(), this);
    getCommand("kit").setExecutor(new CommandListener());
    getCommand("kits").setExecutor(new CommandListener());
    getCommand("createkit").setExecutor(new CommandListener());
    getCommand("createpublickit").setExecutor(new CommandListener());
    getCommand("deletekit").setExecutor(new CommandListener());
    getCommand("createframe").setExecutor(new CommandListener());
    getCommand("deleteframe").setExecutor(new CommandListener());
    getCommand("createpublickit");



    // Config setup
    getConfig().options().copyDefaults();
    saveDefaultConfig();
    CustomConfigHandler.setup();
    CustomConfigHandler.get().options().copyDefaults(true);
    CustomConfigHandler.save();

    // Setup storage dirs
    plugin = Bukkit.getPluginManager().getPlugin("AuroraKits");
    DataFolder = Bukkit.getServer().getPluginManager().getPlugin("AuroraKits").getDataFolder();
    KitDataHandler.setup();
    ItemFrameDataHandler.setup();

    getLogger().info("Aurora Kits Loaded");
  }
  @Override
  public void onDisable() {
    getLogger().info("Aurora Kits Unloaded");
  }
}

