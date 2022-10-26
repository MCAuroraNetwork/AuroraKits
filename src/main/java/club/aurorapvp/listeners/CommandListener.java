package club.aurorapvp.listeners;

import static club.aurorapvp.AuroraKits.plugin;
import static club.aurorapvp.AuroraKits.prefix;
import static club.aurorapvp.datahandlers.KitDataHandler.checkKitAmount;
import static club.aurorapvp.datahandlers.KitDataHandler.checkKits;

import club.aurorapvp.config.CustomConfigHandler;
import club.aurorapvp.datahandlers.GUIHandler;
import club.aurorapvp.datahandlers.ItemFrameDataHandler;
import club.aurorapvp.datahandlers.KitDataHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
                           @NotNull String label, String[] args) {

    if (sender instanceof Player) {
      p = (Player) sender;
    }
    commandArg0 = null;
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
      p.sendMessage(MiniMessage.miniMessage().deserialize(
          prefix + "<gradient:#FFAA00:#FF55FF>Used kit <bold>" + commandArg0));
    } else {
      p.sendMessage(MiniMessage.miniMessage().deserialize(
          prefix + "<gradient:#FFAA00:#FF55FF>Kit <bold>" + commandArg0 + "</bold> not found!"));
    }
  }

  public void createKitCmd() {
    if (checkKitAmount() <= 54 && commandArg0 != null &&
        p.getInventory().getItemInMainHand().getItemMeta() != null) {
      inventoryData = p.getInventory().getContents();

      KitDataHandler.create();

      p.sendMessage(MiniMessage.miniMessage().deserialize(
          prefix + "<gradient:#FFAA00:#FF55FF>Kit <bold>" + commandArg0 +
              "</bold> successfully created! Use /kit " + commandArg0 +
              "to use it!"));
    } else if (commandArg0 == null && checkKitAmount() >= 55) {
      p.sendMessage(MiniMessage.miniMessage()
          .deserialize(prefix + "<gradient:#FFAA00:#FF55FF>Invalid kit name!"));
    } else if (commandArg0 == null && checkKitAmount() <= 54 &&
        p.getInventory().getItemInMainHand().getItemMeta() != null) {
      commandArg0 = "Kit" + Math.addExact(checkKitAmount(), 1);
      inventoryData = p.getInventory().getContents();

      KitDataHandler.create();
      p.sendMessage(MiniMessage.miniMessage().deserialize(
          prefix + "<gradient:#FFAA00:#FF55FF>Kit <bold>" + commandArg0 +
              "</bold> successfully created! Use /kit " + commandArg0 +
              " to use it!"));
    } else if (p.getInventory().getItemInMainHand().getItemMeta() == null) {
      p.sendMessage(MiniMessage.miniMessage()
          .deserialize(prefix + "<gradient:#FFAA00:#FF55FF>Invalid Display Item!"));
    } else {
      p.sendMessage(MiniMessage.miniMessage()
          .deserialize(prefix + "<gradient:#FFAA00:#FF55FF>You have too many kits!"));
    }
  }

  public void deleteKitCmd() {
    KitDataHandler.delete();
    GUIHandler.deleteGUIEntry();

    p.sendMessage(MiniMessage.miniMessage().deserialize(
        prefix + "<gradient:#FFAA00:#FF55FF>Kit <bold>" + commandArg0 +
            "</bold> successfully deleted"));
  }

  public void createPublicKitCmd() {
    checkKitAmount();
    if (checkKitAmount() <= 54 && commandArg0 != null &&
        p.getInventory().getItemInMainHand().getItemMeta() != null) {
      inventoryData = p.getInventory().getContents();

      KitDataHandler.createPublic();

      p.sendMessage(MiniMessage.miniMessage().deserialize(
          prefix + "<gradient:#FFAA00:#FF55FF>Public Kit <bold>" + commandArg0 +
              "</bold> successfully created! Use /kit " + commandArg0 +
              " to use it!"));
    } else if (commandArg0 == null && checkKitAmount() >= 55) {
      p.sendMessage(MiniMessage.miniMessage()
          .deserialize(prefix + "<gradient:#FFAA00:#FF55FF>Invalid kit name!"));
    } else if (commandArg0 == null && checkKitAmount() <= 54 &&
        p.getInventory().getItemInMainHand().getItemMeta() != null) {
      commandArg0 = "Kit" + Math.addExact(checkKitAmount(), 1);
      inventoryData = p.getInventory().getContents();

      KitDataHandler.createPublic();

      p.sendMessage(MiniMessage.miniMessage().deserialize(
          prefix + "<gradient:#FFAA00:#FF55FF>Public Kit <bold>" + commandArg0 +
              "</bold> successfully created! Use /kit " + commandArg0 +
              " to use it!"));
    } else if (p.getInventory().getItemInMainHand().getItemMeta() == null) {
      p.sendMessage(MiniMessage.miniMessage()
          .deserialize(prefix + "<gradient:#FFAA00:#FF55FF>Invalid Display Item!"));
    } else {
      p.sendMessage(MiniMessage.miniMessage()
          .deserialize(prefix + "<gradient:#FFAA00:#FF55FF>You have too many kits!"));
    }
  }

  public void deletePublicKitCmd() {
    if (commandArg0 != null) {
      KitDataHandler.deletePublic();
      GUIHandler.deletePublicGUIEntry();

      p.sendMessage(MiniMessage.miniMessage().deserialize(
          prefix + "<gradient:#FFAA00:#FF55FF>Public Kit <bold>" + commandArg0 +
              "</bold> successfully deleted"));
    } else {
      p.sendMessage(MiniMessage.miniMessage().deserialize(
          prefix + "<gradient:#FFAA00:#FF55FF>Invalid kit name!"));
    }
  }

  public void reloadCmd() {
    CustomConfigHandler.reload();
    plugin.getLogger().info("Configs reloaded");
  }

  public void createFrameCmd() {
    if (commandArg0 != null) {
      mainHandData = p.getInventory().getItemInMainHand();

      ItemFrameDataHandler.create();

      p.sendMessage(MiniMessage.miniMessage().deserialize(
          prefix + "<gradient:#FFAA00:#FF55FF>Frame <bold>" + commandArg0 +
              "</bold> successfully created"));
    } else {
      p.sendMessage(MiniMessage.miniMessage()
          .deserialize(prefix + "<gradient:#FFAA00:#FF55FF>Invalid frame name!"));
    }
  }

  public void deleteFrameCmd() {
    if (commandArg0 != null) {
      ItemFrameDataHandler.checkFile();

      ItemFrameDataHandler.get().set("frames." + commandArg0, null);
      ItemFrameDataHandler.save();

      p.sendMessage(MiniMessage.miniMessage().deserialize(
          prefix + "<gradient:#FFAA00:#FF55FF>Frame <bold>" + commandArg0 +
              "</bold> successfully deleted"));
    } else {
      p.sendMessage(MiniMessage.miniMessage()
          .deserialize(prefix + "<gradient:#FFAA00:#FF55FF>Invalid frame name!"));
    }
  }
}