package club.aurorapvp.aurorakits.data;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.modules.Frame;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

public class FrameDataHandler {

  private static final NamespacedKey key = new NamespacedKey(AuroraKits.INSTANCE, "frame");
  private final Frame frame;
  private final ItemFrame itemFrame;

  public FrameDataHandler(Frame frame) {
    this.frame = frame;
    this.itemFrame = frame.getItemFrame();
  }

  public boolean exists() {
    return itemFrame.getPersistentDataContainer().has(key);
  }

  public ItemStack getItem() {
    PersistentDataContainer container = itemFrame.getPersistentDataContainer();

    return container.get(key, new ItemStackDataType());
  }


  public void delete() {
    PersistentDataContainer container = itemFrame.getPersistentDataContainer();

    container.remove(key);
  }

  public void create() {
    PersistentDataContainer container = itemFrame.getPersistentDataContainer();

    container.set(key, new ItemStackDataType(), frame.getContents());
  }
}
