package club.aurorapvp.datahandlers;

import club.aurorapvp.AuroraKits;
import club.aurorapvp.modules.Frame;
import java.io.File;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class FrameData {
  private static final File FILE = new File(AuroraKits.DATA_FOLDER, "/frames.yml");
  private static final YamlConfiguration framesData = YamlConfiguration.loadConfiguration(FILE);
  private final Frame frame;

  public FrameData(Frame frame) {
    this.frame = frame;

    if (getConfigSection() != null) {
      this.create();
    }
  }

  public ConfigurationSection getConfigSection() {
    return get().getConfigurationSection(frame.getName());
  }

  public Location getLocation() {
    return get().getLocation("frames." + frame.getName() + ".location");
  }

  public ItemStack getItem() {
    return get().getItemStack("frames." + frame.getName() + ".item");
  }

  public void save() {
    try {
      if (!FILE.exists()) {
        FILE.getParentFile().mkdirs();
        FILE.createNewFile();
      }
      get().save(FILE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void delete() {
    get().set("frames." + frame.getName(), null);
    save();
  }

  public void create() {
    if (!frame.exists()) {
      get().set("frames." + frame.getName() + ".item", frame.getItem());
      get().set("frames." + frame.getName() + ".location", frame.getLocation());
      save();
    }
  }

  public static YamlConfiguration get() {
    return framesData;
  }
}