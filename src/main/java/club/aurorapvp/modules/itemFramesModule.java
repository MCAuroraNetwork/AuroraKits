package club.aurorapvp.modules;

import static club.aurorapvp.config.LangHandler.getLangComponent;
import static club.aurorapvp.datahandlers.ItemFrameDataHandler.createFrameData;
import static club.aurorapvp.datahandlers.ItemFrameDataHandler.framesData;
import static club.aurorapvp.datahandlers.ItemFrameDataHandler.getFrameData;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class itemFramesModule {

  public static void onFrameClicked(PlayerInteractEntityEvent event) {
    final Entity clicked = event.getRightClicked();
    if (clicked instanceof ItemFrame) {

      if (getFrameData(clicked.getLocation()) != null) {
        event.getPlayer().getInventory()
            .addItem(
                framesData.getItemStack("frames." + getFrameData(clicked.getLocation()) + ".item"));
        event.setCancelled(true);
      }
    }
  }

  public static void createFrame(CommandSender sender, String arg) {
    if (arg != null && sender instanceof Player) {
      Player p = Bukkit.getPlayer(sender.getName());

      Block b = p.getTargetBlock(4);
      ItemFrame frame = (ItemFrame) p.getWorld().spawn(b.getLocation(), ItemFrame.class);
      frame.teleport(b.getRelative(getFacingDirection(p)).getLocation());
      frame.setFacingDirection(getFacingDirection(p), true);

      frame.setItem(p.getActiveItem());

      createFrameData(arg, p, frame);

      sender.sendMessage(getLangComponent("frame-created"));
    } else {
      sender.sendMessage(getLangComponent("frame-invalid"));
    }
  }

  public static BlockFace getFacingDirection(Player p) {
    double rotation = (p.getLocation().getYaw()) % 360;
    if (rotation < 0) {
      rotation += 360.0;
    }
    if (0 <= rotation && rotation < 56.25) {
      return BlockFace.NORTH;
    } else if (56.25 <= rotation && rotation < 135) {
      return BlockFace.EAST;
    } else if (135 <= rotation && rotation < 230.625) {
      return BlockFace.SOUTH;
    } else if (230.625 <= rotation && rotation < 303.75) {
      return BlockFace.WEST;
    } else if (303.75 <= rotation && rotation < 360.0) {
      return BlockFace.NORTH;
    } else {
      return null;
    }
  }
}