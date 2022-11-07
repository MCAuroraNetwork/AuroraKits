package club.aurorapvp.modules;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.plugin;
import static club.aurorapvp.config.ConfigHandler.getConfigFile;
import static club.aurorapvp.config.LangHandler.getLangComponent;
import static club.aurorapvp.datahandlers.GUIDataHandler.createGUIEntry;
import static club.aurorapvp.datahandlers.KitDataHandler.createKitData;
import static club.aurorapvp.datahandlers.KitDataHandler.getKitAmount;
import static club.aurorapvp.datahandlers.KitDataHandler.getKitFile;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class KitModule {
  public static void giveKitOnJoin(PlayerJoinEvent event) {
    Player p = event.getPlayer();

    new BukkitRunnable() {
      @Override
      public void run() {
        if (getConfigFile().getBoolean("giveKitOnJoin.enabled")) {
          getKit(event.getPlayer(), getConfigFile().getString("giveKitOnJoin.kit"));
        }
      }
    }.runTaskLater(plugin, 5);
  }

  public static void createKit(CommandSender sender, String arg, String dir) {
    Player p = Bukkit.getPlayer(sender.getName());

    if (getKitAmount(p.getUniqueId()) <= 54 && arg != null &&
        p.getInventory().getItemInMainHand().getItemMeta() != null) {

      try {
        createKitData(p, arg, dir);
        createGUIEntry(p, dir, arg);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      sender.sendMessage(getLangComponent("kit-created"));

    } else if (getKitAmount(p.getUniqueId()) >= 55) {
      sender.sendMessage(getLangComponent("kit-too-many"));
    } else if (arg == null && getKitAmount(p.getUniqueId()) <= 54 &&
        p.getInventory().getItemInMainHand().getItemMeta() != null) {

      try {
        createKitData(p, "Kit" + Math.addExact(getKitAmount(p.getUniqueId()), 1), dir);
        createGUIEntry(p, dir, "Kit" + getKitAmount(p.getUniqueId()));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      sender.sendMessage(getLangComponent("kit-created"));
    } else if (p.getInventory().getItemInMainHand().getItemMeta() == null) {
      p.sendMessage(getLangComponent("kit-invalid-item"));
    }
  }

  public static void getKit(Player p, String arg) {
    YamlConfiguration KitFile = getKitFile(String.valueOf(p.getUniqueId()), arg);
    ItemStack[] inventoryData = p.getInventory().getContents();

    if (KitFile != null) {

      for (int i = 0; i < inventoryData.length; i++) {
        inventoryData[i] = KitFile.getItemStack("items." + i);
      }

      p.getInventory().setContents(inventoryData);
      p.sendMessage(getLangComponent("kit-used"));
    } else {
      p.sendMessage(getLangComponent("kit-not-found"));
    }
  }
}