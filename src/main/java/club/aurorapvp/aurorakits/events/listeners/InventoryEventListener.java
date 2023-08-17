package club.aurorapvp.aurorakits.events.listeners;

import club.aurorapvp.aurorakits.modules.kits.KitsGui;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryEventListener implements Listener {
  @EventHandler
  public void onInventoryClicked(InventoryClickEvent event) {
    KitsGui.inventoryClicked(event);
  }
}
