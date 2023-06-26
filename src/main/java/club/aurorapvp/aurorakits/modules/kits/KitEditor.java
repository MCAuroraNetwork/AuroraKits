package club.aurorapvp.aurorakits.modules.kits;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitEditor {
  private final Player owner;
  private static final Set<KitEditor> KIT_EDITORS = new HashSet<>();
  private final Map<String, Inventory> kitEditors = new HashMap<>();

  public KitEditor(Player p) {
    this.owner = p;

    reload();

    KIT_EDITORS.add(this);
  }

  public Player getOwner() {
    return owner;
  }

  public boolean isEditorInventory(Inventory inventory) {
    for (Inventory editor : this.kitEditors.values()) {
      if (editor == inventory) {
        return true;
      }
    }
    return false;
  }

  public void openEditor(Player p, Kit kit) {
    p.openInventory(kitEditors.get(kit.getName()));
  }

  public void reload() {
    Set<Kit> kitSet = Kit.getKits(this.getOwner(), true);

    for (Kit kit : kitSet) {
      Inventory kitEditor = Bukkit.createInventory(null, 54, Component.text(kit.getName()));

      kitEditor.setContents(kit.getItemStacks());

      for (int i = 0; i < 35; i++) {
        kitEditor.setItem(i, kit.getItemStacks()[i]);
      }

      ItemStack barrier = new ItemStack(Material.BARRIER);
      ItemMeta meta = barrier.getItemMeta();
      meta.displayName(Component.text(""));
      barrier.setItemMeta(meta);

      for (int i = 36; i < 54; i++) {
        kitEditor.setItem(i, barrier);
      }

      for (int i = 46; i < 50; i++) {
        kitEditor.setItem(i, kit.getItemStacks()[i - 10]);
      }

      kitEditor.setItem(51, kit.getItemStacks()[40]);

      kitEditors.put(kit.getName(), kitEditor);
    }
  }

  public static void inventoryClicked(InventoryClickEvent event) {
    Player p = (Player) event.getWhoClicked();

    final ItemStack clickedItem = event.getCurrentItem();

    if (clickedItem != null && event.getCurrentItem().getType() == Material.BARRIER) {
      event.setCancelled(true);
      return;
    }

    if (!Objects.requireNonNull(KitEditor.getEditor(p)).isEditorInventory(event.getInventory())) {
      return;
    }

    ItemStack cursorItem = event.getCursor();

    if (cursorItem == null || cursorItem.getType().isAir()) {
      return;
    }

    if ((event.getSlot() == 46 && !cursorItem.getType().name().endsWith("_BOOTS")) ||
        event.getSlot() == 47 && !cursorItem.getType().name().endsWith("_LEGGINGS") ||
        event.getSlot() == 48 && (!cursorItem.getType().name().endsWith("_CHESTPLATE") &&
            cursorItem.getType() != Material.ELYTRA) ||
        event.getSlot() == 49 && !cursorItem.getType().name().endsWith("_HELMET")) {
      event.setCancelled(true);
    }

  }

  public static void inventoryClosed(InventoryCloseEvent event) {
    Player p = (Player) event.getPlayer();
    Inventory inventory = event.getInventory();

    if (!Objects.requireNonNull(KitEditor.getEditor(p)).isEditorInventory(inventory)) {
      return;
    }

    ItemStack[] itemStacks = new ItemStack[41];

    for (int i = 0; i <= 36; i++) {
      itemStacks[i] = inventory.getContents()[i];
    }

    for (int i = 46; i <= 50; i++) {
      itemStacks[i - 10] = inventory.getContents()[i];
    }

    itemStacks[40] = inventory.getContents()[51];

    Objects.requireNonNull(
            Kit.getKit(p, PlainTextComponentSerializer.plainText().serialize(event.getView().title())))
        .edit(p, itemStacks);
  }

  public static KitEditor getEditor(Player p) {
    for (KitEditor kitEditor : KIT_EDITORS) {
      if (kitEditor.getOwner() == p) {
        return kitEditor;
      }
    }
    return null;
  }
}
