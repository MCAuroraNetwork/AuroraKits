package club.aurorapvp.modules;

import static club.aurorapvp.config.LangHandler.getLangComponent;
import static club.aurorapvp.datahandlers.ItemFrameDataHandler.createFrameData;
import static club.aurorapvp.datahandlers.ItemFrameDataHandler.getFrameData;
import static club.aurorapvp.datahandlers.ItemFrameDataHandler.setFrameData;

import club.aurorapvp.datahandlers.ItemFrameDataHandler;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class itemFramesModule {

  public static void onFrameClicked(PlayerInteractEntityEvent event) {
    if (getFrameData() == null) {
      setFrameData();
    }

    final Entity clicked = event.getRightClicked();
    if (clicked instanceof ItemFrame) {

      if (ItemFrameDataHandler.getFrame(clicked.getLocation()) != null) {
        event.getPlayer().getInventory().addItem(
            getFrameData().getItemStack("frames." + ItemFrameDataHandler.getFrame(clicked.getLocation()) + ".item"));
        event.setCancelled(true);
      }
    }
  }

  public static void createFrame(CommandSender sender, String arg) {
    if (getFrameData() == null) {
      setFrameData();
    }

    if (arg != null && sender instanceof Player p) {

      Block b = p.getTargetBlock(4);
      ItemFrame frame = p.getWorld().spawn(b.getLocation(), ItemFrame.class);
      frame.teleport(b.getRelative(getFacingDirection(p)).getLocation());
      frame.setFacingDirection(getFacingDirection(p), true);

      frame.setItem(p.getInventory().getItemInMainHand());

      createFrameData(p, arg, frame);

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