package club.aurorapvp.listeners;

import static club.aurorapvp.config.ConfigHandler.reloadConfig;
import static club.aurorapvp.config.LangHandler.reloadLang;
import static club.aurorapvp.datahandlers.ItemFrameDataHandler.deleteFrameData;
import static club.aurorapvp.datahandlers.ItemFrameDataHandler.reloadFrameData;
import static club.aurorapvp.datahandlers.KitDataHandler.deleteKitData;
import static club.aurorapvp.modules.GUIModule.openGUI;
import static club.aurorapvp.modules.ItemFramesModule.createFrame;
import static club.aurorapvp.modules.KitModule.createKit;
import static club.aurorapvp.modules.KitModule.getKit;

import java.io.IOException;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandListener implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, String[] args) {

    Player p = Bukkit.getPlayer(sender.getName());

    switch (command.getName()) {
      case "aurorakits" -> {
        if (Objects.equals(args[0], "reload")) {
          try {
            reloadFrameData();
            reloadConfig();
            reloadLang();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      }
      case "kit" -> {
        if (args.length != 0) {
          getKit(Bukkit.getPlayer(sender.getName()), args[0]);
        } else {
          openGUI(p);
        }
      }
      case "kits" -> openGUI(p);
      case "createkit" -> {
        if (args.length == 0) {
          createKit(sender, null, String.valueOf(Bukkit.getPlayer(sender.getName()).getUniqueId()));
        } else {
          createKit(sender, args[0],
              String.valueOf(Bukkit.getPlayer(sender.getName()).getUniqueId()));
        }
      }
      case "deletekit" -> deleteKitData(sender, args[0],
          String.valueOf(Bukkit.getPlayer(sender.getName()).getUniqueId()));
      case "createpublickit" -> createKit(sender, args[0], "public");
      case "deletepublickit" -> deleteKitData(sender, args[0], "public");
      case "createframe" -> createFrame(sender, args[0]);
      case "deleteframe" -> deleteFrameData(sender, args[0]);
    }
    return true;
  }
}