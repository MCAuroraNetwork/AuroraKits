package club.aurorapvp.commands;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.configs.Config;
import club.aurorapvp.configs.Lang;
import club.aurorapvp.modules.Frame;
import club.aurorapvp.modules.KitsGUI;
import club.aurorapvp.modules.Kit;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;

@CommandAlias("aurorakits")
public class PluginCommands extends BaseCommand {
  @Subcommand("reload")
  @CommandPermission("aurorakits.admin")
  @Description("Reloads all plugin data")
  @SuppressWarnings("unused")
  public void onReload() {
    long startTime = System.currentTimeMillis();

    Frame.reloadAll();
    Kit.reloadAll();
    Config.reload();
    KitsGUI.reloadAll();
    Lang.reload();

    AuroraKits.INSTANCE.getLogger().info(
        "AuroraKits reloaded in " + (System.currentTimeMillis() - startTime) +
            "ms");
  }

  @Default
  @Subcommand("help")
  @Description("Lists all available plugin commands and their function")
  @CatchUnknown
  @SuppressWarnings("unused")
  public void onUnknown(CommandSender sender) {
    sender.sendMessage(Lang.getComponent("help-command"));
  }
}
