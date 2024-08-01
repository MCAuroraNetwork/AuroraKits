package club.aurorapvp.aurorakits.events.listeners;

import club.aurorapvp.auroracombat.events.custom.PlayerKilledByPlayerEvent;
import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.modules.Frame;
import club.aurorapvp.aurorakits.modules.kits.Kit;
import club.aurorapvp.aurorakits.modules.kits.KitEditor;
import club.aurorapvp.aurorakits.modules.kits.KitsGUI;
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
    KitEditor.register(event.getPlayer());

    if (AuroraKits.getInstance().getConfig().getBoolean("kits.give-last-kit.enabled")) {
      Kit.refillKit(event.getPlayer());
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Kit.unregister(event.getPlayer());
    KitsGUI.unregister(event.getPlayer());
    KitEditor.unregister(event.getPlayer());
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEntityEvent event) {
    Frame.onClicked(event);
  }

  @EventHandler
  public void onPlayerRespawn(PlayerPostRespawnEvent event) {
    if (!AuroraKits.getInstance().getConfig().getBoolean("kits.give-last-kit.enabled")) {
      return;
    }

    Kit.refillKit(event.getPlayer());
  }

  @EventHandler
  public void onPlayerKilled(PlayerKilledByPlayerEvent event) {
    if (!AuroraKits.getInstance().isAuroraCombatInstalled()) {
      return;
    }

    if (!AuroraKits.getInstance().getConfig().getBoolean("kits.give-last-kit.enabled")) {
      return;
    }

    Kit.refillKit(event.getKiller());
  }
}
