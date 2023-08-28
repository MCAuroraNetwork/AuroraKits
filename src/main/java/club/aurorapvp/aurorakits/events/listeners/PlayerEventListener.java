package club.aurorapvp.aurorakits.events.listeners;

import club.aurorapvp.auroracombat.events.custom.PlayerKilledByPlayerEvent;
import club.aurorapvp.aurorakits.config.Config;
import club.aurorapvp.aurorakits.config.Lang;
import club.aurorapvp.aurorakits.events.custom.KitGiveEvent;
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
        KitGiveEvent kitGiveEvent = new KitGiveEvent(kit, event.getPlayer());

        kitGiveEvent.callEvent();

        if (kitGiveEvent.isCancelled()) {
          return;
        }

        kit.giveKit(event.getPlayer());
      }

      event.getPlayer().sendMessage(Lang.getComponent("kit-refilled"));
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
    if (!Config.get().getBoolean("kits.give-last-kit.enabled")) {
      return;
    }

    Kit kit = Kit.getLastUsedKit(event.getPlayer());

    if (kit != null) {
      KitGiveEvent kitGiveEvent = new KitGiveEvent(kit, event.getPlayer());

      kitGiveEvent.callEvent();

      if (kitGiveEvent.isCancelled()) {
        return;
      }

      kit.giveKit(event.getPlayer());

      event.getPlayer().sendMessage(Lang.getComponent("kit-refilled"));
    }
  }

  @EventHandler
  public void onPlayerKilled(PlayerKilledByPlayerEvent event) {
    if (!Config.get().getBoolean("kits.give-last-kit.enabled")) {
      return;
    }

    Kit kit = Kit.getLastUsedKit(event.getKiller());

    if (kit != null) {
      KitGiveEvent kitGiveEvent = new KitGiveEvent(kit, event.getKiller());

      kitGiveEvent.callEvent();

      if (kitGiveEvent.isCancelled()) {
        return;
      }

      kit.giveKit(event.getKiller());

      event.getKiller().sendMessage(Lang.getComponent("kit-refilled"));
    }
  }
}
