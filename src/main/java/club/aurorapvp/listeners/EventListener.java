package club.aurorapvp.listeners;

import static club.aurorapvp.datahandlers.GUIHandler.inv;
import static club.aurorapvp.datahandlers.ItemFrameDataHandler.checkLocation;

import club.aurorapvp.config.CustomConfigHandler;
import club.aurorapvp.util.DataHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener extends YamlConfiguration implements Listener {

  public static Location clickLoc;

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {

    CustomConfigHandler.setup();
    Player p = event.getPlayer();

    p.sendMessage(Component.text((DataHandler.get().getString("message.joinMessage"))));

    if (!event.getPlayer().hasPlayedBefore()) {
      p.sendMessage(Component.text((DataHandler.get().getString("message.firstJoinMessage"))));
    }
  }

  @SuppressWarnings("checkstyle:MethodName")
  @EventHandler
  public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
    final Entity clicked = event.getRightClicked();
    if (clicked instanceof ItemFrame) {
      clickLoc = clicked.getLocation();
      checkLocation();

      if (checkLocation() != null) {
        CommandListener.p.getInventory()
            .addItem(DataHandler.get().getItemStack("frames." + checkLocation() + ".item"));
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onInventoryClick(final InventoryClickEvent event) {
    if (!event.getInventory().equals(inv)) {
      return;
    }

    event.setCancelled(true);

    final ItemStack clickedItem = event.getCurrentItem();

    if (clickedItem == null || clickedItem.getType().isAir()) {
      return;
    }

    final Player p = (Player) event.getWhoClicked();

    p.performCommand("kit " + PlainTextComponentSerializer.plainText()
        .serialize(event.getCurrentItem().getItemMeta().displayName()));
  }

  @EventHandler
  public void onInventoryClick(final InventoryDragEvent event) {
    if (event.getInventory().equals(inv)) {
      event.setCancelled(true);
    }
  }
}