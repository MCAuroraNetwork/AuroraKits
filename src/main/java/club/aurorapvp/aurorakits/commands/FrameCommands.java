package club.aurorapvp.aurorakits.commands;

import club.aurorapvp.aurorakits.config.Lang;
import club.aurorapvp.aurorakits.modules.Frame;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("frames")
public class FrameCommands extends BaseCommand {
  @Subcommand("create")
  @Description("Creates a new frame")
  @SuppressWarnings("unused")
  public void onCreate(Player p, String name) {
    new Frame(name).create(p);
  }

  @Subcommand("delete")
  @Description("Deletes a frame")
  @SuppressWarnings("unused")
  public void onDelete(Player p, String name) {
    new Frame(name).delete(p);
  }

  @Default
  @CatchUnknown
  @SuppressWarnings("unused")
  public void onUnknown(CommandSender sender) {
    sender.sendMessage(Lang.getComponent("unknown-frame-command"));
  }
}