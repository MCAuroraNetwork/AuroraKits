package club.aurorapvp.aurorakits.data;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.modules.Frame;
import java.io.File;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class FrameDataHandler {
  private static final File FILE = new File(AuroraKits.INSTANCE.getDataFolder(), "/frames.yml");
  private static final YamlConfiguration framesData = YamlConfiguration.loadConfiguration(FILE);
  private final Frame frame;

  public FrameDataHandler(Frame frame) {
    this.frame = frame;
  }

  public static YamlConfiguration get() {
    return framesData;
  }

  public boolean exists() {
    return get().contains("frames." + frame.getName());
  }

  public Location getLocation() {
    return get().getLocation("frames." + frame.getName() + ".location");
  }

  public ItemStack getItem() {
    return get().getItemStack("frames." + frame.getName() + ".item");
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void save() {
    try {
      if (!FILE.exists()) {
        FILE.getParentFile().mkdirs();
        FILE.createNewFile();
      }
      get().save(FILE);
    } catch (IOException e) {
      AuroraKits.INSTANCE.getLogger().severe("Failed to save frame data");
    }
  }

  public void delete() {
    get().set("frames." + frame.getName(), null);
    save();
  }

  public void create() {
    get().set("frames." + frame.getName() + ".item", frame.getItem());
    get().set("frames." + frame.getName() + ".location", frame.getLocation());
    save();
  }
}