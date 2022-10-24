package club.aurorapvp.listeners;

import static club.aurorapvp.datahandlers.GUIHandler.inv;
import static club.aurorapvp.datahandlers.ItemFrameDataHandler.checkLocation;
import static club.aurorapvp.listeners.CommandListener.p;

import club.aurorapvp.config.CustomConfigHandler;
import club.aurorapvp.datahandlers.ItemFrameDataHandler;
import java.util.ArrayList;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener extends YamlConfiguration implements Listener {
  public static ArrayList<Player> falldamage = new ArrayList<>();
  public static Location clickLoc;

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player p = event.getPlayer();
    if (!CustomConfigHandler.get().getBoolean("doFirstFallDamage")) {
      falldamage.add(p);
    }
  }

  @EventHandler
  public void hangFrame(HangingPlaceEvent event) {
    if (event.getEntity() instanceof ItemFrame) {
      if (p.getFacing() == BlockFace.WEST) {
        event.getEntity().teleport(
            event.getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.UP).getLocation());
        event.getEntity().setFacingDirection(BlockFace.WEST, true);
      } else if (p.getFacing() == BlockFace.NORTH) {
        event.getEntity().teleport(
            event.getBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP).getLocation());
        event.getEntity().setFacingDirection(BlockFace.SOUTH, true);
      } else if (p.getFacing() == BlockFace.SOUTH) {
        event.getEntity().teleport(
            event.getBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.UP).getLocation());
        event.getEntity().setFacingDirection(BlockFace.NORTH, true);
      }
    }
  }

  @EventHandler
  public void playerInteractEntityEvent(PlayerInteractEntityEvent event) {
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

    falldamage.add(p);
  }

  @EventHandler
  public void onPlayerFall(EntityDamageEvent event) {
    if (event.getEntity() instanceof Player p) {
      CustomConfigHandler.setup();
      if (event.getCause() == EntityDamageEvent.DamageCause.FALL && falldamage.contains(p) &&
          !CustomConfigHandler.get().getBoolean("doFirstFallDamage")) {
        event.setCancelled(true);
        falldamage.remove(p);
      }
    }
  }
}