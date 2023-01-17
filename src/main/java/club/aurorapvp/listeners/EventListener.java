package club.aurorapvp.listeners;

import club.aurorapvp.config.ConfigHandler;
import club.aurorapvp.datahandlers.ItemFrameDataHandler;
import club.aurorapvp.modules.FallDamageModule;
import club.aurorapvp.modules.GUIModule;
import club.aurorapvp.modules.ItemFramesModule;
import club.aurorapvp.modules.KitModule;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.io.IOException;

public class EventListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        FallDamageModule.checkFallDamage(event.getPlayer());
        if (ConfigHandler.get().getBoolean("kits.lastUsedKit.enabled")) {
            KitModule.giveLastUsedKit(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        FallDamageModule.checkFallDamage(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        ItemFramesModule.onClicked(event);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        GUIModule.onInventoryClicked(event);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerPostRespawnEvent event) {
        if (ConfigHandler.get().getBoolean("kits.lastUsedKit.enabled")) {
            KitModule.giveLastUsedKit(event.getPlayer());
        }
        FallDamageModule.checkFallDamage(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerFall(EntityDamageEvent event) {
        FallDamageModule.cancelFallDamage(event);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        try {
            ItemFrameDataHandler.reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}