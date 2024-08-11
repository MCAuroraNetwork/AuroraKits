package club.aurorapvp.aurorakits.modules.kits;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.events.custom.KitSelectEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class KitsGUI implements Listener {

  private static final Map<UUID, KitsGUI> KITS_GUIS = new HashMap<>();
  private final Player player;
  private final List<Page> pages = new ArrayList<>();
  private int currentPage = 0;
  private int index;

  public KitsGUI(Player player) {
    this.player = player;

    reload();

    KITS_GUIS.put(player.getUniqueId(), this);

    Bukkit.getPluginManager().registerEvents(this, AuroraKits.getInstance());
  }

  public static void reloadAll() {
    for (KitsGUI kitsGui : KITS_GUIS.values()) {
      kitsGui.reload();
    }
    AuroraKits.getInstance().getLogger().info("GUIs Reloaded!");
  }

  @EventHandler
  public void onInventoryClicked(InventoryClickEvent event) {
    Player player = (Player) event.getWhoClicked();

    if (!(event.getInventory().getHolder() instanceof Page page)) {
      return;
    }

    KitsGUI gui = page.getGui();

    if (gui != this) {
      return;
    }

    event.setCancelled(true);

    if (event.getSlot() == 45) {
      gui.previousPage();
      return;
    } else if (event.getSlot() == 53) {
      gui.nextPage();
      return;
    } else if (event.getSlot() == 49) {
      new KitSelectEvent(player, null).callEvent();
      
      return;
    }

    final ItemStack clickedItem = event.getCurrentItem();

    if (clickedItem == null || clickedItem.getType().isAir()) {
      return;
    }

    if (event.getCurrentItem().getItemMeta().displayName() == null) {
      player.sendMessage(AuroraKits.getInstance().getLang().getComponent("kit-invalid-name"));
    } else {
      String itemName =
          PlainTextComponentSerializer.plainText().serialize(event.getCurrentItem().displayName());
      String kitName = itemName.substring(1, itemName.length() - 1);

      Kit kit = Kit.getKit(player, kitName);

      assert kit != null;
      if (event.isRightClick() && !kit.isPublic()) {
        kit.editKit(player);
      } else if (new KitSelectEvent(player, kit).callEvent()) {
        kit.giveKit(player);

        player.closeInventory();
      }
    }
  }

  public static KitsGUI getGUI(Player player) {
    return KITS_GUIS.getOrDefault(player.getUniqueId(), new KitsGUI(player));
  }

  public static void unregister(Player player) {
    KITS_GUIS.remove(player.getUniqueId());
  }

  public static Set<KitsGUI> getGuis() {
    return Set.copyOf(KITS_GUIS.values());
  }

  public Player getPlayer() {
    return player;
  }

  public void addKit(Kit kit) {
    Inventory lastPage = pages.get(index).getInventory();

    if (lastPage.getContents()[44] != null) {
      new Page(kit, this);
    } else {
      lastPage.addItem(kit.getDisplayItem());
    }
  }

  public void nextPage() {
    if (currentPage < index) {
      currentPage++;
    } else {
      currentPage = 0;
    }
    player.openInventory(KitsGUI.getGUI(player).getCurrentPage());
  }

  public void previousPage() {
    if (currentPage >= 1) {
      currentPage--;
    } else {
      currentPage = index;
    }
    player.openInventory(KitsGUI.getGUI(player).getCurrentPage());
  }

  public Inventory getCurrentPage() {
    return pages.get(currentPage).getInventory();
  }

  public void reload() {
    Set<Kit> kitSet = Kit.getKits(this.getPlayer(), false);

    kitSet.addAll(Kit.getPublicKits());

    index = -1;
    pages.clear();

    List<Kit> publicKits = new ArrayList<>();
    List<Kit> privateKits = new ArrayList<>();

    for (Kit kit : kitSet) {
      if (kit.isPublic()) {
        publicKits.add(kit);
      } else {
        privateKits.add(kit);
      }
    }

    publicKits.sort(Comparator.comparing(Kit::getName));
    privateKits.sort(Comparator.comparing(Kit::getName));

    List<Kit> sortedKits = new ArrayList<>(publicKits);
    sortedKits.addAll(privateKits);

    int pageSize = 45;
    int totalPages = (int) Math.ceil((double) sortedKits.size() / pageSize);

    for (int pageIndex = 0; pageIndex < totalPages; pageIndex++) {
      int start = pageIndex * pageSize;
      int end = Math.min(start + pageSize, sortedKits.size());

      List<Kit> kitsOnPage = sortedKits.subList(start, end);
      Kit[] kitsArray = kitsOnPage.toArray(new Kit[0]);

      pages.add(new Page(kitsArray, this));
    }
  }


  public static class Page implements InventoryHolder {

    private final Inventory inventory;
    private final KitsGUI gui;

    private Page(Kit[] kits, KitsGUI gui) {
      inventory = Bukkit.createInventory(this, 54,
          AuroraKits.getInstance().getLang().getComponent("kit-gui"));
      this.gui = gui;

      for (Kit kit : kits) {
        inventory.addItem(kit.getDisplayItem());
      }

      ItemMeta meta;

      ItemStack nextPage = new ItemStack(Material.PAPER);
      meta = nextPage.getItemMeta();
      meta.displayName(AuroraKits.getInstance().getLang().getComponent("next-page"));
      nextPage.setItemMeta(meta);

      ItemStack previousPage = new ItemStack(Material.PAPER);
      meta = previousPage.getItemMeta();
      meta.displayName(AuroraKits.getInstance().getLang().getComponent("previous-page"));
      previousPage.setItemMeta(meta);

      ItemStack noKit = new ItemStack(Material.BARRIER);
      meta = noKit.getItemMeta();
      meta.displayName(AuroraKits.getInstance().getLang().getComponent("no-kit"));
      noKit.setItemMeta(meta);

      inventory.setItem(49, noKit);
      inventory.setItem(45, nextPage);
      inventory.setItem(53, nextPage);
      gui.index++;
    }

    private Page(Kit kit, KitsGUI gui) {
      inventory = Bukkit.createInventory(this, 54,
          AuroraKits.getInstance().getLang().getComponent("kit-gui"));
      this.gui = gui;

      inventory.addItem(kit.getDisplayItem());

      ItemMeta meta;

      ItemStack nextPage = new ItemStack(Material.PAPER);
      meta = nextPage.getItemMeta();
      meta.displayName(AuroraKits.getInstance().getLang().getComponent("next-page"));
      nextPage.setItemMeta(meta);

      ItemStack previousPage = new ItemStack(Material.PAPER);
      meta = previousPage.getItemMeta();
      meta.displayName(AuroraKits.getInstance().getLang().getComponent("previous-page"));
      previousPage.setItemMeta(meta);

      inventory.setItem(45, nextPage);
      inventory.setItem(53, nextPage);
      gui.index++;
    }

    public KitsGUI getGui() {
      return gui;
    }

    @Override
    public @NotNull Inventory getInventory() {
      return inventory;
    }
  }
}
