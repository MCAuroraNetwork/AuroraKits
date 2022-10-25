package club.aurorapvp.listeners;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.datahandlers.GUIHandler.inv;
import static club.aurorapvp.datahandlers.ItemFrameDataHandler.checkLocation;

import club.aurorapvp.config.CustomConfigHandler;
import club.aurorapvp.datahandlers.ItemFrameDataHandler;
import java.io.File;
import java.util.ArrayList;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener extends YamlConfiguration implements Listener {
  public static ArrayList<Player> falldamage = new ArrayList<>();
  public static Location clickLoc;

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player p = event.getPlayer();
    if (!CustomConfigHandler.get().getBoolean("doFirstFallDamage") && !falldamage.contains(p)) {
      falldamage.add(p);
    }
    if (CustomConfigHandler.get().getBoolean("giveKitOnJoin.enabled")) {
      ItemStack[] inventoryData = p.getInventory().getContents();
      FileConfiguration kitFile = YamlConfiguration.loadConfiguration(new File(DataFolder,
          "/kits/public/" + CustomConfigHandler.get().getString("giveKitOnJoin.kit") + ".yml"));

      for (int i = 0; i < inventoryData.length; i++) {
        inventoryData[i] = kitFile.getItemStack("items." + i);
      }

      p.getInventory().setContents(inventoryData);
    }
  }

  @EventHandler
  public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
    Player p = event.getPlayer();
    if (!CustomConfigHandler.get().getBoolean("doFirstFallDamage") && !falldamage.contains(p)) {
      falldamage.add(p);
    }
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEntityEvent event) {
    final Entity clicked = event.getRightClicked();
    if (clicked instanceof ItemFrame) {
      clickLoc = clicked.getLocation();
      checkLocation();

      if (checkLocation() != null) {
        event.getPlayer().getInventory()
            .addItem(
                ItemFrameDataHandler.get().getItemStack("frames." + checkLocation() + ".item"));
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onInventoryClick(final InventoryClickEvent event) {
    if (event.getInventory().equals(inv)) {
      event.setCancelled(true);

      final ItemStack clickedItem = event.getCurrentItem();

      if (clickedItem == null || clickedItem.getType().isAir()) {
        return;
      }

      final Player p = (Player) event.getWhoClicked();

      p.performCommand("kit " + PlainTextComponentSerializer.plainText()
          .serialize(event.getCurrentItem().getItemMeta().displayName()));
    }
  }

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    Player p = event.getPlayer();

    falldamage.add(p);
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerFall(EntityDamageEvent event) {
    if (event.getEntity() instanceof Player p) {
      CustomConfigHandler.setup();
      if (event.getCause() == EntityDamageEvent.DamageCause.FALL && falldamage.contains(p) &&
          !CustomConfigHandler.get().getBoolean("doFirstFallDamage") && !event.isCancelled()) {
        event.setCancelled(true);
        falldamage.remove(p);
      }
    }
  }
}