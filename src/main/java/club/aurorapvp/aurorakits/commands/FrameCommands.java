package club.aurorapvp.aurorakits.commands;

import club.aurorapvp.aurorakits.modules.Frame;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;

@CommandAlias("frames")
@CommandPermission("aurorakits.command.frames")
public class FrameCommands extends BaseCommand {

  @Subcommand("create")
  @CommandPermission("aurorakits.command.frames.create")
  @Description("Creates a new frame")
  @SuppressWarnings("unused")
  public void onCreate(Player player, String name) {
    new Frame(name).create(player);
  }

  @Subcommand("delete")
  @Description("Deletes a frame")
  @SuppressWarnings("unused")
  public void onDelete(Player player, String name) {
    new Frame(name).delete(player);
  }
}
