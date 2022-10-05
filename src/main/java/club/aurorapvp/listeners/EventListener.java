package club.aurorapvp.listeners;

import static club.aurorapvp.datahandlers.GUIHandler.inv;
import static club.aurorapvp.datahandlers.ItemFrameDataHandler.checkLocation;

import club.aurorapvp.config.CustomConfigHandler;
import club.aurorapvp.datahandlers.ItemFrameDataHandler;
import java.util.ArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener extends YamlConfiguration implements Listener {
  private static ArrayList falldamage;
  public static Location clickLoc;

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {

    CustomConfigHandler.setup();
    Player p = event.getPlayer();

    p.sendMessage(Component.text((CustomConfigHandler.get().getString("message.joinMessage"))));

    if (!event.getPlayer().hasPlayedBefore()) {
      p.sendMessage(
          Component.text((CustomConfigHandler.get().getString("message.firstJoinMessage"))));
    }
    ArrayList<Player> falldamage = new ArrayList<Player>();

    falldamage.add(p);
  }

  @EventHandler
  public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
    final Entity clicked = event.getRightClicked();
    if (clicked instanceof ItemFrame) {
      clickLoc = clicked.getLocation();
      checkLocation();

      if (checkLocation() != null) {
        event.getPlayer().getInventory()
            .addItem(
                ItemFrameDataHandler.get().getItemStack("frames." + checkLocation() + ".item"));
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

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    Player p = event.getPlayer();

    if (!falldamage.contains(p)) {
      falldamage.add(p);
    }
  }

  @EventHandler
  public void onPlayerFall(EntityDamageEvent event) {
    CustomConfigHandler.setup();
    Player player = (Player) event.getEntity();

    if (event.getCause() == EntityDamageEvent.DamageCause.FALL && !falldamage.contains(player) &&
        CustomConfigHandler.get().getBoolean("doFirstFallDamage")) {
      event.setCancelled(true);
      falldamage.remove(player);
    }
    return;
  }
}