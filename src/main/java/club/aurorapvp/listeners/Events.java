package club.aurorapvp.listeners;

import club.aurorapvp.config.Config;
import club.aurorapvp.datahandlers.ItemFrameData;
import club.aurorapvp.modules.FallDamage;
import club.aurorapvp.modules.GUI;
import club.aurorapvp.modules.ItemFrames;
import club.aurorapvp.modules.Kits;
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

public class Events implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Config.get().getBoolean("kits.lastUsedKit.enabled")) {
            Kits.giveLastUsedKit(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        ItemFrames.onClicked(event);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        GUI.onInventoryClicked(event);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerPostRespawnEvent event) {
        if (Config.get().getBoolean("kits.lastUsedKit.enabled")) {
            Kits.giveLastUsedKit(event.getPlayer());
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        try {
            ItemFrameData.reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}