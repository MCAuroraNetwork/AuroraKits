package club.aurorapvp.aurorakits.events.listeners;

import club.aurorapvp.aurorakits.modules.Frame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldEvents implements Listener {
  @EventHandler
  public void onWorldLoad(WorldLoadEvent event) {
    Frame.reloadAll();
  }
}
