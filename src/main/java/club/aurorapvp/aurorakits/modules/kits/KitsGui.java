package club.aurorapvp.aurorakits.modules.kits;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.config.Lang;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

public class KitsGui implements Listener {
  private static final Map<Player, KitsGui> KITS_GUIS = new HashMap<>();
  private final Player p;
  private Page[] pages;
  private int currentPage = 0;
  private int index;

  public KitsGui(Player p) {
    this.p = p;

    reload();

    KITS_GUIS.put(p, this);

    Bukkit.getPluginManager().registerEvents(this, AuroraKits.INSTANCE);
  }

  public static void reloadAll() {
    for (KitsGui kitsGui : KITS_GUIS.values()) {
      kitsGui.reload();
    }
    AuroraKits.INSTANCE.getLogger().info("GUIs Reloaded!");
  }

  public static void onPlayerQuit(Player p) {
    KITS_GUIS.remove(p);
  }

  @EventHandler
  public void onInventoryClicked(InventoryClickEvent event) {
    Player p = (Player) event.getWhoClicked();

    if (!(event.getInventory().getHolder() instanceof Page page)) {
      return;
    }

    KitsGui gui = page.getGui();

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
    }

    final ItemStack clickedItem = event.getCurrentItem();

    if (clickedItem == null || clickedItem.getType().isAir()) {
      return;
    }

    if (event.getCurrentItem().getItemMeta().displayName() == null) {
      p.sendMessage(Lang.getComponent("kit-invalid-name"));
    } else {
      String itemName =
          PlainTextComponentSerializer.plainText().serialize(event.getCurrentItem().displayName());
      String kitName = itemName.substring(1, itemName.length() - 1);

      Kit kit = Kit.getKit(p, kitName);

      assert kit != null;
      if (event.isRightClick() && !kit.isPublic()) {
        kit.editKit(p);
      } else {
        kit.giveKit(p);
      }
    }
  }

  public static KitsGui getGui(Player p) {
    return KITS_GUIS.get(p);
  }

  public static Set<KitsGui> getGuis() {
    return Set.copyOf(KITS_GUIS.values());
  }

  public static void open(Player p) {
    p.openInventory(Objects.requireNonNull(KitsGui.getGui(p)).getCurrentPage());
  }

  public Player getPlayer() {
    return p;
  }

  public void addKit(Kit kit) {
    Inventory lastPage = pages[index - 1].getInventory();

    if (lastPage.getContents()[44] != null) {
      new Page(kit, this);
    } else {
      lastPage.addItem(kit.getDisplayItem());
    }
  }

  public void removeKit(Kit kit) {
    for (Page page : pages) {
      for (ItemStack itemStack : page.getInventory().getContents()) {

        if (itemStack == null) {
          continue;
        }

        if (itemStack.isSimilar(kit.getDisplayItem())) {
          page.getInventory().remove(itemStack);
        }
      }
    }
  }

  public void nextPage() {
    if (currentPage <= index) {
      currentPage++;
    } else {
      currentPage = 0;
    }
    KitsGui.open(p);
  }

  public void previousPage() {
    if (currentPage >= 1) {
      currentPage--;
    } else {
      currentPage = index;
    }
    KitsGui.open(p);
  }

  public Inventory getCurrentPage() {
    return pages[currentPage].getInventory();
  }

  public void reload() {
    Set<Kit> kitSet = Kit.getKits(this.getPlayer(), true);

    for (int i = 0; i < kitSet.size(); i++) {
      if (i % 44 == 0 && i != 0) {
        index++;
      }
    }

    pages = new Page[index + 1];

    int currentPages = index + 1;

    for (int i = 0; i < currentPages; i++) {
      int numKits = kitSet.size() - i * 45;
      Kit[] kits = new Kit[Math.max(numKits, 0)];

      Iterator<Kit> iterator = kitSet.iterator();

      int counter = 0;

      while (iterator.hasNext()) {
        if (counter < i * 45) {
          iterator.next();
          counter++;
          continue;
        }

        if (counter - i * 45 >= kits.length) {
          break;
        }

        kits[counter - i * 45] = iterator.next();
        counter++;
      }

      pages[index] = new Page(kits, this);
      index++;
    }
  }

  public static class Page implements InventoryHolder {
    private final Inventory inventory;
    private final KitsGui gui;
    private Page(Kit[] kits, KitsGui gui) {
      inventory = Bukkit.createInventory(null, 54, Lang.getComponent("GUIName"));
      this.gui = gui;

      for (Kit kit : kits) {
        inventory.addItem(kit.getDisplayItem());
      }

      ItemMeta meta;

      ItemStack nextPage = new ItemStack(Material.PAPER);
      meta = nextPage.getItemMeta();
      meta.displayName(Lang.getComponent("next-page"));
      nextPage.setItemMeta(meta);

      ItemStack previousPage = new ItemStack(Material.PAPER);
      meta = previousPage.getItemMeta();
      meta.displayName(Lang.getComponent("previous-page"));
      previousPage.setItemMeta(meta);

      inventory.setItem(45, previousPage);
      inventory.setItem(53, nextPage);
    }

    private Page(Kit kit, KitsGui gui) {
      inventory = Bukkit.createInventory(this, 54, Lang.getComponent("GUIName"));
      this.gui = gui;

      inventory.addItem(kit.getDisplayItem());

      ItemMeta meta;

      ItemStack nextPage = new ItemStack(Material.PAPER);
      meta = nextPage.getItemMeta();
      meta.displayName(Lang.getComponent("next-page"));
      nextPage.setItemMeta(meta);

      ItemStack previousPage = new ItemStack(Material.PAPER);
      meta = previousPage.getItemMeta();
      meta.displayName(Lang.getComponent("previous-page"));
      previousPage.setItemMeta(meta);

      inventory.setItem(45, nextPage);
      inventory.setItem(53, nextPage);
    }

    public KitsGui getGui() {
      return gui;
    }

    @Override
    public @NotNull Inventory getInventory() {
      return inventory;
    }
  }
}
