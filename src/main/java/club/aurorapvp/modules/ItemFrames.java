package club.aurorapvp.modules;

import club.aurorapvp.config.Lang;
import club.aurorapvp.datahandlers.ItemFrameData;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.io.IOException;

public class ItemFrames {

    public static void onClicked(PlayerInteractEntityEvent event) {
        if (ItemFrameData.get() == null) {
            try {
                ItemFrameData.reload();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        final Entity clicked = event.getRightClicked();
        if (clicked instanceof ItemFrame) {

            if (ItemFrameData.getFrame(clicked.getLocation()) != null) {
                event.getPlayer().getInventory().addItem(
                        ItemFrameData.get().getItemStack(
                                "frames." + ItemFrameData.getFrame(clicked.getLocation()) + ".item"));
                event.setCancelled(true);
            }
        }
    }

    public static void create(CommandSender sender, String frameName) {
        if (ItemFrameData.get() == null) {
            try {
                ItemFrameData.reload();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (frameName != null && sender instanceof Player p) {

            Block b = p.getTargetBlock(4);
            ItemFrame frame = p.getWorld().spawn(b.getLocation(), ItemFrame.class);
            frame.teleport(b.getRelative(getFacingDirection(p)).getLocation());
            frame.setFacingDirection(getFacingDirection(p), true);

            frame.setItem(p.getInventory().getItemInMainHand());

            ItemFrameData.create(p, frameName, frame);

            sender.sendMessage(Lang.getComponent("frame-created"));
        } else {
            sender.sendMessage(Lang.getComponent("frame-invalid"));
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