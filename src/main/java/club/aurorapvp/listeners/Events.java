package club.aurorapvp.listeners;

import club.aurorapvp.config.Config;
import club.aurorapvp.modules.Frame;
import club.aurorapvp.modules.GUI;
import club.aurorapvp.modules.Kit;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class Events implements Listener {
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    if (Config.get().getBoolean("kits.lastUsedKit.enabled")) {
      Kit.giveLastUsedKit(event.getPlayer());
    }
    Kit.onPlayerJoin(event.getPlayer());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Kit.onPlayerQuit(event.getPlayer());
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEntityEvent event) {
    Frame.onClicked(event);
  }

  @EventHandler
  public static void onInventoryClicked(InventoryClickEvent event) {
    GUI.inventoryClicked(event);
  }

  @EventHandler
  public void onPlayerRespawn(PlayerPostRespawnEvent event) {
    if (Config.get().getBoolean("kits.lastUsedKit.enabled")) {
      Kit.giveLastUsedKit(event.getPlayer());
    }
  }

  @EventHandler
  public void onWorldLoad(WorldLoadEvent event) {
    Frame.reloadAll();
  }
}