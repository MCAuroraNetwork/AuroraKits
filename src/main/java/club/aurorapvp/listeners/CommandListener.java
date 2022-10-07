package club.aurorapvp.listeners;

import static club.aurorapvp.AuroraKits.plugin;
import static club.aurorapvp.datahandlers.KitDataHandler.checkKitAmount;
import static club.aurorapvp.datahandlers.KitDataHandler.checkKits;

import club.aurorapvp.config.CustomConfigHandler;
import club.aurorapvp.datahandlers.GUIHandler;
import club.aurorapvp.datahandlers.ItemFrameDataHandler;
import club.aurorapvp.datahandlers.KitDataHandler;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CommandListener implements CommandExecutor {

  public static Player p;
  public static String commandArg0;
  public static ItemStack[] inventoryData;
  public static ItemStack mainHandData;

  @Override
  public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label,
                           String[] args) {

    if (sender instanceof Player) {
      p = (Player) sender;
    }
    if (args.length != 0) {
      commandArg0 = args[0];
    }

    if (command.getName().equals("aurorakits") && Objects.equals(args[0], "reload")) {
      reloadcmd();
    } else if (command.getName().equals("kit") && p.hasPermission("AuroraKits.kit")) {
      kitcmd();
    } else if (command.getName().equals("kits") && p.hasPermission("AuroraKits.kit")) {
      GUIHandler.openGUI(p);
    } else if (command.getName().equals("createkit") && p.hasPermission("AuroraKits.createkit")) {
      createkitcmd();
    } else if (command.getName().equals("deletekit") && p.hasPermission("AuroraKits.deletekit")) {
      KitDataHandler.delete();
    } else if (command.getName().equals("createpublickit") &&
        p.hasPermission("AuroraKits.public")) {
      createpublickitcmd();
    } else if (command.getName().equals("createframe") && p.hasPermission("AuroraKits.frame")) {
      createframe();
    } else if (command.getName().equals("deleteframe") && p.hasPermission("AuroraKits.frame")) {
      deleteframe();
    } else if (command.getName().equals("deletepublickit") &&
        p.hasPermission("AuroraKits.public")) {
      KitDataHandler.deletepublic();
    }

    return true;
  }

  public void kitcmd() {
    if (commandArg0 != null) {
      checkKits();
      inventoryData = p.getInventory().getContents();

      for (int i = 0; i < inventoryData.length; i++) {
        inventoryData[i] = KitDataHandler.get().getItemStack("items." + i);
      }

      p.getInventory().setContents(inventoryData);
    } else {
      p.sendMessage(Component.text("Invalid kit name!"));
    }
  }

  public void createkitcmd() {
    checkKitAmount();
    if (checkKitAmount() && commandArg0 != null &&
        p.getInventory().getItemInMainHand().getItemMeta() != null) {
      inventoryData = p.getInventory().getContents();

      KitDataHandler.create();
    } else if (commandArg0 == null) {
      p.sendMessage(Component.text("Invalid kit name!"));
    } else if (p.getInventory().getItemInMainHand().getItemMeta() == null) {
      p.sendMessage(Component.text("Invalid Display Item!"));
    } else {
      p.sendMessage(Component.text("You have too many kits!"));
    }
  }

  public void createpublickitcmd() {
    checkKitAmount();
    if (checkKitAmount() && commandArg0 != null &&
        p.getInventory().getItemInMainHand().getItemMeta() != null) {
      inventoryData = p.getInventory().getContents();

      KitDataHandler.createPublic();
    } else if (commandArg0 == null) {
      p.sendMessage(Component.text("Invalid kit name!"));
    } else if (p.getInventory().getItemInMainHand().getItemMeta() == null) {
      p.sendMessage(Component.text("Invalid Display Item!"));
    } else {
      p.sendMessage(Component.text("You have too many kits!"));
    }
  }

  public void reloadcmd() {
    CustomConfigHandler.reload();
    plugin.getLogger().info("Configs reloaded");
  }

  public void createframe() {
    if (commandArg0 != null) {
      mainHandData = p.getInventory().getItemInMainHand();

      ItemFrameDataHandler.create();
    } else {
      p.sendMessage(Component.text("Invalid frame name"));
    }
  }

  public void deleteframe() {
    if (commandArg0 != null) {
      ItemFrameDataHandler.checkFile();

      ItemFrameDataHandler.get().set("frames." + commandArg0, null);
      ItemFrameDataHandler.save();
    } else {
      p.sendMessage(Component.text("Invalid frame name"));
    }
  }
}