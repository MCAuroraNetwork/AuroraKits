package club.aurorapvp.aurorakits.data;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.modules.kits.Kit;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.bson.Document;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class KitDataHandler {

  private final MongoCollection<Document> collection;
  private final Kit kit;
  private final String kitId;

  public KitDataHandler(Kit kit) {
    this.kit = kit;
    this.collection = AuroraKits.getInstance().getDatabase().getCollection("kits");
    this.kitId = kit.getOwner().getUniqueId() + "-" + kit.getName();
  }

  public ItemStack getDisplayItem() {
    Document kitData = collection.find(Filters.eq("_id", kitId)).first();
    if (kitData != null) {
      Map<String, Object> data = getMap(kitData, "displayItem");
      return deserializeItemStack(data);
    }
    return null;
  }

  public boolean isPublic() {
    Document kitData = collection.find(Filters.eq("_id", kitId)).first();
    return kitData != null && kitData.getBoolean("isPublic", false);
  }

  public ItemStack[] getContents() {
    Document kitData = collection.find(Filters.eq("_id", kitId)).first();
    if (kitData != null) {
      Map<String, Object> data = getMap(kitData, "contents");
      return deserializeItemArray(data);
    }
    return new ItemStack[0];
  }

  public ItemStack[] getArmor() {
    Document kitData = collection.find(Filters.eq("_id", kitId)).first();
    if (kitData != null) {
      Map<String, Object> data = getMap(kitData, "armor");
      return deserializeItemArray(data);
    }
    return new ItemStack[0];
  }

  public ItemStack getOffhand() {
    Document kitData = collection.find(Filters.eq("_id", kitId)).first();
    if (kitData != null) {
      Map<String, Object> data = getMap(kitData, "offhand");
      return deserializeItemStack(data);
    }
    return null;
  }

  public void create() {
    Document kitData = new Document()
            .append("_id", kitId)
            .append("isPublic", kit.isPublic())
            .append("owner", kit.getOwner().getUniqueId().toString())
            .append("displayItem", serializeItemStack(kit.getDisplayItem()))
            .append("contents", serializeItemArray(kit.getContents()))
            .append("armor", serializeItemArray(kit.getArmor()))
            .append("offhand", serializeItemStack(kit.getOffhand()));

    collection.updateOne(
            Filters.eq("_id", kitId),
            new Document("$set", kitData),
            new com.mongodb.client.model.UpdateOptions().upsert(true)
    );
  }

  public boolean exists() {
    return collection.find(Filters.eq("_id", kitId)).first() != null;
  }

  public void delete() {
    collection.deleteOne(Filters.eq("_id", kitId));
  }

  private Map<String, Object> serializeItemStack(ItemStack item) {
    return item != null ? ((ConfigurationSerializable) item).serialize() : null;
  }

  private ItemStack deserializeItemStack(Map<String, Object> data) {
    return data != null ? ItemStack.deserialize(data) : null;
  }

  private Map<String, Object> serializeItemArray(ItemStack[] items) {
    return items != null
            ? java.util.stream.IntStream.range(0, items.length)
            .boxed()
            .collect(Collectors.toMap(String::valueOf, i -> serializeItemStack(items[i])))
            : null;
  }

  private ItemStack[] deserializeItemArray(Map<String, Object> data) {
    if (data == null) {
      return new ItemStack[0];
    }
    return data.values().stream()
            .map(obj -> {
              if (obj instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) obj;
                return deserializeItemStack(map);
              }
              return null;
            })
            .toArray(ItemStack[]::new);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> getMap(Document doc, String key) {
    Object obj = doc.get(key);
    if (obj instanceof Map) {
      return new HashMap<>((Map<String, Object>) obj);
    }
    return null;
  }
}
