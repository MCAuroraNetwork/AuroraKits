package club.aurorapvp.commands.kits;

import club.aurorapvp.configs.Lang;
import club.aurorapvp.modules.Kit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DeletePublicKit implements CommandExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player p)) {
      return false;
    }

    Kit kit = Kit.getKit(p, args[0]);
    if (kit != null) {
      kit.delete(p, true);
    } else {
      p.sendMessage(Lang.getComponent("kit-invalid-name"));
    }

    return false;
  }
}
