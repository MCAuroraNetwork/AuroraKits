package club.aurorapvp.commands.plugin;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.configs.Config;
import club.aurorapvp.configs.Lang;
import club.aurorapvp.modules.Frame;
import club.aurorapvp.modules.GUI;
import club.aurorapvp.modules.Kit;
import java.util.Objects;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Reload implements CommandExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, @NotNull String[] args) {
    if (Objects.equals(args[0], "reload")) {
      long startTime = System.currentTimeMillis();

      Frame.reloadAll();
      Kit.reloadAll();
      Config.reload();
      GUI.reloadAll();
      Lang.reload();

      AuroraKits.LOGGER.info(
          "AuroraKits reloaded in " + (System.currentTimeMillis() - startTime) +
              "ms");
    }

    return true;
  }
}
