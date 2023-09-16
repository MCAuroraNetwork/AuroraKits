package club.aurorapvp.aurorakits.modules;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.config.Lang;
import club.aurorapvp.aurorakits.data.FrameDataHandler;
import club.aurorapvp.aurorakits.util.StringUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Frame {

  private static final Map<ItemFrame, Frame> frames = new HashMap<>();
  private final FrameDataHandler data;
  private final ItemFrame itemFrame;
  private ItemStack contents;

  public Frame(ItemFrame itemFrame) {
    this.itemFrame = itemFrame;
    this.data = new FrameDataHandler(this);

    if (this.exists()) {
      this.reload();

      frames.put(itemFrame, this);
    }
  }

  public static void init() {
    for (World world : Bukkit.getWorlds()) {
      for (Entity entity : world.getEntities()) {
        if (entity instanceof ItemFrame itemFrame) {
          new Frame(itemFrame);
        }
      }
    }
  }

  public static void reloadAll() {
    frames.clear();

    for (World world : Bukkit.getWorlds()) {
      for (Entity entity : world.getEntities()) {
        if (entity instanceof ItemFrame itemFrame) {
          new Frame(itemFrame);
        }
      }
    }

    AuroraKits.INSTANCE.getLogger().info("All frames reloaded!");
  }

  public static void register(ItemFrame itemFrame) {
    if (frames.containsKey(itemFrame)) {
      frames.replace(itemFrame, new Frame(itemFrame));
    }
  }

  public static void remove(ItemFrame itemFrame) {
    frames.remove(itemFrame);
  }

  public static Frame getFrame(ItemFrame itemFrame) {
    return frames.get(itemFrame);
  }

  public static Set<Frame> getFrames() {
    return Set.copyOf(frames.values());
  }

  public static void onClicked(PlayerInteractEntityEvent event) {
    final Entity clicked = event.getRightClicked();
    if (clicked instanceof ItemFrame itemFrame) {
      Frame frame = Frame.getFrame(itemFrame);
      if (frame != null) {
        event.getPlayer().getInventory().addItem(frame.getContents().clone());
        event.setCancelled(true);
      }
    }
  }

  public static BlockFace getBlockFace(Player player) {
    List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 100);

    if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isOccluding()) {
      return null;
    }

    Block targetBlock = lastTwoTargetBlocks.get(1);
    Block adjacentBlock = lastTwoTargetBlocks.get(0);

    return targetBlock.getFace(adjacentBlock);
  }

  public ItemStack getContents() {
    return contents;
  }

  public FrameDataHandler getData() {
    return data;
  }

  public Component getName() {
    return this.getContents().displayName();
  }

  public ItemFrame getItemFrame() {
    return itemFrame;
  }

  public boolean exists() {
    return this.getData().exists();
  }

  public void create(Player player, String name) {
    if (name != null && player != null) {
      contents = player.getInventory().getItemInMainHand().clone();

      ItemStack displayItem = player.getInventory().getItemInMainHand().clone();
      ItemMeta meta = displayItem.getItemMeta();
      meta.displayName(MiniMessage.miniMessage()
          .deserialize(StringUtil.convertToNormalTypedLanguage(name)));
      displayItem.setItemMeta(meta);

      itemFrame.setItem(displayItem);

      this.data.create();
      frames.put(itemFrame, this);

      player.sendMessage(Lang.getComponent("frame-created"));
    } else if (player != null) {
      player.sendMessage(Lang.getComponent("frame-invalid"));
    }
  }

  public void delete(Player player) {
    this.getData().delete();
    frames.remove(this.getItemFrame());
    if (player != null) {
      player.sendMessage(Lang.getComponent("frame-deleted"));
    }
  }

  public void reload() {
    contents = this.getData().getItem();
  }
}
