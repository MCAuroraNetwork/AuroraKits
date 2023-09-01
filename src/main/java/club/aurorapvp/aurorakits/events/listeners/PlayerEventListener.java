package club.aurorapvp.aurorakits.events.listeners;

import static club.aurorapvp.aurorakits.modules.kits.Kit.LAST_USED_KIT;

import club.aurorapvp.auroracombat.events.custom.PlayerKilledByPlayerEvent;
import club.aurorapvp.aurorakits.config.Config;
import club.aurorapvp.aurorakits.config.Lang;
import club.aurorapvp.aurorakits.events.custom.KitGiveEvent;
import club.aurorapvp.aurorakits.modules.Frame;
import club.aurorapvp.aurorakits.modules.kits.Kit;
import club.aurorapvp.aurorakits.modules.kits.KitEditor;
import club.aurorapvp.aurorakits.modules.kits.KitsGUI;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventListener implements Listener {

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Kit.register(event.getPlayer());
    new KitEditor(event.getPlayer());
    new KitsGUI(event.getPlayer());

    if (Config.get().getBoolean("kits.give-last-kit.enabled")) {
      Kit kit = Kit.getLastUsedKit(event.getPlayer());

      if (kit != null) {
        kit.giveKit(event.getPlayer());
      }

      event.getPlayer().sendMessage(Lang.getComponent("kit-refilled"));
    }
  }

  @EventHandler
      (priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onKitGive(KitGiveEvent event) {
    event.getPlayer().getInventory().setContents(event.getKit().getItemStacks());

    LAST_USED_KIT.put(event.getPlayer(), event.getKit());
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
    if (!Config.get().getBoolean("kits.give-last-kit.enabled")) {
      return;
    }

    Kit kit = Kit.getLastUsedKit(event.getPlayer());

    if (kit != null) {
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
      kit.giveKit(event.getKiller());

      event.getKiller().sendMessage(Lang.getComponent("kit-refilled"));
    }
  }
}
