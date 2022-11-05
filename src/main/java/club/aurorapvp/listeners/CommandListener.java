package club.aurorapvp.listeners;

import static club.aurorapvp.config.ConfigHandler.reloadConfig;
import static club.aurorapvp.config.LangHandler.reloadLang;
import static club.aurorapvp.datahandlers.ItemFrameDataHandler.deleteFrameData;
import static club.aurorapvp.datahandlers.ItemFrameDataHandler.reloadFrameData;
import static club.aurorapvp.datahandlers.KitDataHandler.deleteKitData;
import static club.aurorapvp.modules.GUIModule.openGUI;
import static club.aurorapvp.modules.itemFramesModule.createFrame;
import static club.aurorapvp.modules.kitModule.createKit;
import static club.aurorapvp.modules.kitModule.getKit;

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
      case "aurorakits": {
        if (Objects.equals(args[0], "reload")) {
          reloadFrameData();
          reloadConfig();
          reloadLang();
        }
      }
      case "kit":
        if (args.length != 0) {
          getKit(Bukkit.getPlayer(sender.getName()), args[0]);
        } else {
          openGUI(p);
        }
        break;
      case "kits":
        openGUI(p);
        break;
      case "createkit":
        createKit(sender, args[0],
            String.valueOf(Bukkit.getPlayer(sender.getName()).getUniqueId()));
        break;
      case "deletekit":
        deleteKitData(sender, args[0],
            String.valueOf(Bukkit.getPlayer(sender.getName()).getUniqueId()));
        break;
      case "createpublickit":
        createKit(sender, args[0], "public");
        break;
      case "deletepublickit":
        deleteKitData(sender, args[0], "public");
        break;
      case "createframe":
        createFrame(sender, args[0]);
        break;
      case "deleteframe":
        deleteFrameData(sender, args[0]);
        break;
    }
    return true;
  }
}