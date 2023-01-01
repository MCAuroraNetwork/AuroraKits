package club.aurorapvp.modules;

import club.aurorapvp.config.ConfigHandler;
import club.aurorapvp.config.LangHandler;
import club.aurorapvp.datahandlers.GUIDataHandler;
import club.aurorapvp.datahandlers.KitDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;

public class KitModule {
    private static final HashMap<String, String> LAST_USED_KIT = new HashMap<>();

    public static void giveLastUsedKit(Player p) {
        if (!LAST_USED_KIT.containsKey(p.getName())) {
            getKit(p, ConfigHandler.get().getString("kits.lastUsedKit.defaultKit"));
        } else {
            getKit(p, LAST_USED_KIT.get(p.getName()));
        }
    }

    public static void create(CommandSender sender, String kitName, String kitLocation) {
        Player p = Bukkit.getPlayer(sender.getName());

        if (KitDataHandler.getAmount(p.getUniqueId()) <= 54 && kitName != null &&
                p.getInventory().getItemInMainHand().getItemMeta() != null) {

            try {
                KitDataHandler.create(p, kitName, kitLocation);
                GUIDataHandler.createEntry(p, kitLocation, kitName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            sender.sendMessage(LangHandler.getComponent("kit-created"));

        } else if (KitDataHandler.getAmount(p.getUniqueId()) >= 55) {
            sender.sendMessage(LangHandler.getComponent("kit-too-many"));
        } else if (kitName == null && KitDataHandler.getAmount(p.getUniqueId()) <= 54 &&
                p.getInventory().getItemInMainHand().getItemMeta() != null) {

            try {
                KitDataHandler.create(p, "Kit" + Math.addExact(KitDataHandler.getAmount(p.getUniqueId()), 1), kitLocation);
                GUIDataHandler.createEntry(p, kitLocation, "Kit" + KitDataHandler.getAmount(p.getUniqueId()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage(LangHandler.getComponent("kit-created"));
        } else if (p.getInventory().getItemInMainHand().getItemMeta() == null) {
            p.sendMessage(LangHandler.getComponent("kit-invalid-item"));
        }
    }

    public static void getKit(Player p, String kitName) {
        YamlConfiguration KitFile = KitDataHandler.get(String.valueOf(p.getUniqueId()), kitName);
        ItemStack[] inventoryData = p.getInventory().getContents();

        if (KitFile != null) {

            for (int i = 0; i < inventoryData.length; i++) {
                inventoryData[i] = KitFile.getItemStack("items." + i);
            }

            p.getInventory().setContents(inventoryData);
            p.sendMessage(LangHandler.getComponent("kit-used"));
            LAST_USED_KIT.put(p.getName(), kitName);
        } else {
            p.sendMessage(LangHandler.getComponent("kit-not-found"));
        }
    }
}