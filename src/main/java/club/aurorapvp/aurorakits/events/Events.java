package club.aurorapvp.aurorakits.events;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.events.listeners.InventoryEvents;
import club.aurorapvp.aurorakits.events.listeners.PlayerEvents;
import club.aurorapvp.aurorakits.events.listeners.WorldEvents;
import org.bukkit.Bukkit;

public class Events {
  public static void init() {
    Bukkit.getPluginManager().registerEvents(new InventoryEvents(), AuroraKits.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new PlayerEvents(), AuroraKits.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new WorldEvents(), AuroraKits.INSTANCE);
  }
}