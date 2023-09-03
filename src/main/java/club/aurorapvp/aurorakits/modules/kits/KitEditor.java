package club.aurorapvp.aurorakits.modules.kits;

import club.aurorapvp.aurorakits.AuroraKits;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class KitEditor implements Listener {

  private static final Map<Player, KitEditor> KIT_EDITORS = new HashMap<>();
  private final Player player;
  private final Map<Kit, EditPage> kitEditors = new HashMap<>();

  public KitEditor(Player player) {
    this.player = player;

    reload();

    KIT_EDITORS.put(player, this);

    Bukkit.getPluginManager().registerEvents(this, AuroraKits.INSTANCE);
  }

  @EventHandler
  public void inventoryClicked(InventoryClickEvent event) {
    if (!(event.getInventory().getHolder() instanceof EditPage)) {
      return;
    }

    final ItemStack clickedItem = event.getCurrentItem();

    if (clickedItem != null && event.getCurrentItem().getType() == Material.BARRIER) {
      event.setCancelled(true);
      return;
    }

    ItemStack cursorItem = event.getCursor();

    if (cursorItem == null || cursorItem.getType().isAir()) {
      return;
    }

    if ((event.getSlot() == 46 && !cursorItem.getType().name().endsWith("_BOOTS"))
        || event.getSlot() == 47 && !cursorItem.getType().name().endsWith("_LEGGINGS")
        || event.getSlot() == 48
        && (!cursorItem.getType().name().endsWith("_CHESTPLATE")
        && cursorItem.getType() != Material.ELYTRA)
        || event.getSlot() == 49 && !cursorItem.getType().name().endsWith("_HELMET")) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void inventoryClosed(InventoryCloseEvent event) {
    Inventory inventory = event.getInventory();

    if (!(inventory.getHolder() instanceof EditPage editPage)) {
      return;
    }

    Player player = (Player) event.getPlayer();

    ItemStack[] contents = new ItemStack[36];
    ItemStack[] armor = new ItemStack[4];
    ItemStack offhand;

    for (int i = 0; i <= 36; i++) {
      contents[i] = inventory.getContents()[i];
    }

    int o = 0;
    for (int i = 46; i <= 50; i++) {
      armor[o] = inventory.getContents()[i];
    }

    offhand = inventory.getContents()[51];

    editPage.getKit().edit(player, contents, armor, offhand);
  }

  public static KitEditor getEditor(Player player) {
    return KIT_EDITORS.get(player);
  }

  public static void unregister(Player player) {
    KIT_EDITORS.remove(player);
  }

  public void open(Player player, Kit kit) {
    player.openInventory(kitEditors.get(kit).getInventory());
  }

  public void addKit(Kit kit) {
    kitEditors.put(kit, new EditPage(kit));
  }

  public void reload() {
    Set<Kit> kitSet = Kit.getKits(player, false);

    for (Kit kit : kitSet) {
      kitEditors.put(kit, new EditPage(kit));
    }
  }

  public static class EditPage implements InventoryHolder {

    private final Inventory inventory;
    private final Kit kit;

    private EditPage(Kit kit) {
      inventory = Bukkit.createInventory(this, 54, Component.text(kit.getName()));
      this.kit = kit;

      inventory.setContents(kit.getContents());

      for (int i = 0; i < 35; i++) {
        inventory.setItem(i, kit.getContents()[i]);
      }

      ItemStack barrier = new ItemStack(Material.BARRIER);
      ItemMeta meta = barrier.getItemMeta();
      meta.displayName(Component.text(""));
      barrier.setItemMeta(meta);

      for (int i = 36; i < 54; i++) {
        inventory.setItem(i, barrier);
      }

      int o = 0;
      for (int i = 46; i < 50; i++) {
        inventory.setItem(i, kit.getArmor()[o]);
        o++;
      }

      inventory.setItem(51, kit.getOffhand());
    }

    public Kit getKit() {
      return kit;
    }

    @Override
    @NotNull
    public Inventory getInventory() {
      return inventory;
    }
  }
}
