package club.aurorapvp.commands.kits;

import club.aurorapvp.modules.Kit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CreatePublicKit implements CommandExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player p)) {
      return false;
    }

    new Kit(args[0], "public").create(p, true);

    return true;
  }
}
