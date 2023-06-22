package club.aurorapvp.aurorakits.events.listeners;

import club.aurorapvp.aurorakits.modules.KitsGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryEvents implements Listener {
  @EventHandler
  public static void onInventoryClicked(InventoryClickEvent event) {
    KitsGUI.inventoryClicked(event);
  }
}
