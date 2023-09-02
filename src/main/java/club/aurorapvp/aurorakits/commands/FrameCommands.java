package club.aurorapvp.aurorakits.commands;

import club.aurorapvp.aurorakits.modules.Frame;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import java.util.Objects;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

@CommandAlias("frames")
@CommandPermission("aurorakits.command.frames")
public class FrameCommands extends BaseCommand {

  @Subcommand("create")
  @CommandPermission("aurorakits.command.frames.create")
  @Description("Creates a new frame")
  @SuppressWarnings("unused")
  public void onCreate(Player player, String name) {
    Block block = player.getTargetBlockExact(10);
    assert block != null;
    ItemFrame frame = player.getWorld().spawn(block.getLocation(), ItemFrame.class);
    frame.teleport(
        block.getRelative(Objects.requireNonNull(Frame.getBlockFace(player))).getLocation());
    frame.setFacingDirection(Objects.requireNonNull(Frame.getBlockFace(player)), true);

    new Frame(frame).create(player, name);
  }

  @Subcommand("delete")
  @Description("Deletes a frame")
  @SuppressWarnings("unused")
  public void onDelete(Player player, String name) {
    Entity entity = player.getTargetEntity(10);

    if (!(entity instanceof ItemFrame itemFrame)) {
      return;
    }


    Frame.getFrame(itemFrame).delete(player);
  }
}
