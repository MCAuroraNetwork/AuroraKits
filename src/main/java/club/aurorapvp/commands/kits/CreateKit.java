package club.aurorapvp.commands.kits;

import club.aurorapvp.modules.Kit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CreateKit implements CommandExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player p)) {
      return false;
    }

    if (args.length == 0) {
      new Kit("Kit" + Math.addExact(Kit.getAmount(p, false), 1),
          String.valueOf(p.getUniqueId())).create(p, false);
    } else {
      new Kit(args[0], p.getUniqueId().toString()).create(p, false);
    }

    return false;
  }
}
