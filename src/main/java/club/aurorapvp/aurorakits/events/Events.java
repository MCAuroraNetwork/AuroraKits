package club.aurorapvp.aurorakits.events;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.events.listeners.InventoryEventListener;
import club.aurorapvp.aurorakits.events.listeners.PlayerEventListener;
import club.aurorapvp.aurorakits.events.listeners.WorldEventListener;
import org.bukkit.Bukkit;

public class Events {
  public static void init() {
    Bukkit.getPluginManager().registerEvents(new InventoryEventListener(), AuroraKits.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), AuroraKits.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new WorldEventListener(), AuroraKits.INSTANCE);
  }
}