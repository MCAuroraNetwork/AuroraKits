package club.aurorapvp.aurorakits.events.listeners;

import club.aurorapvp.aurorakits.modules.Frame;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldEventListener implements Listener {

  @EventHandler
  public void onWorldLoad(WorldLoadEvent event) {
    Frame.reloadAll();
  }

  @EventHandler
  public void onEntityLoad(EntityAddToWorldEvent event) {
    if (event.getEntity() instanceof ItemFrame itemFrame) {
      new Frame(itemFrame);
    }
  }

  @EventHandler
  public void onEntityUnload(EntityRemoveFromWorldEvent event) {
    if (event.getEntity() instanceof ItemFrame itemFrame) {
      Frame.remove(itemFrame);
    }
  }
}
