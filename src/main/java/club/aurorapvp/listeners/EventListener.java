package club.aurorapvp.listeners;

import static club.aurorapvp.config.ConfigHandler.getConfigFile;
import static club.aurorapvp.modules.GUIModule.onGUIInventoryClicked;
import static club.aurorapvp.modules.fallDamageModule.cancelFallDamage;
import static club.aurorapvp.modules.fallDamageModule.falldamage;
import static club.aurorapvp.modules.itemFramesModule.onFrameClicked;
import static club.aurorapvp.modules.kitModule.giveKitOnJoin;

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

public class EventListener extends YamlConfiguration implements Listener {
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    if (!getConfigFile().getBoolean("doFirstFallDamage") && !falldamage.contains(event.getPlayer())) {
      falldamage.add(event.getPlayer());
    }
    giveKitOnJoin(event);
  }

  @EventHandler
  public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
    if (!getConfigFile().getBoolean("doFirstFallDamage") && !falldamage.contains(event.getPlayer())) {
      falldamage.add(event.getPlayer());
    }
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
    if (!getConfigFile().getBoolean("doFirstFallDamage") && !falldamage.contains(event.getPlayer())) {
      falldamage.add(event.getPlayer());
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerFall(EntityDamageEvent event) {
    cancelFallDamage(event);
  }
}