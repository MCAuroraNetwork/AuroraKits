package club.aurorapvp.aurorakits.events.listeners;

import club.aurorapvp.aurorakits.modules.KitEditor;
import club.aurorapvp.aurorakits.modules.KitsGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryEvents implements Listener {
  @EventHandler
  public void onInventoryClicked(InventoryClickEvent event) {
    KitsGUI.inventoryClicked(event);
    KitEditor.inventoryClicked(event);
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    KitEditor.inventoryClosed(event);
  }
}
