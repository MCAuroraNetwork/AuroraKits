package club.aurorapvp.modules;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.configs.Lang;
import club.aurorapvp.datahandlers.FrameData;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
  private static final Set<Frame> frames = new HashSet<>();
  private Location location;
  private final String name;
  private final FrameData data;
  private ItemStack item;

  public Frame(String frameName) {
    this.name = frameName;
    this.data = new FrameData(this);

    frames.add(this);

    if (this.exists()) {
      this.reload();
    }
  }

  public static void init() {
    File file = new File(AuroraKits.DATA_FOLDER, "/frames.yml");
    YamlConfiguration framesData = YamlConfiguration.loadConfiguration(file);
    ConfigurationSection section = framesData.getConfigurationSection("frames");

    if (section == null) {
      return;
    }

    for (String name : section.getKeys(false)) {
      new Frame(name);
    }
    AuroraKits.LOGGER.info("All frames loaded!");
  }

  public ItemStack getItem() {
    return item;
  }

  public FrameData getData() {
    return data;
  }

  public String getName() {
    return name;
  }

  public Location getLocation() {
    return location;
  }

  public boolean exists() {
    return this.getData().getConfigSection() != null;
  }

  public void create(Player p) {
    if (name != null && p != null) {
      Block block = p.getTargetBlockExact(4);
      ItemFrame frame = p.getWorld().spawn(block.getLocation(), ItemFrame.class);
      frame.teleport(block.getRelative(getBlockFace(p)).getLocation());
      frame.setFacingDirection(getBlockFace(p), true);

      frame.setItem(p.getInventory().getItemInMainHand());
      item = p.getInventory().getItemInMainHand();
      location = frame.getLocation();

      this.data.create();

      p.sendMessage(Lang.getComponent("frame-created"));
    } else if (p != null) {
      p.sendMessage(Lang.getComponent("frame-invalid"));
    }
  }

  public void delete(Player p) {
    this.getData().delete();
    frames.remove(this);
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
    AuroraKits.LOGGER.info("All frames reloaded!");
  }

  public static Frame getFrame(Location loc) {
    for (Frame frame : frames) {
      if (frame.getLocation().equals(loc)) {
        return frame;
      }
    }
    return null;
  }

  public static Frame getFrame(String frameName) {
    for (Frame frame : frames) {
      if (Objects.equals(frame.getName(), frameName)) {
        return frame;
      }
    }
    return null;
  }

  public static Set<Frame> getFrames() {
    return frames;
  }

  public static void onClicked(PlayerInteractEntityEvent event) {
    final Entity clicked = event.getRightClicked();
    if (clicked instanceof ItemFrame) {
      Frame frame = Frame.getFrame(clicked.getLocation());
      if (frame != null) {
        event.getPlayer().getInventory().addItem(frame.getItem());
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
}