package club.aurorapvp.aurorakits.commands;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.config.Config;
import club.aurorapvp.aurorakits.config.Lang;
import club.aurorapvp.aurorakits.modules.Frame;
import club.aurorapvp.aurorakits.modules.kits.Kit;
import club.aurorapvp.aurorakits.modules.kits.KitsGUI;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("aurorakits")
@CommandPermission("aurorakits.command.aurorakits")
public class PluginCommands extends BaseCommand {

  @Subcommand("reload")
  @CommandPermission("aurorakits.command.aurorakits.reload")
  @Description("Reloads all plugin data")
  @SuppressWarnings("unused")
  public void onReload(Player player) {
    long startTime = System.currentTimeMillis();

    AuroraKits.getInstance().reloadConfig();
    AuroraKits.getInstance().getLang().reload();
    Frame.reloadAll();
    Kit.reloadAll();
    KitsGUI.reloadAll();

    player.sendMessage(AuroraKits.getInstance().getLang().getComponent("reloaded"));

    AuroraKits.getInstance()
        .getLogger()
        .info("AuroraKits reloaded in " + (System.currentTimeMillis() - startTime) + "ms");
  }

  @CatchUnknown
  @Subcommand("help")
  @CommandPermission("aurorakits.command.aurorakits.help")
  @Description("Lists all available plugin commands and their function")
  @SuppressWarnings("unused")
  public void onUnknown(CommandSender sender) {
    sender.sendMessage(AuroraKits.getInstance().getLang().getComponent("help-command"));
  }
}
