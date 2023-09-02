package club.aurorapvp.aurorakits.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Map;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

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
  public byte @NotNull [] toPrimitive(ItemStack complex,
      @NotNull PersistentDataAdapterContext context) {
    try {
      ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
      ObjectOutputStream objectOutStream = new ObjectOutputStream(byteOutStream);
      objectOutStream.writeObject(complex.serialize());
      return Base64.getEncoder().encode(byteOutStream.toByteArray());
    } catch (IOException e) {
      throw new IllegalStateException("Failed to convert ItemStack to byte array.", e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public @NotNull ItemStack fromPrimitive(byte @NotNull [] primitive,
      @NotNull PersistentDataAdapterContext context) {
    try {
      ByteArrayInputStream byteInStream = new ByteArrayInputStream(
          Base64.getDecoder().decode(primitive));
      ObjectInputStream objectInStream = new ObjectInputStream(byteInStream);
      Object object = objectInStream.readObject();
      if (object instanceof Map) {
        return ItemStack.deserialize((Map<String, Object>) object);
      } else {
        throw new IllegalStateException("Data is not an ItemStack.");
      }
    } catch (IOException | ClassNotFoundException e) {
      throw new IllegalStateException("Failed to convert byte array to ItemStack.", e);
    }
  }
}
