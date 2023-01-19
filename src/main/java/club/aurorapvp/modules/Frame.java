package club.aurorapvp.modules;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.config.Lang;
import club.aurorapvp.datahandlers.FrameData;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Frame {
  private static final List<Frame> frames = new ArrayList<>();
  private Location location;
  private final String frameName;
  private FrameData frameData;
  private ItemStack item;

  public Frame(String frameName) {
    this.frameName = frameName;
    new FrameData(this);

    frames.add(this);

    if (this.exists()) {
      this.reload();
    }
  }

  public boolean exists() {
    return this.getData().getConfigSection() != null;
  }

  public FrameData getData() {
    return frameData;
  }

  public ItemStack getItem() {
    return item;
  }

  public String getName() {
    return frameName;
  }

  public Location getLocation() {
    return location;
  }

  public void create(Player p) {
    if (frameName != null && p != null) {
      Block block = p.getTargetBlock(4);
      ItemFrame frame = p.getWorld().spawn(block.getLocation(), ItemFrame.class);
      frame.teleport(block.getRelative(getBlockFace(p)).getLocation());
      frame.setFacingDirection(getBlockFace(p), true);

      frame.setItem(p.getInventory().getItemInMainHand());
      item = p.getInventory().getItemInMainHand();
      location = frame.getLocation();

      this.frameData = new FrameData(this);

      p.sendMessage(Lang.getComponent("frame-created"));
    } else if (p != null) {
      p.sendMessage(Lang.getComponent("frame-invalid"));
    }
  }

  public void delete(Player p) {
    this.getData().delete();
    if (p != null) {
      p.sendMessage(Lang.getComponent("frame-deleted"));
    }
  }

  public void reload() {
    location = this.getData().getLocation();
    item = this.getData().getItem();
  }

  public static void reloadAll() {
    for (Frame frame : frames) {
      frame.reload();
    }
  }

  public static void setup() {
    File file = new File(AuroraKits.DATA_FOLDER, "/frames.yml");
    YamlConfiguration framesData = YamlConfiguration.loadConfiguration(file);
    ConfigurationSection section = framesData.getConfigurationSection("frames");

    for (String name : section.getKeys(false)) {
      new Frame(name);
    }
  }

  public static Frame getItemFrame(Location loc) {
    for (Frame frame : frames) {
      if (frame.getLocation() == loc) {
        return frame;
      }
    }
    return null;
  }

  public static Frame getItemFrame(String frameName) {
    for (Frame frame : frames) {
      if (Objects.equals(frame.getName(), frameName)) {
        return frame;
      }
    }
    return null;
  }

  public static void onClicked(PlayerInteractEntityEvent event) {
    final Entity clicked = event.getRightClicked();
    if (clicked instanceof ItemFrame) {
      event.getPlayer().getInventory().addItem(Frame.getItemFrame(clicked.getLocation()).getItem());
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
}