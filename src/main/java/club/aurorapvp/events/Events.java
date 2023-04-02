package club.aurorapvp.events;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.events.listeners.InventoryEvents;
import club.aurorapvp.events.listeners.PlayerEvents;
import club.aurorapvp.events.listeners.WorldEvents;
import org.bukkit.Bukkit;

public class Events {
  public static void init() {
    Bukkit.getPluginManager().registerEvents(new InventoryEvents(), AuroraKits.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new PlayerEvents(), AuroraKits.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new WorldEvents(), AuroraKits.INSTANCE);
  }
}