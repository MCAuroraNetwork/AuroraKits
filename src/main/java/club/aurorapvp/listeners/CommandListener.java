package club.aurorapvp.listeners;

import static club.aurorapvp.AuroraKits.plugin;
import static club.aurorapvp.datahandlers.KitDataHandler.checkKitAmount;
import static club.aurorapvp.datahandlers.KitDataHandler.checkKits;
import static club.aurorapvp.datahandlers.KitDataHandler.get;

import club.aurorapvp.config.CustomConfigHandler;
import club.aurorapvp.datahandlers.GUIHandler;
import club.aurorapvp.datahandlers.ItemFrameDataHandler;
import club.aurorapvp.datahandlers.KitDataHandler;
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
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label,
                           String[] args) {

    if (sender instanceof Player) {
      p = (Player) sender;
    }
    if (args.length != 0) {
      commandArg0 = args[0];
    }

    if (command.getName().equals("aurorakits")) {
      reloadCmd();
    } else if (command.getName().equals("kit") && args.length != 0) {
      kitCmd();
    } else if (command.getName().equals("kits") ||
        command.getName().equals("kit") && args.length == 0) {
      GUIHandler.openGUI(p);
    } else if (command.getName().equals("createkit")) {
      createKitCmd();
    } else if (command.getName().equals("deletekit")) {
      deleteKitCmd();
    } else if (command.getName().equals("createpublickit")) {
      createPublicKitCmd();
    } else if (command.getName().equals("createframe")) {
      createFrameCmd();
    } else if (command.getName().equals("deleteframe")) {
      deleteFrameCmd();
    } else if (command.getName().equals("deletepublickit")) {
      deletePublicKitCmd();
    }

    return true;
  }

  public void kitCmd() {
    checkKits();
    if (KitDataHandler.get() != null) {
      inventoryData = p.getInventory().getContents();

      for (int i = 0; i < inventoryData.length; i++) {
        inventoryData[i] = KitDataHandler.get().getItemStack("items." + i);
      }

      p.getInventory().setContents(inventoryData);
      p.sendMessage(Component.text("Used kit " + commandArg0));
    } else {
      p.sendMessage(Component.text("Kit " + commandArg0 + " not found!"));
    }
  }

  public void createKitCmd() {
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
    p.sendMessage(Component.text("Kit " + commandArg0 + " sucessfully created"));
  }

  public void deleteKitCmd() {
    KitDataHandler.delete();
    GUIHandler.deleteGUIEntry();

    p.sendMessage(Component.text("Kit " + commandArg0 + " sucessfully deleted"));
  }

  public void createPublicKitCmd() {
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
    p.sendMessage(Component.text("Public kit " + commandArg0 + " sucessfully created"));
  }

  public void deletePublicKitCmd() {
    KitDataHandler.deletePublic();
    GUIHandler.deletePublicGUIEntry();

    p.sendMessage(Component.text("Kit " + commandArg0 + " sucessfully deleted"));
  }

  public void reloadCmd() {
    CustomConfigHandler.reload();
    plugin.getLogger().info("Configs reloaded");
  }

  public void createFrameCmd() {
    if (commandArg0 != null) {
      mainHandData = p.getInventory().getItemInMainHand();

      ItemFrameDataHandler.create();
    } else {
      p.sendMessage(Component.text("Invalid frame name"));
    }
    p.sendMessage(Component.text("Frame " + commandArg0 + " sucessfully created"));
  }

  public void deleteFrameCmd() {
    if (commandArg0 != null) {
      ItemFrameDataHandler.checkFile();

      ItemFrameDataHandler.get().set("frames." + commandArg0, null);
      ItemFrameDataHandler.save();
    } else {
      p.sendMessage(Component.text("Invalid frame name"));
    }
    p.sendMessage(Component.text("Frame " + commandArg0 + " sucessfully deleted"));
  }
}