package club.aurorapvp.modules;

import static club.aurorapvp.config.ConfigHandler.getConfigFile;
import static club.aurorapvp.config.LangHandler.getLangComponent;
import static club.aurorapvp.datahandlers.GUIDataHandler.createGUIEntry;
import static club.aurorapvp.datahandlers.KitDataHandler.createKitData;
import static club.aurorapvp.datahandlers.KitDataHandler.getKitAmount;
import static club.aurorapvp.datahandlers.KitDataHandler.getKitFile;

import java.io.IOException;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitModule {
  private static HashMap<String, String> lastUsedKit = new HashMap<>();

  public static void giveLastUsedKit(Player p) {
    if (!lastUsedKit.containsKey(p.getName())) {
      getKit(p, getConfigFile().getString("kits.lastUsedKit.defaultKit"));
    } else {
      getKit(p, lastUsedKit.get(p.getName()));
    }
  }

  public static void createKit(CommandSender sender, String kitName, String kitLocation) {
    Player p = Bukkit.getPlayer(sender.getName());

    if (getKitAmount(p.getUniqueId()) <= 54 && kitName != null &&
        p.getInventory().getItemInMainHand().getItemMeta() != null) {

      try {
        createKitData(p, kitName, kitLocation);
        createGUIEntry(p, kitLocation, kitName);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      sender.sendMessage(getLangComponent("kit-created"));

    } else if (getKitAmount(p.getUniqueId()) >= 55) {
      sender.sendMessage(getLangComponent("kit-too-many"));
    } else if (kitName == null && getKitAmount(p.getUniqueId()) <= 54 &&
        p.getInventory().getItemInMainHand().getItemMeta() != null) {

      try {
        createKitData(p, "Kit" + Math.addExact(getKitAmount(p.getUniqueId()), 1), kitLocation);
        createGUIEntry(p, kitLocation, "Kit" + getKitAmount(p.getUniqueId()));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      sender.sendMessage(getLangComponent("kit-created"));
    } else if (p.getInventory().getItemInMainHand().getItemMeta() == null) {
      p.sendMessage(getLangComponent("kit-invalid-item"));
    }
  }

  public static void getKit(Player p, String kitName) {
    YamlConfiguration KitFile = getKitFile(String.valueOf(p.getUniqueId()), kitName);
    ItemStack[] inventoryData = p.getInventory().getContents();

    if (KitFile != null) {

      for (int i = 0; i < inventoryData.length; i++) {
        inventoryData[i] = KitFile.getItemStack("items." + i);
      }

      p.getInventory().setContents(inventoryData);
      p.sendMessage(getLangComponent("kit-used"));
      lastUsedKit.put(p.getName(), kitName);
    } else {
      p.sendMessage(getLangComponent("kit-not-found"));
    }
  }
}