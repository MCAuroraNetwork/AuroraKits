package club.aurorapvp.events.listeners;

import club.aurorapvp.modules.Frame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class World implements Listener {
  @EventHandler
  public void onWorldLoad(WorldLoadEvent event) {
    Frame.reloadAll();
  }
}
