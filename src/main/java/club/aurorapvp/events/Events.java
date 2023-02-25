package club.aurorapvp.events;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.events.listeners.Inventory;
import club.aurorapvp.events.listeners.Player;
import club.aurorapvp.events.listeners.World;
import org.bukkit.Bukkit;

public class Events {
  public Events() {
    Bukkit.getPluginManager().registerEvents(new Inventory(), AuroraKits.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new Player(), AuroraKits.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new World(), AuroraKits.INSTANCE);
  }
}