package club.aurorapvp.listeners;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.configs.Config;
import club.aurorapvp.configs.Lang;
import club.aurorapvp.modules.Frame;
import club.aurorapvp.modules.GUI;
import club.aurorapvp.modules.Kit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor, TabCompleter {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, String[] args) {

    Player p = null;

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
          AuroraKits.INSTANCE.getLogger()
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
          new Kit("Kit" + Math.addExact(Kit.getAmount(p), 1), String.valueOf(p.getUniqueId())).create(p, false);
        } else {
          new Kit(args[0], p.getUniqueId().toString()).create(p, false);
        }
      }
      case "deletekit" -> {
        Kit kit = Kit.getKit(p, args[0]);
        if (kit != null) {
          kit.delete(p, false);
        } else {
          p.sendMessage(Lang.getComponent("kit-invalid-name"));
        }
      }
      case "deletepublickit" -> {
        Kit kit = Kit.getKit(p, args[0]);
        if (kit != null) {
          kit.delete(p, true);
        } else {
          p.sendMessage(Lang.getComponent("kit-invalid-name"));
        }
      }
      case "createpublickit" -> new Kit(args[0], "public").create(p, true);
      case "createframe" -> new Frame(args[0]).create(p);
      case "deleteframe" -> Frame.getItemFrame(args[0]).delete(p);
    }
    return true;
  }

  private final static List<String> results = new ArrayList<>();

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                    @NotNull String alias, String[] args) {
    Player p = null;
    results.clear();

    if (sender instanceof Player) {
      p = (Player) sender;
    }

    if (args.length == 1) {
      switch (cmd.getLabel()) {
        case "deletekit", "kit" -> {
          for (Kit kit : Kit.getKits(p)) {
            results.add(kit.getName());
          }
        }
        case "aurorakits" -> results.add("reload");
      }
    }

    Collections.sort(results);
    return results;
  }
}