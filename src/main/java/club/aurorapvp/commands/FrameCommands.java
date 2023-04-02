package club.aurorapvp.commands;

import club.aurorapvp.configs.Lang;
import club.aurorapvp.modules.Frame;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("frame")
public class FrameCommands extends BaseCommand {
  @Subcommand("create")
  @CommandPermission("aurorakits.admin")
  @Description("Creates a new frame")
  public void onCreate(Player p, String name) {
    new Frame(name).create(p);
  }

  @Subcommand("delete")
  @CommandPermission("aurorakits.admin")
  @Description("Deletes a frame")
  public void onDelete(Player p, String name) {
    new Frame(name).delete(p);
  }

  @Default
  @CatchUnknown
  public void onUnknown(CommandSender sender) {
    sender.sendMessage(Lang.getComponent(""));
  }
}