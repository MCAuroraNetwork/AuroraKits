package club.aurorapvp.listeners;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.config.Config;
import club.aurorapvp.config.Lang;
import club.aurorapvp.modules.Frame;
import club.aurorapvp.modules.GUI;
import club.aurorapvp.modules.Kit;
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
          long startTime = System.currentTimeMillis();
          Frame.reloadAll();
          Kit.reloadAll();
          Config.reload();
          GUI.reloadAll();
          Lang.reload();
          AuroraKits.PLUGIN.getLogger()
              .info("All reloaded in " + Math.subtractExact(System.currentTimeMillis(), startTime) + "ms");
        }
      }
      case "kit" -> {
        if (args.length != 0) {
          Kit kit = Kit.getKit(p, args[0]);
          if (kit != null) {
            kit.giveKit(p);
          } else {
            p.sendMessage(Lang.getComponent("kit-not-found"));
          }
        } else {
          GUI.open(p);
        }
      }
      case "kits" -> GUI.open(p);
      case "createkit" -> {
        if (args.length == 0) {
          new Kit(null, String.valueOf(p.getUniqueId())).create(p, 0);
        } else {
          new Kit(args[0], p.getUniqueId().toString()).create(p, 0);
        }
      }
      case "deletekit" -> {
        Kit kit = Kit.getKit(p, args[0]);
        if (kit != null) {
          kit.delete(p, 0);
        } else {
          p.sendMessage(Lang.getComponent("kit-invalid-name"));
        }
      }
      case "deletepublickit" -> {
        Kit kit = Kit.getKit(p, args[0]);
        if (kit != null) {
          kit.delete(p, 1);
        } else {
          p.sendMessage(Lang.getComponent("kit-invalid-name"));
        }
      }
      case "createpublickit" -> new Kit(args[0], "public").create(p, 1);
      case "createframe" -> new Frame(args[0]).create(p);
      case "deleteframe" -> Frame.getItemFrame(args[0]).delete(p);
    }
    return true;
  }
}