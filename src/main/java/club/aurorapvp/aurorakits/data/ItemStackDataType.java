package club.aurorapvp.aurorakits.data;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

// TODO this is terrible
public class ItemStackDataType implements PersistentDataType<byte[], ItemStack> {

  @Override
  public @NotNull Class<byte[]> getPrimitiveType() {
    return byte[].class;
  }

  @Override
  public @NotNull Class<ItemStack> getComplexType() {
    return ItemStack.class;
  }

  @Override
  public byte @NotNull [] toPrimitive(ItemStack complex, @NotNull PersistentDataAdapterContext context) {
    YamlConfiguration config = new YamlConfiguration();
    config.set("item", complex.serialize());
    return config.saveToString().getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public @NotNull ItemStack fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
    YamlConfiguration config = new YamlConfiguration();
    try {
      config.loadFromString(new String(primitive, StandardCharsets.UTF_8));
      Map<String, Object> itemMap;
      if (config.get("item") instanceof MemorySection) {
        itemMap = ((MemorySection) Objects.requireNonNull(config.get("item"))).getValues(false);
      } else {
        throw new IllegalStateException("Data is not an ItemStack.");
      }
      return ItemStack.deserialize(itemMap);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to convert byte array to ItemStack.", e);
    }
  }
}