package club.aurorapvp.aurorakits.data;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.modules.kits.Kit;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.*;
import java.util.Base64;
import java.util.stream.Collectors;

public class KitDataHandler {

  private final Kit kit;
  private final MongoCollection<Document> collection; // resolved per‑kit
  private final String kitId; // = kitName, unique per collection

  public KitDataHandler(Kit kit) {
    this.kit = kit;

    MongoDatabase db = AuroraKits.getInstance().getDatabase();
    String collName = kit.isPublic() ? "kits.public" : "kits." + kit.getOwner().getUniqueId();
    this.collection = db.getCollection(collName);

    this.kitId = kit.getName();
  }

  private Document fetch() {
    return collection.find(Filters.eq("_id", kitId)).first();
  }

  public ItemStack getDisplayItem() {
    Document d = fetch();
    return d != null ? deserializeItemStack(d.get("displayItem")) : null;
  }

  public boolean isPublic() {
    return kit.isPublic();
  }

  public ItemStack[] getContents() {
    Document d = fetch();
    return d != null ? deserializeItemArray(d.get("contents")) : new ItemStack[0];
  }

  public ItemStack[] getArmor() {
    Document d = fetch();
    return d != null ? deserializeItemArray(d.get("armor")) : new ItemStack[0];
  }

  public ItemStack getOffhand() {
    Document d = fetch();
    return d != null ? deserializeItemStack(d.get("offhand")) : null;
  }

  public void create() {
    if (collection.countDocuments(Filters.eq("_id", kitId)) > 0) {
      throw new IllegalStateException("Kit named " + kitId + " already exists in this scope.");
    }

    Document doc =
        new Document("_id", kitId)
            .append("name", kit.getName())
            .append("isPublic", kit.isPublic())
            .append("owner", kit.getOwner().getUniqueId().toString())
            .append("displayItem", serializeItemStack(kit.getDisplayItem()))
            .append("contents", serializeItemArray(kit.getContents()))
            .append("armor", serializeItemArray(kit.getArmor()))
            .append("offhand", serializeItemStack(kit.getOffhand()));

    collection.insertOne(doc);
  }

  public boolean exists() {
    return collection.find(Filters.eq("_id", kitId)).first() != null;
  }

  public void delete() {
    collection.deleteOne(Filters.eq("_id", kitId));
  }

  private String serializeItemStack(ItemStack item) {
    if (item == null) return null;
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BukkitObjectOutputStream oos = new BukkitObjectOutputStream(bos)) {
      oos.writeObject(item);
      return Base64.getEncoder().encodeToString(bos.toByteArray());
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private ItemStack deserializeItemStack(Object data) {
    if (data == null) return null;
    if (data instanceof String s) {
      try (ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(s));
          BukkitObjectInputStream ois = new BukkitObjectInputStream(bis)) {
        return (ItemStack) ois.readObject();
      } catch (IOException | ClassNotFoundException ex) {
        ex.printStackTrace();
        return null;
      }
    }
    if (data instanceof Map<?, ?> map) { // legacy YAML support
      @SuppressWarnings("unchecked")
      Map<String, Object> legacy = (Map<String, Object>) map;
      return ItemStack.deserialize(legacy);
    }
    return null;
  }

  private List<Object> serializeItemArray(ItemStack[] items) {
    if (items == null) return null;
    return Arrays.stream(items).map(this::serializeItemStack).collect(Collectors.toList());
  }

  private ItemStack[] deserializeItemArray(Object data) {
    if (data == null) return new ItemStack[0];
    if (data instanceof List<?> list) {
      return list.stream().map(this::deserializeItemStack).toArray(ItemStack[]::new);
    }
    if (data instanceof Map<?, ?> map) { // legacy path
      List<ItemStack> ordered = new ArrayList<>();
      map.entrySet().stream()
          .sorted(Comparator.comparingInt(e -> Integer.parseInt(e.getKey().toString())))
          .forEachOrdered(e -> ordered.add(deserializeItemStack(e.getValue())));
      return ordered.toArray(new ItemStack[0]);
    }
    return new ItemStack[0];
  }
}
