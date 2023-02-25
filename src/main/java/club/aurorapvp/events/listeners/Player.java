package club.aurorapvp.events.listeners;

import club.aurorapvp.configs.Config;
import club.aurorapvp.modules.Frame;
import club.aurorapvp.modules.Kit;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Player implements Listener {
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Kit.setupPlayer(event.getPlayer());
    if (Config.get().getBoolean("kits.lastUsedKit.enabled")) {
      Kit.giveLastUsedKit(event.getPlayer());
    }
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
  public void onPlayerRespawn(PlayerPostRespawnEvent event) {
    if (Config.get().getBoolean("kits.lastUsedKit.enabled")) {
      Kit.giveLastUsedKit(event.getPlayer());
    }
  }
}
