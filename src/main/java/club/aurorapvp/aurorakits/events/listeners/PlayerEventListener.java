package club.aurorapvp.aurorakits.events.listeners;

import club.aurorapvp.auroracombat.events.custom.PlayerKilledByPlayerEvent;
import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.events.custom.KitCreateEvent;
import club.aurorapvp.aurorakits.events.custom.KitDeleteEvent;
import club.aurorapvp.aurorakits.events.custom.KitGiveEvent;
import club.aurorapvp.aurorakits.events.custom.KitRefillEvent;
import club.aurorapvp.aurorakits.events.custom.KitSelectEvent;
import club.aurorapvp.aurorakits.modules.Frame;
import club.aurorapvp.aurorakits.modules.kits.Kit;
import club.aurorapvp.aurorakits.modules.kits.KitEditor;
import club.aurorapvp.aurorakits.modules.kits.KitsGUI;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import java.util.Objects;
import java.util.Set;
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

    if (AuroraKits.getInstance().getConfig().getBoolean("kits.give-last-kit.enabled")) {
      Kit.refillKit(event.getPlayer());
    }
  }

  @EventHandler
      (priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onKitGive(KitGiveEvent event) {
    event.getPlayer().getInventory().setStorageContents(event.getKit().getContents().clone());
    event.getPlayer().getInventory().setArmorContents(event.getKit().getArmor().clone());
    event.getPlayer().getInventory().setItemInOffHand(event.getKit().getOffhand().clone());

    Kit.setLastUsedKit(event.getPlayer(), event.getKit());

    event.getPlayer().sendMessage(AuroraKits.getInstance().getLang().getComponent("kit-used"));
  }

  @EventHandler
      (priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onKitRefill(KitRefillEvent event) {
    event.getPlayer().getInventory().setStorageContents(event.getKit().getContents().clone());
    event.getPlayer().getInventory().setArmorContents(event.getKit().getArmor().clone());
    event.getPlayer().getInventory().setItemInOffHand(event.getKit().getOffhand().clone());

    event.getPlayer().sendMessage(AuroraKits.getInstance().getLang().getComponent("kit-refilled"));
  }

  @EventHandler
      (priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onKitSelect(KitSelectEvent event) {
    event.getKit().giveKit(event.getPlayer());
  }

  @EventHandler
      (priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onKitCreate(KitCreateEvent event) {
    event.getKit().create();
  }


  @EventHandler
      (priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onKitDelete(KitDeleteEvent event) {
    Set<Kit> set = (event.getKit().isPublic() ? Kit.getPublicKits()
        : Kit.getPrivateKits(event.getPlayer()));

    set.remove(event.getKit());

    event.getKit().delete();

    if (event.getKit().isPublic()) {
      KitsGUI.reloadAll();
    } else {
      Objects.requireNonNull(KitsGUI.getGUI(event.getPlayer())).reload();
    }

    if (event.getPlayer() != null) {
      event.getPlayer().sendMessage(AuroraKits.getInstance().getLang().getComponent("kit-deleted"));
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
