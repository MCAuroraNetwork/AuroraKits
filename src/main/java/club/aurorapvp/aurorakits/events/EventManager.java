package club.aurorapvp.aurorakits.events;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.events.listeners.PlayerEventListener;
import club.aurorapvp.aurorakits.events.listeners.WorldEventListener;
import org.bukkit.Bukkit;

public class EventManager {
  public static void init() {
    Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), AuroraKits.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new WorldEventListener(), AuroraKits.INSTANCE);
  }
}
