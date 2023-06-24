package club.aurorapvp.aurorakits.modules;

import club.aurorapvp.aurorakits.AuroraKits;
import club.aurorapvp.aurorakits.configs.Lang;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitsGUI {
  private static final Set<KitsGUI> KITS_GUIS = new HashSet<>();
  private final Player owner;
  private Inventory[] inventories;
  private int currentPage = 0;
  private int pages;

  public KitsGUI(Player p) {
    this.owner = p;

    KITS_GUIS.add(this);
  }

  public Player getOwner() {
    return owner;
  }

  public void addKit(club.aurorapvp.aurorakits.modules.Kit kit) {
    Inventory lastPage = inventories[pages - 1];

    if (lastPage.getContents()[44] != null) {
      createPage(kit);
    } else {
      lastPage.addItem(kit.getDisplayItem());
    }
  }

  public void removeKit(club.aurorapvp.aurorakits.modules.Kit kit) {
    for (Inventory inventory : inventories) {
      for (ItemStack itemStack : inventory.getContents()) {

        if (itemStack == null) {
          continue;
        }

        if (itemStack.isSimilar(kit.getDisplayItem())) {
          inventory.remove(itemStack);
        }
      }
    }
  }

  public void createPage(club.aurorapvp.aurorakits.modules.Kit kit) {
    Inventory inventory = Bukkit.createInventory(null, 54, Lang.getComponent("GUIName"));
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

    inventories[pages - 1] = inventory;
    pages++;
  }

  public void createPage(club.aurorapvp.aurorakits.modules.Kit[] kits) {
    Inventory inventory = Bukkit.createInventory(null, 54, Lang.getComponent("GUIName"));

    for (club.aurorapvp.aurorakits.modules.Kit kit : kits) {
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

    inventory.setItem(45, nextPage);
    inventory.setItem(53, nextPage);

    inventories[pages] = inventory;
    pages++;
  }

  public void nextPage() {
    if (currentPage <= pages) {
      currentPage++;
    } else {
      currentPage = 0;
    }
    KitsGUI.open(owner);
  }

  public void previousPage() {
    if (currentPage >= 1) {
      currentPage--;
    } else {
      currentPage = pages;
    }
    KitsGUI.open(owner);
  }

  public Inventory getCurrentPage() {
    return inventories[currentPage];
  }

  public boolean isGuiInventory(Inventory inventory) {
    for (Inventory guiInventory : inventories) {
      if (inventory.equals(guiInventory)) {
        return true;
      }
    }
    return false;
  }

  public void reload() {
    Set<club.aurorapvp.aurorakits.modules.Kit> kitSet =
        club.aurorapvp.aurorakits.modules.Kit.getKits(this.getOwner(), true);

    for (int i = 0; i < kitSet.size(); i++) {
      if (i % 44 == 0 && i != 0) {
        pages++;
      }
    }

    inventories = new Inventory[pages + 1];

    int currentPages = pages + 1;

    for (int i = 0; i < currentPages; i++) {
      int numKits = kitSet.size() - i * 45;
      club.aurorapvp.aurorakits.modules.Kit[] kits =
          new club.aurorapvp.aurorakits.modules.Kit[Math.max(numKits, 0)];

      Iterator<club.aurorapvp.aurorakits.modules.Kit> iterator = kitSet.iterator();

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

      createPage(kits);
    }
  }

  public static void reloadAll() {
    for (KitsGUI kitsGui : KITS_GUIS) {
      kitsGui.reload();
    }
    AuroraKits.INSTANCE.getLogger().info("GUIs Reloaded!");
  }

  public static Set<KitsGUI> getGuis() {
    return KITS_GUIS;
  }

  public static void onPlayerQuit(Player p) {
    KITS_GUIS.removeIf(gui -> gui.getOwner() == p);
  }

  public static void inventoryClicked(InventoryClickEvent event) {
    Player p = (Player) event.getWhoClicked();

    if (Objects.requireNonNull(KitsGUI.getGui(p)).isGuiInventory(event.getInventory())) {
      event.setCancelled(true);

      if (event.getSlot() == 45) {
        Objects.requireNonNull(KitsGUI.getGui(p)).previousPage();
        return;
      } else if (event.getSlot() == 53) {
        Objects.requireNonNull(KitsGUI.getGui(p)).nextPage();
        return;
      }

      final ItemStack clickedItem = event.getCurrentItem();

      if (clickedItem == null || clickedItem.getType().isAir()) {
        return;
      }

      if (event.getCurrentItem().getItemMeta().displayName() == null) {
        p.sendMessage(Lang.getComponent("kit-invalid-name"));
      } else {
        String itemName = PlainTextComponentSerializer.plainText()
            .serialize(event.getCurrentItem().displayName());
        String kitName = itemName.substring(1, itemName.length() - 1);

        Objects.requireNonNull(Kit.getKit(kitName)).giveKit(p);
      }
    }
  }

  public static KitsGUI getGui(Player p) {
    for (KitsGUI kitsGui : KITS_GUIS) {
      if (kitsGui.getOwner() == p) {
        return kitsGui;
      }
    }
    return null;
  }

  public static void open(Player p) {
    p.openInventory(Objects.requireNonNull(KitsGUI.getGui(p)).getCurrentPage());
  }
}
