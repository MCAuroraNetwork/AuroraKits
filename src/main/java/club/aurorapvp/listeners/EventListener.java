package club.aurorapvp.listeners;

import static club.aurorapvp.config.ConfigHandler.getConfigFile;
import static club.aurorapvp.datahandlers.ItemFrameDataHandler.reloadFrameData;
import static club.aurorapvp.modules.FallDamageModule.cancelFallDamage;
import static club.aurorapvp.modules.FallDamageModule.checkFallDamage;
import static club.aurorapvp.modules.GUIModule.onGUIInventoryClicked;
import static club.aurorapvp.modules.ItemFramesModule.onFrameClicked;
import static club.aurorapvp.modules.KitModule.giveLastUsedKit;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class EventListener extends YamlConfiguration implements Listener {
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    checkFallDamage(event.getPlayer());
    if (getConfigFile().getBoolean("kits.lastUsedKit.enabled")) {
      giveLastUsedKit(event.getPlayer());
    }
  }

  @EventHandler
  public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
    checkFallDamage(event.getPlayer());
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEntityEvent event) {
    onFrameClicked(event);
  }

  @EventHandler
  public void onInventoryClick(final InventoryClickEvent event) {
    onGUIInventoryClicked(event);
  }

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    if (getConfigFile().getBoolean("kits.lastUsedKit.enabled")) {
      giveLastUsedKit(event.getPlayer());
    }
    checkFallDamage(event.getPlayer());
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerFall(EntityDamageEvent event) {
    cancelFallDamage(event);
  }

  @EventHandler
  public void onWorldLoad(WorldLoadEvent event) {
    reloadFrameData();
  }
}