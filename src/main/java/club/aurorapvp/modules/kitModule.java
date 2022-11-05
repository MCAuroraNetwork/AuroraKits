package club.aurorapvp.modules;

import static club.aurorapvp.AuroraKits.DataFolder;
import static club.aurorapvp.AuroraKits.config;
import static club.aurorapvp.AuroraKits.plugin;
import static club.aurorapvp.config.LangHandler.getLangComponent;
import static club.aurorapvp.datahandlers.GUIDataHandler.createGUIData;
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

public class kitModule {
  public static void giveKitOnJoin(PlayerJoinEvent event) {
    Player p = event.getPlayer();

    new BukkitRunnable() {
      @Override
      public void run() {
        if (config.getBoolean("giveKitOnJoin.enabled")) {
          ItemStack[] inventoryData = p.getInventory().getContents();
          FileConfiguration kitFile = YamlConfiguration.loadConfiguration(new File(DataFolder,
              "/kits/public/" + config.getString("giveKitOnJoin.kit") + ".yml"));

          for (int i = 0; i < inventoryData.length; i++) {
            inventoryData[i] = kitFile.getItemStack("items." + i);
          }

          p.getInventory().setContents(inventoryData);
        }
      }
    }.runTaskLater(plugin, 5);
  }

  public static void createKit(CommandSender sender, String arg) {
    Player p = Bukkit.getPlayer(sender.getName());

    if (getKitAmount(p) <= 54 && arg != null &&
        p.getInventory().getItemInMainHand().getItemMeta() != null) {

      try {
        createKitData(p, arg, String.valueOf(p.getUniqueId()));
        createGUIData(p, arg);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      sender.sendMessage(getLangComponent("kit-created"));

    } else if (getKitAmount(p) >= 55) {
      sender.sendMessage(getLangComponent("kit-too-many"));
    } else if (arg == null && getKitAmount(p) <= 54 &&
        p.getInventory().getItemInMainHand().getItemMeta() != null) {

      try {
        createKitData(p, String.valueOf(getKitAmount(p) + 1), String.valueOf(p.getUniqueId()));
        createGUIData(p, String.valueOf(getKitAmount(p) + 1));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      sender.sendMessage(getLangComponent("kit-created"));
    } else if (p.getInventory().getItemInMainHand().getItemMeta() == null) {
      p.sendMessage(getLangComponent("kit-invalid-item"));
    }
  }

  public static void createPublicKit(CommandSender sender, String arg) {
    Player p = Bukkit.getPlayer(sender.getName());

    if (getKitAmount(p) <= 54 && arg != null &&
        p.getInventory().getItemInMainHand().getItemMeta() != null) {

      try {
        createKitData(p, arg, "public");
        createGUIData(p, arg);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      sender.sendMessage(getLangComponent("kit-created"));

    } else if (getKitAmount(p) >= 55) {
      sender.sendMessage(getLangComponent("kit-too-many"));
    } else if (arg == null && getKitAmount(p) <= 54 &&
        p.getInventory().getItemInMainHand().getItemMeta() != null) {

      try {
        createKitData(p, String.valueOf(getKitAmount(p) + 1), "public");
        createGUIData(p, String.valueOf(getKitAmount(p) + 1));
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