package club.aurorapvp.aurorakits.modules.kits;

import club.aurorapvp.aurorakits.AuroraKits;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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

  private static final Map<UUID, KitEditor> KIT_EDITORS = new HashMap<>();
  private final Player player;
  private final Map<Kit, EditPage> editPages = new HashMap<>();

  public KitEditor(Player player) {
    this.player = player;

    reload();

    Bukkit.getPluginManager().registerEvents(this, AuroraKits.getInstance());
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!event.getWhoClicked().getUniqueId().equals(player.getUniqueId())) {
      return;
    }

    if (!(event.getInventory().getHolder() instanceof EditPage)) {
      return;
    }

    final ItemStack clickedItem = event.getCurrentItem();

    if (clickedItem != null && event.getCurrentItem().getType().equals(Material.BARRIER)) {
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
  public void onInventoryClose(InventoryCloseEvent event) {
    if (!event.getPlayer().getUniqueId().equals(player.getUniqueId())) {
      return;
    }

    Inventory inventory = event.getInventory();

    if (!(inventory.getHolder() instanceof EditPage editPage)) {
      return;
    }

    Player player = (Player) event.getPlayer();

    ItemStack[] contents = new ItemStack[36];
    ItemStack[] armor = new ItemStack[4];
    ItemStack offhand;

    for (int i = 0; i <= 35; i++) {
      contents[i] = inventory.getContents()[i];
    }

    int o = 0;
    for (int i = 46; i <= 49; i++) {
      armor[o] = inventory.getContents()[i];
      o++;
    }

    offhand = inventory.getContents()[51];

    if (editPage.isCopy()) {
      String newName = getUniqueCopyName(player, editPage.getOriginalKit().getName());
      Kit newKit = new Kit(newName, player, false);
      newKit.create(contents, armor, offhand, editPage.getOriginalKit().getDisplayItem().getType());
      player.sendMessage(AuroraKits.getInstance().getLang().formatComponent("kit-copied", newName));
    } else {
      editPage.getOriginalKit().edit(player, contents, armor, offhand);
    }
  }

  private String getUniqueCopyName(Player player, String baseName) {
    Set<String> existingNames = new HashSet<>();
    for (Kit kit : Kit.getPrivateKits(player)) {
      existingNames.add(kit.getName());
    }

    String name = baseName + " (copy)";
    int i = 1;
    while (existingNames.contains(name)) {
      name = baseName + " (copy " + i + ")";
      i++;
    }
    return name;
  }

  public static KitEditor getEditor(Player player) {
    return KIT_EDITORS.computeIfAbsent(player.getUniqueId(), k -> new KitEditor(player));
  }

  public static void register(Player player) {
    if (KIT_EDITORS.containsKey(player.getUniqueId())) {
      unregister(player);
    }
    KIT_EDITORS.put(player.getUniqueId(), new KitEditor(player));
  }

  public static void unregister(Player player) {
    KitEditor editor = KIT_EDITORS.remove(player.getUniqueId());
    if (editor != null) {
      org.bukkit.event.HandlerList.unregisterAll(editor);
    }
  }

  public void open(Player player, Kit kit) {
    player.openInventory(editPages.get(kit).getInventory());
  }

  public void openCopyEditor(Player player, Kit originalKit) {
    EditPage copyPage = new EditPage(originalKit, true);
    player.openInventory(copyPage.getInventory());
  }

  public void addKit(Kit kit) {
    editPages.put(kit, new EditPage(kit, false));
  }

  public void reload() {
    Set<Kit> kitSet = Kit.getKits(player, false);

    for (Kit kit : kitSet) {
      editPages.put(kit, new EditPage(kit, false));
    }
  }

  public static class EditPage implements InventoryHolder {

    private final Inventory inventory;
    private final Kit originalKit;
    private final boolean isCopy;

    private EditPage(Kit kit, boolean isCopy) {
      this.originalKit = kit;
      this.isCopy = isCopy;
      String title = isCopy ? "Creating copy of " + kit.getName() : "Editing " + kit.getName();
      inventory = Bukkit.createInventory(this, 54, Component.text(title));

      for (int i = 0; i <= kit.getContents().length - 1; i++) {
        inventory.setItem(i, kit.getContents()[i]);
      }

      ItemStack barrier = new ItemStack(Material.BARRIER);
      ItemMeta meta = barrier.getItemMeta();
      meta.displayName(Component.text(""));
      barrier.setItemMeta(meta);

      for (int i = 36; i <= 53; i++) {
        inventory.setItem(i, barrier);
      }

      int o = 0;
      for (int i = 46; i <= 45 + kit.getArmor().length; i++) {
        inventory.setItem(i, kit.getArmor()[o]);
        o++;
      }

      for (int i = 46 + kit.getArmor().length; i <= 49; i++) {
        inventory.clear(i);
      }

      inventory.setItem(51, kit.getOffhand());
    }

    public Kit getOriginalKit() {
      return originalKit;
    }

    public boolean isCopy() {
      return isCopy;
    }

    @Override
    @NotNull
    public Inventory getInventory() {
      return inventory;
    }
  }
}
