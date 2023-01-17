package club.aurorapvp.modules;

import club.aurorapvp.config.Config;
import club.aurorapvp.config.Lang;
import club.aurorapvp.datahandlers.GUIData;
import club.aurorapvp.datahandlers.KitData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;

public class Kits {
    private static final HashMap<String, String> LAST_USED_KIT = new HashMap<>();

    public static void giveLastUsedKit(Player p) {
        if (!LAST_USED_KIT.containsKey(p.getName())) {
            getKit(p, Config.get().getString("kits.lastUsedKit.defaultKit"));
        } else {
            getKit(p, LAST_USED_KIT.get(p.getName()));
        }
    }

    public static void create(CommandSender sender, String kitName, String kitLocation) {
        Player p = Bukkit.getPlayer(sender.getName());

        if (KitData.getAmount(p.getUniqueId()) <= 54 && kitName != null &&
                p.getInventory().getItemInMainHand().getItemMeta() != null) {

            try {
                KitData.create(p, kitName, kitLocation);
                GUIData.createEntry(p, kitLocation, kitName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            sender.sendMessage(Lang.getComponent("kit-created"));

        } else if (KitData.getAmount(p.getUniqueId()) >= 55) {
            sender.sendMessage(Lang.getComponent("kit-too-many"));
        } else if (kitName == null && KitData.getAmount(p.getUniqueId()) <= 54 &&
                p.getInventory().getItemInMainHand().getItemMeta() != null) {

            try {
                KitData.create(p, "Kit" + Math.addExact(KitData.getAmount(p.getUniqueId()), 1), kitLocation);
                GUIData.createEntry(p, kitLocation, "Kit" + KitData.getAmount(p.getUniqueId()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage(Lang.getComponent("kit-created"));
        } else if (p.getInventory().getItemInMainHand().getItemMeta() == null) {
            p.sendMessage(Lang.getComponent("kit-invalid-item"));
        }
    }

    public static void getKit(Player p, String kitName) {
        YamlConfiguration KitFile = KitData.get(String.valueOf(p.getUniqueId()), kitName);
        ItemStack[] inventoryData = p.getInventory().getContents();

        if (KitFile != null) {

            for (int i = 0; i < inventoryData.length; i++) {
                inventoryData[i] = KitFile.getItemStack("items." + i);
            }

            p.getInventory().setContents(inventoryData);
            p.sendMessage(Lang.getComponent("kit-used"));
            LAST_USED_KIT.put(p.getName(), kitName);
        } else {
            p.sendMessage(Lang.getComponent("kit-not-found"));
        }
    }
}