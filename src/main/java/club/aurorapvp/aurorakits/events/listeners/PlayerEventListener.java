package club.aurorapvp.aurorakits.events.listeners;

import club.aurorapvp.auroracombat.events.custom.PlayerKilledByPlayerEvent;
import club.aurorapvp.aurorakits.config.Config;
import club.aurorapvp.aurorakits.modules.Frame;
import club.aurorapvp.aurorakits.modules.kits.Kit;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventListener implements Listener {
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Kit.register(event.getPlayer());

    if (Config.get().getBoolean("kits.give-last-kit.enabled")) {
      Kit kit = Kit.getLastUsedKit(event.getPlayer());

      if (kit != null) {
        kit.giveKit(event.getPlayer());
      }
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Kit.unregister(event.getPlayer());
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEntityEvent event) {
    Frame.onClicked(event);
  }

  @EventHandler
  public void onPlayerRespawn(PlayerPostRespawnEvent event) {
    if (Config.get().getBoolean("kits.give-last-kit.enabled")) {
      Kit kit = Kit.getLastUsedKit(event.getPlayer());

      if (kit != null) {
        kit.giveKit(event.getPlayer());
      }
    }
  }

  @EventHandler
  public void onPlayerKilled(PlayerKilledByPlayerEvent event) {
    if (Config.get().getBoolean("kits.give-last-kit.enabled")) {
      Kit kit = Kit.getLastUsedKit(event.getKiller());

      if (kit != null) {
        kit.giveKit(event.getKiller());
      }
    }
  }
}
