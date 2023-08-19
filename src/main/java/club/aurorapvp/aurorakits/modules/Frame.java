package club.aurorapvp.aurorakits.modules;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.config.Lang;
import club.aurorapvp.aurorakits.data.FrameDataHandler;
import club.aurorapvp.aurorakits.util.StringUtil;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.Component;
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
import org.bukkit.inventory.meta.ItemMeta;

public class Frame {
  private static final Set<Frame> frames = new HashSet<>();
  private final String name;
  private final FrameDataHandler data;
  private Location location;
  private ItemStack contents;

  public Frame(String frameName) {
    this.name = frameName;
    this.data = new FrameDataHandler(this);

    frames.add(this);

    if (this.exists()) {
      this.reload();
    }
  }

  public static void init() {
    File file = new File(AuroraKits.INSTANCE.getDataFolder(), "/frames.yml");
    YamlConfiguration framesData = YamlConfiguration.loadConfiguration(file);
    ConfigurationSection section = framesData.getConfigurationSection("frames");

    if (section == null) {
      return;
    }

    for (String name : section.getKeys(false)) {
      new Frame(name);
    }
    AuroraKits.INSTANCE.getLogger().info("All frames loaded!");
  }

  public static void reloadAll() {
    for (Frame frame : frames) {
      frame.reload();
    }
    AuroraKits.INSTANCE.getLogger().info("All frames reloaded!");
  }

  public static Frame getFrame(Location loc) {
    for (Frame frame : frames) {
      if (frame.getLocation().equals(loc)) {
        return frame;
      }
    }
    return null;
  }

  @SuppressWarnings("unused")
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
        event.getPlayer().getInventory().addItem(frame.getContents());
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

  public String getName() {
    return name;
  }

  public Location getLocation() {
    return location;
  }

  public boolean exists() {
    return this.getData().exists();
  }

  public void create(Player player) {
    if (name != null && player != null) {
      Block block = player.getTargetBlockExact(10);
      assert block != null;
      ItemFrame frame = player.getWorld().spawn(block.getLocation(), ItemFrame.class);
      frame.teleport(
          block.getRelative(Objects.requireNonNull(Frame.getBlockFace(player))).getLocation());
      frame.setFacingDirection(Objects.requireNonNull(Frame.getBlockFace(player)), true);

      contents = player.getInventory().getItemInMainHand();
      location = frame.getLocation();

      ItemStack displayItem = player.getInventory().getItemInMainHand();
      ItemMeta meta = displayItem.getItemMeta();
      meta.displayName(Component.text(StringUtil.convertToNormalTypedLanguage(name)));
      displayItem.setItemMeta(meta);

      frame.setItem(displayItem);

      this.data.create();

      player.sendMessage(Lang.getComponent("frame-created"));
    } else if (player != null) {
      player.sendMessage(Lang.getComponent("frame-invalid"));
    }
  }

  public void delete(Player player) {
    this.getData().delete();
    frames.remove(this);
    if (player != null) {
      player.sendMessage(Lang.getComponent("frame-deleted"));
    }
  }

  public void reload() {
    location = this.getData().getLocation();
    contents = this.getData().getItem();
  }
}
