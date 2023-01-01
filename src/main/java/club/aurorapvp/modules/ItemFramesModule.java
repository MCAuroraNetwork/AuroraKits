package club.aurorapvp.modules;

import club.aurorapvp.config.LangHandler;
import club.aurorapvp.datahandlers.ItemFrameDataHandler;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.io.IOException;

public class ItemFramesModule {

    public static void onClicked(PlayerInteractEntityEvent event) {
        if (ItemFrameDataHandler.get() == null) {
            try {
                ItemFrameDataHandler.reload();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        final Entity clicked = event.getRightClicked();
        if (clicked instanceof ItemFrame) {

            if (ItemFrameDataHandler.getFrame(clicked.getLocation()) != null) {
                event.getPlayer().getInventory().addItem(
                        ItemFrameDataHandler.get().getItemStack(
                                "frames." + ItemFrameDataHandler.getFrame(clicked.getLocation()) + ".item"));
                event.setCancelled(true);
            }
        }
    }

    public static void create(CommandSender sender, String frameName) {
        if (ItemFrameDataHandler.get() == null) {
            try {
                ItemFrameDataHandler.reload();
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

            ItemFrameDataHandler.create(p, frameName, frame);

            sender.sendMessage(LangHandler.getComponent("frame-created"));
        } else {
            sender.sendMessage(LangHandler.getComponent("frame-invalid"));
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