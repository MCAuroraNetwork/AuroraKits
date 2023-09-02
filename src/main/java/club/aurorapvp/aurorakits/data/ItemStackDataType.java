package club.aurorapvp.aurorakits.data;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
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

  @SuppressWarnings("unchecked")
  @Override
  public @NotNull ItemStack fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
    YamlConfiguration config = new YamlConfiguration();
    try {
      config.loadFromString(new String(primitive, StandardCharsets.UTF_8));
      return ItemStack.deserialize((Map<String, Object>) Objects.requireNonNull(config.get("item")));
    } catch (Exception e) {
      throw new IllegalStateException("Failed to convert byte array to ItemStack.", e);
    }
  }
}