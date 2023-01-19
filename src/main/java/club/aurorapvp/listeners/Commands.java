package club.aurorapvp.listeners;

import club.aurorapvp.config.Config;
import club.aurorapvp.config.Lang;
import club.aurorapvp.modules.Frame;
import club.aurorapvp.modules.GUI;
import club.aurorapvp.modules.Kit;
import java.io.IOException;
import java.util.Objects;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {
  private Player p;

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, String[] args) {

    if (sender instanceof Player) {
      p = (Player) sender;
    }

    switch (command.getName()) {
      case "aurorakits" -> {
        if (Objects.equals(args[0], "reload")) {
          try {
            Frame.reloadAll();
            Kit.reloadAll();
            Config.reload();
            Lang.reload();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      }
      case "kit" -> {
        if (args.length != 0) {
          Kit.getKit(p, args[0]).giveKit(p);
        } else {
          GUI.open(p);
        }
      }
      case "kits" -> GUI.open(p);
      case "createkit" -> {
        if (args.length == 0) {
          new Kit(null).create(p, 0);
        } else {
          new Kit(args[0]).create(p, 0);
        }
      }
      case "deletekit", "deletepublickit" -> Kit.getKit(p, args[0]).delete(p);
      case "createpublickit" -> new Kit(args[0]).create(p, 1);
      case "createframe" -> new Frame(args[0]).create(p);
      case "deleteframe" -> Frame.getItemFrame(args[0]).delete(p);
    }
    return true;
  }
}